package com.example.gaming

enum class GameAssistMode {
    IDLE,
    ACTIVE_TAKEOVER,
    PHONE_CALL_DEFENSE,
    EMERGENCY_COVER_HOLD
}

data class GameTacticalTelemetry(
    val gameTitle: String = "Free Fire MAX",
    val characterState: String = "In Safe Cover (Prone & Guard)",
    val healthPercent: Int = 92,
    val zoneTimerSeconds: Int = 45,
    val isPhoneCallSimulated: Boolean = false,
    val callersName: String = "Incoming Call: Boss / Mom",
    val antiCheatNotice: String = "100% Anti-Cheat Safe (Accessibility-driven touch simulation only, zero memory injection)",
    val telemetryLogs: List<String> = emptyList()
)

data class VipTierInfo(
    val tierId: String,
    val title: String,
    val durationDays: Int,
    val priceInr: Int,
    val description: String,
    val popularTag: Boolean = false
) {
    companion object {
        val TIER_15_DAYS = VipTierInfo(
            tierId = "TIER_15_DAYS",
            title = "15 Days VIP Pass",
            durationDays = 15,
            priceInr = 99,
            description = "Unlocks full Autonomous Game Takeover, Phone Call Defense & Tactical Radar.",
            popularTag = false
        )

        val TIER_30_DAYS = VipTierInfo(
            tierId = "TIER_30_DAYS",
            title = "30 Days VIP Pass (Best Value)",
            durationDays = 30,
            priceInr = 199,
            description = "Complete monthly access with priority anti-cheat compliance updates & zero ads.",
            popularTag = true
        )

        val ALL_TIERS = listOf(TIER_15_DAYS, TIER_30_DAYS)
    }
}
