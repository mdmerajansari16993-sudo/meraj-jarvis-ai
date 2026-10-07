package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vip_subscription")
data class VipSubscriptionEntity(
    @PrimaryKey val id: Int = 1,
    val tier: String = "NONE", // "15_DAYS", "30_DAYS", "NONE"
    val priceInr: Int = 0,     // 99 or 199
    val startDateEpoch: Long = 0L,
    val expiryDateEpoch: Long = 0L,
    val isActive: Boolean = false,
    val txnId: String = ""
) {
    fun isExpired(): Boolean {
        if (!isActive || expiryDateEpoch <= 0L) return true
        return System.currentTimeMillis() > expiryDateEpoch
    }

    fun remainingDays(): Long {
        if (!isActive) return 0L
        val diff = expiryDateEpoch - System.currentTimeMillis()
        if (diff <= 0L) return 0L
        return (diff / (1000L * 60 * 60 * 24)).coerceAtLeast(1L)
    }
}
