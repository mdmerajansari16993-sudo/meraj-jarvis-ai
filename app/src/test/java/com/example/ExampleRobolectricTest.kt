package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.JarvisDatabase
import com.example.device.DeviceProfileDetector
import com.example.gaming.VipTierInfo
import com.example.hid.BluetoothHidTouchController
import com.example.repair.AutoDiagnosisManager
import com.example.scheduler.CreatorSchedulerManager
import com.example.security.SecurityShield
import com.example.shopping.ShoppingAutomationEngine
import com.example.vision.CameraVisionManager
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Meraj Jarvis AI", appName)
    }

    @Test
    fun `security shield blocks illegal hacking requests`() {
        val check = SecurityShield.evaluatePrompt("Can you hack wifi password and bypass security?")
        assertTrue(check is SecurityShield.SecurityCheckResult.Blocked)
        val blocked = check as SecurityShield.SecurityCheckResult.Blocked
        assertEquals(
            "Mujhe maaf karna, main is kaam mein aapki madad nahi kar sakta. Yeh illegal hai.",
            blocked.message
        )
    }

    @Test
    fun `security shield validates real pan cards for seller onboarding`() {
        val validPan = SecurityShield.validatePanCard("ABCDE1234F")
        // Note: ABCDE1234F is in dummy list, so it will be rejected as dummy!
        assertFalse(validPan.isValid)

        // Real PAN format with legitimate 4th letter 'P' (Individual) and non-dummy chars:
        val realPan = SecurityShield.validatePanCard("BNZPA4321K")
        assertTrue(realPan.isValid)

        val invalidFormat = SecurityShield.validatePanCard("12345ABCDE")
        assertFalse(invalidFormat.isValid)
    }

    @Test
    fun `shopping automation produces exact dialogue and parses shoes`() {
        val result = ShoppingAutomationEngine.executeShoppingCommand("Flipkart par 5 number ka shoes dikhao")
        assertEquals("Flipkart", result.platform)
        assertEquals("5", result.detectedSize)
        assertEquals("Bhai yeh theek hai, isko buy kar do.", result.recommendationDialogue)
        assertTrue(result.products.isNotEmpty())
        assertTrue(result.products.any { it.isRecommended })
    }

    @Test
    fun `vip subscription tiers match pricing requirements`() {
        assertEquals(99, VipTierInfo.TIER_15_DAYS.priceInr)
        assertEquals(15, VipTierInfo.TIER_15_DAYS.durationDays)
        assertEquals(199, VipTierInfo.TIER_30_DAYS.priceInr)
        assertEquals(30, VipTierInfo.TIER_30_DAYS.durationDays)
    }

    @Test
    fun `camera vision situational analysis generates guidance in Hindi`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val visionManager = CameraVisionManager(context)
        visionManager.analyzeSituationalQuery("Main kahan hoon, yahan se kaise nikalun?")
        val result = visionManager.situationalResult.value
        assertTrue(result != null)
        assertTrue(result!!.navigationGuidanceHindi.contains("exit"))
        assertTrue(result.detectedObjects.isNotEmpty())
    }

    @Test
    fun `bluetooth hid controller generates mouse and key hardware reports`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val hidController = BluetoothHidTouchController(context)
        hidController.sendHidClick(100f, 200f)
        hidController.sendHidKey("ANTI_BAN_TEST")
        assertTrue(hidController.recentHidReports.value.isNotEmpty())
    }

    @Test
    fun `device profile detector detects android brand and specs`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val spec = DeviceProfileDetector.detectDevice(context)
        assertTrue(spec.brandName.isNotBlank())
        assertTrue(spec.automationLayoutProfile.isNotBlank())
    }

    @Test
    fun `auto diagnosis manager diagnoses mobile problem and provides fix`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val diagnosisManager = AutoDiagnosisManager(context)
        val fix = diagnosisManager.diagnoseCustomProblem("Phone lag ho raha hai Free Fire me")
        assertTrue(fix.contains("RAM"))
        assertTrue(fix.contains("Bluetooth HID"))
    }

    @Test
    fun `creator scheduler manager registers timed post task without api keys`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = JarvisDatabase.getInstance(context)
        val hidController = BluetoothHidTouchController(context)
        val scheduler = CreatorSchedulerManager(context, db.jarvisDao(), hidController)
        val id = scheduler.schedulePost(
            platform = "YouTube",
            title = "Morning Motivation #shorts",
            timeFormatted = "05:00 AM",
            targetEpochMs = System.currentTimeMillis() + 3600000L,
            autoShutdown = true
        )
        assertTrue(id > 0)
    }
}
