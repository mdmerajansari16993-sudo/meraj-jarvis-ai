package com.example.gaming

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.NeonGreenSuccess
import com.example.ui.theme.NeonRedAlert

enum class HudActionType {
    BUTTON,
    JOYSTICK,
    SIGHT_AIM,
    FIRE,
    UTILITY
}

enum class KeyMappingPreset(val displayName: String, val description: String) {
    FREE_FIRE_DEFAULT("Free Fire Standard HUD", "Optimized for 2-thumb Free Fire layout with WASD movement"),
    FREE_FIRE_CLAW_3FINGER("Free Fire 3-Finger Claw", "High-reflex layout with top index finger triggers for scope and jump"),
    BATTLE_ROYALE_PRO("Battle Royale Pro (Octopus Style)", "Full PC style layout with rapid weapon switching and sprint lock")
}

data class HudKeyMapping(
    val id: String,
    val keyLabel: String,
    val actionLabel: String,
    val relX: Float, // Normalized 0f..1f (X coordinate percentage)
    val relY: Float, // Normalized 0f..1f (Y coordinate percentage)
    val actionType: HudActionType = HudActionType.BUTTON,
    val colorHex: Long = 0xFF00E5FF,
    val radiusDp: Int = 36
)

object DefaultKeyMappings {

    fun getDefaultFreeFireMappings(): List<HudKeyMapping> {
        return listOf(
            HudKeyMapping(
                id = "move_joystick",
                keyLabel = "WASD",
                actionLabel = "Movement Joystick",
                relX = 0.18f,
                relY = 0.72f,
                actionType = HudActionType.JOYSTICK,
                colorHex = 0xFF00E5FF,
                radiusDp = 64
            ),
            HudKeyMapping(
                id = "fire_btn",
                keyLabel = "L-CLICK",
                actionLabel = "Fire / Shoot",
                relX = 0.82f,
                relY = 0.62f,
                actionType = HudActionType.FIRE,
                colorHex = 0xFFFF3B30,
                radiusDp = 48
            ),
            HudKeyMapping(
                id = "scope_btn",
                keyLabel = "R-CLICK",
                actionLabel = "Scope / Aim (360°)",
                relX = 0.88f,
                relY = 0.44f,
                actionType = HudActionType.SIGHT_AIM,
                colorHex = 0xFFFFD700,
                radiusDp = 44
            ),
            HudKeyMapping(
                id = "jump_btn",
                keyLabel = "SPACE",
                actionLabel = "Jump",
                relX = 0.90f,
                relY = 0.76f,
                actionType = HudActionType.BUTTON,
                colorHex = 0xFF00E676,
                radiusDp = 38
            ),
            HudKeyMapping(
                id = "crouch_btn",
                keyLabel = "C",
                actionLabel = "Crouch",
                relX = 0.80f,
                relY = 0.80f,
                actionType = HudActionType.BUTTON,
                colorHex = 0xFF00E5FF,
                radiusDp = 36
            ),
            HudKeyMapping(
                id = "prone_btn",
                keyLabel = "Z",
                actionLabel = "Prone",
                relX = 0.72f,
                relY = 0.84f,
                actionType = HudActionType.BUTTON,
                colorHex = 0xFF00E5FF,
                radiusDp = 34
            ),
            HudKeyMapping(
                id = "sprint_btn",
                keyLabel = "SHIFT",
                actionLabel = "Sprint / Run",
                relX = 0.28f,
                relY = 0.58f,
                actionType = HudActionType.BUTTON,
                colorHex = 0xFFFF9100,
                radiusDp = 36
            ),
            HudKeyMapping(
                id = "reload_btn",
                keyLabel = "R",
                actionLabel = "Reload",
                relX = 0.72f,
                relY = 0.54f,
                actionType = HudActionType.BUTTON,
                colorHex = 0xFFFFD700,
                radiusDp = 36
            ),
            HudKeyMapping(
                id = "weapon1_btn",
                keyLabel = "1",
                actionLabel = "Primary Gun",
                relX = 0.52f,
                relY = 0.84f,
                actionType = HudActionType.BUTTON,
                colorHex = 0xFF00E5FF,
                radiusDp = 34
            ),
            HudKeyMapping(
                id = "weapon2_btn",
                keyLabel = "2",
                actionLabel = "Secondary Gun",
                relX = 0.60f,
                relY = 0.84f,
                actionType = HudActionType.BUTTON,
                colorHex = 0xFF00E5FF,
                radiusDp = 34
            ),
            HudKeyMapping(
                id = "weapon3_btn",
                keyLabel = "3",
                actionLabel = "Melee / Pistol",
                relX = 0.68f,
                relY = 0.84f,
                actionType = HudActionType.BUTTON,
                colorHex = 0xFF00E5FF,
                radiusDp = 34
            ),
            HudKeyMapping(
                id = "medkit_btn",
                keyLabel = "4",
                actionLabel = "Medkit / Heal",
                relX = 0.12f,
                relY = 0.52f,
                actionType = HudActionType.UTILITY,
                colorHex = 0xFF00E676,
                radiusDp = 34
            ),
            HudKeyMapping(
                id = "loot_btn",
                keyLabel = "F",
                actionLabel = "Loot / Interact",
                relX = 0.76f,
                relY = 0.38f,
                actionType = HudActionType.UTILITY,
                colorHex = 0xFFFFD700,
                radiusDp = 34
            ),
            HudKeyMapping(
                id = "map_btn",
                keyLabel = "M",
                actionLabel = "Tactical Map",
                relX = 0.08f,
                relY = 0.18f,
                actionType = HudActionType.UTILITY,
                colorHex = 0xFF00E5FF,
                radiusDp = 34
            ),
            HudKeyMapping(
                id = "bag_btn",
                keyLabel = "TAB",
                actionLabel = "Backpack",
                relX = 0.10f,
                relY = 0.84f,
                actionType = HudActionType.UTILITY,
                colorHex = 0xFFFF9100,
                radiusDp = 34
            )
        )
    }

    fun getClaw3FingerMappings(): List<HudKeyMapping> {
        val base = getDefaultFreeFireMappings().toMutableList()
        // Reposition Jump and Scope to top-right corner for index finger tapping
        return base.map { mapping ->
            when (mapping.id) {
                "scope_btn" -> mapping.copy(relX = 0.88f, relY = 0.22f, keyLabel = "R-CLICK / E")
                "jump_btn" -> mapping.copy(relX = 0.78f, relY = 0.22f, keyLabel = "SPACE")
                "fire_btn" -> mapping.copy(relX = 0.84f, relY = 0.64f)
                else -> mapping
            }
        }
    }

    fun getProBattleRoyaleMappings(): List<HudKeyMapping> {
        val base = getDefaultFreeFireMappings().toMutableList()
        return base.map { mapping ->
            when (mapping.id) {
                "sprint_btn" -> mapping.copy(relX = 0.24f, relY = 0.50f, keyLabel = "SHIFT (Sprint Lock)")
                "fire_btn" -> mapping.copy(radiusDp = 52)
                else -> mapping
            }
        }
    }
}
