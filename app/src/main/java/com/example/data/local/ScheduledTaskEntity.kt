package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scheduled_tasks")
data class ScheduledTaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val platform: String, // "YouTube", "Instagram", "X / Twitter"
    val titleOrCaption: String,
    val scheduledTimeFormatted: String, // "05:00 AM"
    val scheduledEpochMs: Long,
    val mediaFileName: String = "creator_video_draft.mp4",
    val autoShutdownAfter: Boolean = true,
    val status: String = "SCHEDULED", // "SCHEDULED", "EXECUTED", "CANCELLED"
    val createdAt: Long = System.currentTimeMillis()
)
