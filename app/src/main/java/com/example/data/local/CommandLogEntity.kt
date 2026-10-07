package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "command_logs")
data class CommandLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val commandText: String,
    val mode: String, // "SHOPPING", "GAMING_VIP", "SYSTEM", "SECURITY_ALERT"
    val responseText: String,
    val status: String, // "SUCCESS", "BLOCKED_ILLEGAL", "ERROR"
    val timestamp: Long = System.currentTimeMillis()
)
