package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1,
    val name: String = "",
    val age: Int = 0,
    val location: String = "",
    val deviceBrand: String = "Auto-Detect",
    val deviceModel: String = "",
    val osFlavor: String = "",
    val isVerified: Boolean = false,
    val panCard: String = "",
    val isSellerSetup: Boolean = false,
    val selectedPlatform: String = "Flipkart",
    val createdAt: Long = System.currentTimeMillis()
)
