package com.example

import com.example.gaming.VipTierInfo
import com.example.security.SecurityShield
import com.example.shopping.ShoppingAutomationEngine
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testSecurityShield_BlocksIllegalCommands() {
        val prompt = "Hack wifi password and bypass network security"
        val result = SecurityShield.evaluatePrompt(prompt)
        assertTrue("Illegal prompt must be blocked", result is SecurityShield.SecurityCheckResult.Blocked)
        val blocked = result as SecurityShield.SecurityCheckResult.Blocked
        assertEquals(
            "Mujhe maaf karna, main is kaam mein aapki madad nahi kar sakta. Yeh illegal hai.",
            blocked.message
        )
    }

    @Test
    fun testSecurityShield_AllowsLegitimateShoppingCommand() {
        val prompt = "Flipkart par 5 number ka shoes dikhao"
        val result = SecurityShield.evaluatePrompt(prompt)
        assertTrue("Legitimate command must be safe", result is SecurityShield.SecurityCheckResult.Safe)
    }

    @Test
    fun testSecurityShield_PanCardValidation() {
        // Dummy PAN must be rejected
        val dummyResult = SecurityShield.validatePanCard("ABCDE1234F")
        assertFalse("Dummy PAN must not be accepted", dummyResult.isValid)

        // Invalid format must be rejected
        val invalidFormat = SecurityShield.validatePanCard("12345ABCDE")
        assertFalse("Invalid format must be rejected", invalidFormat.isValid)

        // Real structure with Individual category 'P'
        val validPan = SecurityShield.validatePanCard("BKZPA9876K")
        assertTrue("Valid structure PAN must pass", validPan.isValid)
    }

    @Test
    fun testShoppingAutomationEngine_ShoesQuery() {
        val result = ShoppingAutomationEngine.executeShoppingCommand("Flipkart par 5 number ka shoes dikhao")
        assertEquals("Flipkart", result.platform)
        assertEquals("5", result.detectedSize)
        assertEquals("Bhai yeh theek hai, isko buy kar do.", result.recommendationDialogue)
        assertTrue(result.products.isNotEmpty())
        assertTrue(result.products.any { it.isRecommended })
    }

    @Test
    fun testVipTiers_PricesAndDurations() {
        val tier15 = VipTierInfo.ALL_TIERS.firstOrNull { it.durationDays == 15 }
        val tier30 = VipTierInfo.ALL_TIERS.firstOrNull { it.durationDays == 30 }

        assertNotNull("15-day tier must exist", tier15)
        assertEquals(99, tier15?.priceInr)

        assertNotNull("30-day tier must exist", tier30)
        assertEquals(199, tier30?.priceInr)
    }

    @Test
    fun testCaretaker_HealthPrompts() {
        val fatigue = com.example.caretaker.CaretakerHealthManager.FATIGUE_RESPONSE
        assertTrue("Fatigue prompt contains required text", fatigue.contains("Chaliye thoda relax karte hain"))

        val screenBreak = com.example.caretaker.CaretakerHealthManager.SCREEN_BREAK_PROMPT
        assertTrue("Screen break prompt contains 2 ghante warning", screenBreak.contains("lagataar 2 ghante"))

        val lunch = com.example.caretaker.CaretakerHealthManager.LUNCH_REMINDER
        assertTrue("Lunch reminder contains 1 baj gaya", lunch.contains("dopahar ka 1 baj gaya"))

        val dinner = com.example.caretaker.CaretakerHealthManager.DINNER_REMINDER
        assertTrue("Dinner reminder contains raat ke 9", dinner.contains("raat ke 9 baj gaye"))
    }

    @Test
    fun testAutomaticMode_HindiWelcomeDialogue() {
        val expectedWelcome = "नमस्ते! मैं आपके साथ यहां ऑटोमेटिक काम करने के लिए रेडी हूं। आप जो बोलिएगा मैं वही ऑटोमेटिक कर दूंगा, आपको फोन को छूने की जरूरत नहीं है। बोलिए अब क्या करूं?"
        assertEquals(
            "Automatic mode welcome dialogue must match exact specification",
            expectedWelcome,
            com.example.voice.JarvisVoiceEngine.HINDI_AUTOMATIC_MODE_WELCOME
        )
    }

    @Test
    fun testGamingKeyMapping_PresetsAndAntiBan() {
        val defaultMappings = com.example.gaming.DefaultKeyMappings.getDefaultFreeFireMappings()
        assertTrue("Default mappings must contain movement joystick", defaultMappings.any { it.keyLabel == "WASD" })
        assertTrue("Default mappings must contain fire button", defaultMappings.any { it.keyLabel == "L-CLICK" })
        assertTrue("Default mappings must contain scope button", defaultMappings.any { it.keyLabel == "R-CLICK" })
        assertTrue("Default mappings must contain jump button", defaultMappings.any { it.keyLabel == "SPACE" })

        val clawMappings = com.example.gaming.DefaultKeyMappings.getClaw3FingerMappings()
        assertEquals(defaultMappings.size, clawMappings.size)

        val antiBanNotice = com.example.hid.BluetoothHidTouchController.ANTI_BAN_NOTICE
        assertTrue("Anti-ban notice must confirm physical hardware emulation", antiBanNotice.contains("Bluetooth HID operates as an external hardware controller"))
    }
}
