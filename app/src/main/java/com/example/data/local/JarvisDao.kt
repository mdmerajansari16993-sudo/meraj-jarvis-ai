package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface JarvisDao {
    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    fun getUserProfileFlow(): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    suspend fun getUserProfile(): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUserProfile(profile: UserProfileEntity)

    @Query("SELECT * FROM vip_subscription WHERE id = 1 LIMIT 1")
    fun getVipSubscriptionFlow(): Flow<VipSubscriptionEntity?>

    @Query("SELECT * FROM vip_subscription WHERE id = 1 LIMIT 1")
    suspend fun getVipSubscription(): VipSubscriptionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveVipSubscription(subscription: VipSubscriptionEntity)

    @Query("SELECT * FROM command_logs ORDER BY id DESC LIMIT 50")
    fun getRecentLogsFlow(): Flow<List<CommandLogEntity>>

    @Insert
    suspend fun insertLog(log: CommandLogEntity)

    @Query("DELETE FROM command_logs")
    suspend fun clearLogs()

    @Query("SELECT * FROM crash_reports ORDER BY id DESC LIMIT 30")
    fun getCrashReportsFlow(): Flow<List<CrashReportEntity>>

    @Insert
    suspend fun insertCrashReport(report: CrashReportEntity): Long

    @Query("UPDATE crash_reports SET isReported = 1 WHERE id = :reportId")
    suspend fun markCrashReported(reportId: Long)

    @Query("SELECT * FROM scheduled_tasks ORDER BY scheduledEpochMs ASC")
    fun getScheduledTasksFlow(): Flow<List<ScheduledTaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScheduledTask(task: ScheduledTaskEntity): Long

    @Query("UPDATE scheduled_tasks SET status = :status WHERE id = :taskId")
    suspend fun updateTaskStatus(taskId: Long, status: String)

    @Query("DELETE FROM scheduled_tasks WHERE id = :taskId")
    suspend fun deleteTask(taskId: Long)
}
