package com.example.caretaker

import android.content.Context
import android.os.SystemClock
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Calendar
import java.util.Locale

data class CaretakerStatus(
    val continuousScreenMinutes: Int = 18,
    val isScreenTimeBreakTriggered: Boolean = false,
    val isRelaxModeActive: Boolean = false,
    val areNonEssentialAlertsMuted: Boolean = false,
    val waterGlassesLogged: Int = 5,
    val targetWaterGlasses: Int = 8,
    val lastCareMessage: String = "All vital health parameters optimal. Stay hydrated, boss!",
    val isLunchDue: Boolean = false,
    val isDinnerDue: Boolean = false
)

class CaretakerHealthManager(private val context: Context) {

    private val sessionStartTime = SystemClock.elapsedRealtime()
    private val _status = MutableStateFlow(CaretakerStatus())
    val status: StateFlow<CaretakerStatus> = _status.asStateFlow()

    companion object {
        const val FATIGUE_RESPONSE =
            "Bhai, aaj aapka din lamba raha aur aap thake hue lag rahe hain. Chaliye thoda relax karte hain, main saare non-essential alerts mute kar deti hoon."

        const val SCREEN_BREAK_PROMPT =
            "Bhai, aap lagataar 2 ghante se zyada phone use kar rahe hain. Thoda break le lijiye, thoda paani pee lijiye aur aankhon ko aaram dijiye."

        const val LUNCH_REMINDER =
            "Bhai, dopahar ka 1 baj gaya hai. Kaam chhodkar lunch kar lijiye, khana time par khana zaroori hai!"

        const val DINNER_REMINDER =
            "Bhai, raat ke 9 baj gaye hain. Dinner ka samay ho gaya hai, fresh ho jaiye aur khana kha lijiye."

        private val FATIGUE_KEYWORDS = listOf(
            "thak gaya",
            "thaki hui",
            "bohot thaka",
            "bahut thak",
            "tired",
            "exhausted",
            "stress",
            "headache",
            "sar dard",
            "din lamba",
            "lamba din",
            "heavy day",
            "neend aa rahi",
            "burnout",
            "rest chahiye",
            "tension ho rahi",
            "bore ho gaya",
            "energy low",
            "so tired"
        )
    }

    /**
     * Senses user mood and fatigue from input commands or tone.
     * Returns a gentle adaptation message if fatigue is detected, or null if normal.
     */
    fun checkMoodAndFatigue(userCommand: String): String? {
        val lower = userCommand.lowercase(Locale.ROOT)
        val isFatigued = FATIGUE_KEYWORDS.any { lower.contains(it) }

        if (isFatigued) {
            activateRelaxMode()
            return FATIGUE_RESPONSE
        }
        return null
    }

    fun activateRelaxMode() {
        _status.value = _status.value.copy(
            isRelaxModeActive = true,
            areNonEssentialAlertsMuted = true,
            lastCareMessage = "Relax Mode ACTIVE: Non-essential alerts muted. Ambient calming glow enabled."
        )
    }

    fun deactivateRelaxMode() {
        _status.value = _status.value.copy(
            isRelaxModeActive = false,
            areNonEssentialAlertsMuted = false,
            lastCareMessage = "Standard productivity alerts restored."
        )
    }

    /**
     * Checks continuous screen time. If > 120 min, prompts gentle break.
     */
    fun checkScreenTimeBreak(): String? {
        val elapsedMinutes = ((SystemClock.elapsedRealtime() - sessionStartTime) / (1000 * 60)).toInt()
        val currentMinutes = _status.value.continuousScreenMinutes + elapsedMinutes
        if (currentMinutes >= 120 && !_status.value.isScreenTimeBreakTriggered) {
            _status.value = _status.value.copy(
                continuousScreenMinutes = currentMinutes,
                isScreenTimeBreakTriggered = true,
                lastCareMessage = SCREEN_BREAK_PROMPT
            )
            return SCREEN_BREAK_PROMPT
        }
        return null
    }

    fun triggerScreenTimeBreakSimulated(): String {
        _status.value = _status.value.copy(
            continuousScreenMinutes = 125,
            isScreenTimeBreakTriggered = true,
            lastCareMessage = SCREEN_BREAK_PROMPT
        )
        return SCREEN_BREAK_PROMPT
    }

    fun resetScreenTimeTimer() {
        _status.value = _status.value.copy(
            continuousScreenMinutes = 0,
            isScreenTimeBreakTriggered = false,
            lastCareMessage = "Screen-time refreshed! Great job taking care of your eyes."
        )
    }

    /**
     * Evaluates clock time for Lunch (1:00 PM) and Dinner (9:00 PM) reminders.
     */
    fun checkMealReminders(): String? {
        val calendar = Calendar.getInstance()
        val hour24 = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        // Lunch window around 1:00 PM (13:00 - 13:59)
        if (hour24 == 13) {
            _status.value = _status.value.copy(isLunchDue = true, lastCareMessage = LUNCH_REMINDER)
            return LUNCH_REMINDER
        }

        // Dinner window around 9:00 PM (21:00 - 21:59)
        if (hour24 == 21) {
            _status.value = _status.value.copy(isDinnerDue = true, lastCareMessage = DINNER_REMINDER)
            return DINNER_REMINDER
        }

        return null
    }

    fun triggerLunchReminderTest(): String {
        _status.value = _status.value.copy(isLunchDue = true, lastCareMessage = LUNCH_REMINDER)
        return LUNCH_REMINDER
    }

    fun triggerDinnerReminderTest(): String {
        _status.value = _status.value.copy(isDinnerDue = true, lastCareMessage = DINNER_REMINDER)
        return DINNER_REMINDER
    }

    fun logWaterGlass(): Int {
        val newCount = (_status.value.waterGlassesLogged + 1).coerceAtMost(12)
        _status.value = _status.value.copy(
            waterGlassesLogged = newCount,
            lastCareMessage = "Hydration updated: $newCount / ${_status.value.targetWaterGlasses} glasses logged."
        )
        return newCount
    }
}
