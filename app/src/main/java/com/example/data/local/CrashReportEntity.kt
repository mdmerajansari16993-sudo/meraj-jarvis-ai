package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "crash_reports")
data class CrashReportEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val errorMessage: String,
    val stackTrace: String,
    val deviceInfo: String,
    val timestamp: Long = System.currentTimeMillis(),
    val recipientEmail: String = "mdmerajansari16993@gmail.com",
    val isReported: Boolean = false
)
