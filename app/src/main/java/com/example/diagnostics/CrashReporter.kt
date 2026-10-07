package com.example.diagnostics

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import com.example.data.local.CrashReportEntity
import com.example.data.local.JarvisDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object CrashReporter {

    const val DEVELOPER_EMAIL = "mdmerajansari16993@gmail.com"
    private const val TAG = "MerajJarvisCrashReporter"

    fun initGlobalHandler(context: Context) {
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            try {
                recordCrash(context, throwable, isFatal = true)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to record uncaught exception", e)
            } finally {
                defaultHandler?.uncaughtException(thread, throwable)
            }
        }
    }

    fun recordCrash(context: Context, throwable: Throwable, isFatal: Boolean = false): CrashReportEntity {
        val sw = StringWriter()
        val pw = PrintWriter(sw)
        throwable.printStackTrace(pw)
        val stackTrace = sw.toString()
        val message = throwable.message ?: throwable.javaClass.simpleName

        val deviceInfo = buildString {
            append("Device: ${Build.MANUFACTURER} ${Build.MODEL} (${Build.DEVICE})\n")
            append("Android OS: ${Build.VERSION.RELEASE} (SDK ${Build.VERSION.SDK_INT})\n")
            append("Fatal: $isFatal\n")
            append("Thread: ${Thread.currentThread().name}")
        }

        val report = CrashReportEntity(
            errorMessage = message,
            stackTrace = stackTrace,
            deviceInfo = deviceInfo,
            timestamp = System.currentTimeMillis(),
            recipientEmail = DEVELOPER_EMAIL,
            isReported = false
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = JarvisDatabase.getInstance(context)
                db.jarvisDao().insertCrashReport(report)
            } catch (e: Exception) {
                Log.e(TAG, "DB error while storing crash report", e)
            }
        }

        return report
    }

    fun createEmailIntent(report: CrashReportEntity): Intent {
        val dateStr = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            .format(Date(report.timestamp))

        val emailSubject = "[MERAJ JARVIS AI BUG REPORT] Critical Error - $dateStr"
        val emailBody = buildString {
            append("=== AUTOMATED BUG REPORT FOR MERAJ JARVIS AI ===\n\n")
            append("To Developer: $DEVELOPER_EMAIL\n")
            append("Time: $dateStr\n")
            append("Error: ${report.errorMessage}\n\n")
            append("--- DEVICE TELEMETRY ---\n")
            append("${report.deviceInfo}\n\n")
            append("--- STACK TRACE ---\n")
            append(report.stackTrace)
            append("\n==============================================")
        }

        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(DEVELOPER_EMAIL))
            putExtra(Intent.EXTRA_SUBJECT, emailSubject)
            putExtra(Intent.EXTRA_TEXT, emailBody)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        return intent
    }

    fun sendReportViaEmail(context: Context, report: CrashReportEntity) {
        val intent = createEmailIntent(report)
        try {
            val chooser = Intent.createChooser(intent, "Send Error Log via Email to Meraj Jarvis Dev")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooser)
        } catch (e: Exception) {
            Log.e(TAG, "No email client found to send report", e)
        }
    }
}
