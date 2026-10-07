package com.example

import android.app.Application
import android.util.Log
import com.example.diagnostics.CrashReporter

class JarvisApp : Application() {

    override fun onCreate() {
        super.onCreate()
        try {
            CrashReporter.initGlobalHandler(this)
            Log.d("JarvisApp", "Meraj Jarvis AI Application initialized with automated error reporter.")
        } catch (e: Exception) {
            Log.e("JarvisApp", "Failed to initialize global crash reporter", e)
        }
    }
}
