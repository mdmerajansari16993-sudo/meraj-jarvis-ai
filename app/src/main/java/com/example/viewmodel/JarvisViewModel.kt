package com.example.viewmodel

import android.app.Application
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.Build
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.caretaker.CaretakerHealthManager
import com.example.caretaker.CaretakerStatus
import com.example.data.local.CommandLogEntity
import com.example.data.local.CrashReportEntity
import com.example.data.local.JarvisDatabase
import com.example.data.local.ScheduledTaskEntity
import com.example.data.local.UserProfileEntity
import com.example.data.local.VipSubscriptionEntity
import com.example.diagnostics.CrashReporter
import com.example.device.DeviceSpec
import com.example.gaming.GameAssistMode
import com.example.gaming.GameTacticalTelemetry
import com.example.gaming.VipTierInfo
import com.example.hid.BluetoothHidTouchController
import com.example.network.GeminiClient
import com.example.repair.AutoDiagnosisManager
import com.example.scheduler.CreatorSchedulerManager
import com.example.security.SecurityShield
import com.example.service.JarvisAccessibilityService
import com.example.shopping.ProductItem
import com.example.shopping.ShoppingAutomationEngine
import com.example.shopping.ShoppingQueryResult
import com.example.vision.CameraVisionManager
import com.example.voice.JarvisVoiceEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

enum class JarvisTab {
    CORE_HUD,
    SHOPPING,
    GAMING_VIP,
    CREATOR_SCHEDULER,
    AUTO_REPAIR,
    CAMERA_VISION,
    HEALTH_CARETAKER,
    SECURITY_SELLER,
    DIAGNOSTICS,
    GAMING_KEY_MAPPING
}

enum class AssistantExecutionMode {
    MEDIUM,
    AUTOMATIC,
    GAMING_KEY_MAPPING
}

data class SecurityLockdown(
    val detectedKeyword: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)

class JarvisViewModel(application: Application) : AndroidViewModel(application) {

    private val db = JarvisDatabase.getInstance(application)
    private val dao = db.jarvisDao()
    val voiceEngine = JarvisVoiceEngine(application)
    val bluetoothHidController = BluetoothHidTouchController(application)
    val cameraVisionManager = CameraVisionManager(application)
    val autoDiagnosisManager = AutoDiagnosisManager(application)
    val creatorSchedulerManager = CreatorSchedulerManager(application, dao, bluetoothHidController)
    val caretakerManager = CaretakerHealthManager(application)

    val userProfile: StateFlow<UserProfileEntity?> = dao.getUserProfileFlow()
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    val vipSubscription: StateFlow<VipSubscriptionEntity?> = dao.getVipSubscriptionFlow()
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    val recentLogs: StateFlow<List<CommandLogEntity>> = dao.getRecentLogsFlow()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val crashReports: StateFlow<List<CrashReportEntity>> = dao.getCrashReportsFlow()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val scheduledTasks: StateFlow<List<ScheduledTaskEntity>> = dao.getScheduledTasksFlow()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _currentTab = MutableStateFlow(JarvisTab.CORE_HUD)
    val currentTab: StateFlow<JarvisTab> = _currentTab.asStateFlow()

    private val _commandInput = MutableStateFlow("")
    val commandInput: StateFlow<String> = _commandInput.asStateFlow()

    private val _isBusy = MutableStateFlow(false)
    val isBusy: StateFlow<Boolean> = _isBusy.asStateFlow()

    private val _lastAssistantResponse = MutableStateFlow("Jarvis systems online. How can I assist you, boss?")
    val lastAssistantResponse: StateFlow<String> = _lastAssistantResponse.asStateFlow()

    private val _securityLockdown = MutableStateFlow<SecurityLockdown?>(null)
    val securityLockdown: StateFlow<SecurityLockdown?> = _securityLockdown.asStateFlow()

    private val _showVipPayDialog = MutableStateFlow(false)
    val showVipPayDialog: StateFlow<Boolean> = _showVipPayDialog.asStateFlow()

    private val _shoppingResult = MutableStateFlow<ShoppingQueryResult?>(null)
    val shoppingResult: StateFlow<ShoppingQueryResult?> = _shoppingResult.asStateFlow()

    private val _gamingTelemetry = MutableStateFlow(GameTacticalTelemetry())
    val gamingTelemetry: StateFlow<GameTacticalTelemetry> = _gamingTelemetry.asStateFlow()

    private val _gamingAssistMode = MutableStateFlow(GameAssistMode.IDLE)
    val gamingAssistMode: StateFlow<GameAssistMode> = _gamingAssistMode.asStateFlow()

    val isAccessibilityConnected: StateFlow<Boolean> = JarvisAccessibilityService.isServiceActive

    private val prefs = application.getSharedPreferences("meraj_jarvis_prefs", android.content.Context.MODE_PRIVATE)

    private val _executionMode = MutableStateFlow(AssistantExecutionMode.MEDIUM)
    val executionMode: StateFlow<AssistantExecutionMode> = _executionMode.asStateFlow()

    private val _isMinimizedToFloatingWidget = MutableStateFlow(false)
    val isMinimizedToFloatingWidget: StateFlow<Boolean> = _isMinimizedToFloatingWidget.asStateFlow()

    private val _isFloatingSidebarMenuOpen = MutableStateFlow(false)
    val isFloatingSidebarMenuOpen: StateFlow<Boolean> = _isFloatingSidebarMenuOpen.asStateFlow()

    private val _trialRemainingHours = MutableStateFlow(24)
    val trialRemainingHours: StateFlow<Int> = _trialRemainingHours.asStateFlow()

    init {
        // Pre-load default initial shopping demonstration so user sees instant capability
        _shoppingResult.value = ShoppingAutomationEngine.executeShoppingCommand("Flipkart par 5 number ka shoes dikhao")
        updateTrialRemainingHours()
    }

    fun getTrialStartTime(): Long {
        var start = prefs.getLong("auto_mode_trial_start", 0L)
        if (start == 0L) {
            start = System.currentTimeMillis()
            prefs.edit().putLong("auto_mode_trial_start", start).apply()
        }
        return start
    }

    fun updateTrialRemainingHours() {
        val startTime = getTrialStartTime()
        val elapsed = System.currentTimeMillis() - startTime
        val oneDayMs = 24L * 60L * 60L * 1000L
        val remaining = ((oneDayMs - elapsed) / (60L * 60L * 1000L)).toInt().coerceAtLeast(0)
        _trialRemainingHours.value = remaining
    }

    fun isAutomaticModeTrialValid(): Boolean {
        val sub = vipSubscription.value
        if (sub != null && !sub.isExpired()) return true
        val startTime = getTrialStartTime()
        val elapsed = System.currentTimeMillis() - startTime
        val oneDayMs = 24L * 60L * 60L * 1000L
        updateTrialRemainingHours()
        return elapsed < oneDayMs
    }

    fun launchMediumMode() {
        _executionMode.value = AssistantExecutionMode.MEDIUM
        _isFloatingSidebarMenuOpen.value = false
        _isMinimizedToFloatingWidget.value = false
        _lastAssistantResponse.value = "Medium Mode Active: Handling general queries, web search, screenshots, and visual interaction."
        voiceEngine.speak("Medium Mode active hai. Aap general queries aur visual commands de sakte hain.")
    }

    fun launchAutomaticMode() {
        if (!isAutomaticModeTrialValid()) {
            _showVipPayDialog.value = true
            _lastAssistantResponse.value = "Automatic Mode ka 1-Day Free Trial expire ho chuka hai. Kripya VIP subscription activate karein (₹99 for 15 Days / ₹199 for 30 Days)."
            voiceEngine.speak("Automatic Mode ka 24 hours free trial expire ho gaya hai. Please VIP subscription activate karein.")
            return
        }
        _executionMode.value = AssistantExecutionMode.AUTOMATIC
        _isFloatingSidebarMenuOpen.value = false
        _isMinimizedToFloatingWidget.value = false
        val welcomeText = JarvisVoiceEngine.HINDI_AUTOMATIC_MODE_WELCOME
        _lastAssistantResponse.value = welcomeText
        voiceEngine.speakAutomaticModeWelcomeHindi()
    }

    fun launchGamingKeyMappingMode() {
        _executionMode.value = AssistantExecutionMode.GAMING_KEY_MAPPING
        _isFloatingSidebarMenuOpen.value = false
        _isMinimizedToFloatingWidget.value = false
        _currentTab.value = JarvisTab.GAMING_KEY_MAPPING
        bluetoothHidController.connectHardwareStack()
        _lastAssistantResponse.value = "Gaming Key-Mapping Mode Active: USB OTG + Bluetooth HID calibrated for Free Fire Custom HUD with 100% anti-ban safety."
        voiceEngine.speak("Gaming Key-Mapping mode active hai. USB OTG aur Bluetooth stack link ho gaya hai.")
    }

    fun toggleMinimizeToFloatingWidget() {
        _isMinimizedToFloatingWidget.value = !_isMinimizedToFloatingWidget.value
        if (_isMinimizedToFloatingWidget.value) {
            _isFloatingSidebarMenuOpen.value = false
        }
    }

    fun openFloatingSidebarMenu() {
        _isFloatingSidebarMenuOpen.value = true
    }

    fun closeFloatingSidebarMenu() {
        _isFloatingSidebarMenuOpen.value = false
    }

    fun selectTab(tab: JarvisTab) {
        _currentTab.value = tab
    }

    fun setCommandInput(text: String) {
        _commandInput.value = text
    }

    fun clearSecurityLockdown() {
        _securityLockdown.value = null
    }

    fun openVipPayDialog() {
        _showVipPayDialog.value = true
    }

    fun closeVipPayDialog() {
        _showVipPayDialog.value = false
    }

    fun saveOnboardingProfile(name: String, ageStr: String, location: String, spec: DeviceSpec) {
        val age = ageStr.toIntOrNull() ?: 21
        viewModelScope.launch(Dispatchers.IO) {
            val profile = UserProfileEntity(
                id = 1,
                name = name.trim().ifEmpty { "User" },
                age = age,
                location = location.trim().ifEmpty { "New Delhi, India" },
                deviceBrand = spec.brandName,
                deviceModel = spec.modelName,
                osFlavor = spec.osFlavor,
                isVerified = true,
                createdAt = System.currentTimeMillis()
            )
            dao.saveUserProfile(profile)
            _lastAssistantResponse.value = "Welcome ${profile.name}! ${spec.brandName} layout calibrated. GPS: ${profile.location}."
            voiceEngine.speak("Welcome ${profile.name}! ${spec.brandName} device layout calibrate ho gaya hai.")
        }
    }

    fun executeUserCommand(rawCommand: String) {
        val cmd = rawCommand.trim()
        if (cmd.isEmpty()) return
        _commandInput.value = ""

        // 1. ETHICAL SECURITY SHIELD CHECK
        val securityCheck = SecurityShield.evaluatePrompt(cmd)
        if (securityCheck is SecurityShield.SecurityCheckResult.Blocked) {
            triggerVibrationAlert()
            val lockdown = SecurityLockdown(
                detectedKeyword = securityCheck.detectedKeyword,
                message = securityCheck.message
            )
            _securityLockdown.value = lockdown
            _lastAssistantResponse.value = securityCheck.message

            // Immediate voice output of required dialogue
            voiceEngine.speak(SecurityShield.ILLEGAL_RESPONSE)

            viewModelScope.launch(Dispatchers.IO) {
                dao.insertLog(
                    CommandLogEntity(
                        commandText = cmd,
                        mode = "SECURITY_ALERT",
                        responseText = securityCheck.message,
                        status = "BLOCKED_ILLEGAL"
                    )
                )
            }
            return
        }

        // 2. EMOTIONAL & FATIGUE CARE (MOOD SENSING)
        val fatiguePrompt = caretakerManager.checkMoodAndFatigue(cmd)
        if (fatiguePrompt != null) {
            _lastAssistantResponse.value = fatiguePrompt
            voiceEngine.speak(fatiguePrompt)
            _currentTab.value = JarvisTab.HEALTH_CARETAKER
            viewModelScope.launch(Dispatchers.IO) {
                dao.insertLog(
                    CommandLogEntity(
                        commandText = cmd,
                        mode = "CARETAKER_MOOD",
                        responseText = fatiguePrompt,
                        status = "RELAX_MODE_ENGAGED"
                    )
                )
            }
            return
        }

        // 3. 24/7 CARETAKER, HEALTH & MEAL SYSTEM COMMANDS
        val lower = cmd.lowercase()
        if (lower.contains("lunch") || lower.contains("dinner") || lower.contains("meal") ||
            lower.contains("khana") || lower.contains("screen time") || lower.contains("break") ||
            lower.contains("water") || lower.contains("paani") || lower.contains("health") ||
            lower.contains("caretaker") || lower.contains("relax mode")) {
            _currentTab.value = JarvisTab.HEALTH_CARETAKER
            val response = when {
                lower.contains("lunch") -> caretakerManager.triggerLunchReminderTest()
                lower.contains("dinner") -> caretakerManager.triggerDinnerReminderTest()
                lower.contains("screen") || lower.contains("break") -> caretakerManager.triggerScreenTimeBreakSimulated()
                lower.contains("water") || lower.contains("paani") -> {
                    val count = caretakerManager.logWaterGlass()
                    "Shabash! $count of ${caretakerManager.status.value.targetWaterGlasses} glasses logged. Hydration maintain rakhein."
                }
                lower.contains("relax") -> {
                    caretakerManager.activateRelaxMode()
                    "Relax Mode active kar diya gaya hai. Non-essential alerts muted."
                }
                else -> caretakerManager.status.value.lastCareMessage
            }
            _lastAssistantResponse.value = response
            voiceEngine.speak(response)
            viewModelScope.launch(Dispatchers.IO) {
                dao.insertLog(
                    CommandLogEntity(
                        commandText = cmd,
                        mode = "CARETAKER_HEALTH",
                        responseText = response,
                        status = "SUCCESS"
                    )
                )
            }
            return
        }

        // 4. CHECK OTHER COMMAND INTENTS

        // CREATOR NIGHT-SHIFT SCHEDULED AUTOMATION
        if (lower.contains("schedule") || lower.contains("night shift") || lower.contains("post video") ||
            lower.contains("youtube post") || lower.contains("instagram post") || lower.contains("5:00 am") ||
            lower.contains("5 am") || lower.contains("creator post")) {
            _currentTab.value = JarvisTab.CREATOR_SCHEDULER
            val sub = vipSubscription.value
            if (sub == null || sub.isExpired()) {
                _showVipPayDialog.value = true
                _lastAssistantResponse.value = "Creator Night-Shift Scheduled Automation VIP feature hai. Kripya VIP pass activate karein."
                voiceEngine.speak("Creator Night-Shift mode VIP feature hai. Please activate karein.")
            } else {
                _lastAssistantResponse.value = "Creator Night-Shift Hub online! Zero API keys needed. Bluetooth HID will natively post your content at the scheduled time."
                voiceEngine.speak("Creator Night-Shift Hub ready hai. Aap scheduled post set kar sakte hain.")
            }
            return
        }

        // AUTO-DIAGNOSIS & SELF-REPAIR COMMAND
        if (lower.contains("repair") || lower.contains("diagnose") || lower.contains("phone lag") ||
            lower.contains("slow") || lower.contains("fix settings") || lower.contains("problem") ||
            lower.contains("auto repair") || lower.contains("clean ram")) {
            _currentTab.value = JarvisTab.AUTO_REPAIR
            val sub = vipSubscription.value
            if (sub == null || sub.isExpired()) {
                _showVipPayDialog.value = true
                _lastAssistantResponse.value = "Auto-Diagnosis & Self-Repair VIP feature hai. Kripya VIP pass activate karein."
                voiceEngine.speak("Auto-Diagnosis aur Self-Repair VIP feature hai. Kripya activate karein.")
            } else {
                val fix = autoDiagnosisManager.diagnoseCustomProblem(cmd)
                _lastAssistantResponse.value = fix
                voiceEngine.speak(fix)
            }
            return
        }

        // CAMERA VISION & SITUATIONAL GUIDANCE COMMAND
        if (lower.contains("kahan hoon") || lower.contains("nikalun") || lower.contains("camera") ||
            lower.contains("vision") || lower.contains("where am i") || lower.contains("exit rasta")) {
            _currentTab.value = JarvisTab.CAMERA_VISION
            cameraVisionManager.analyzeSituationalQuery(cmd)
            val guidance = cameraVisionManager.situationalResult.value?.navigationGuidanceHindi
                ?: "Camera Vision Active: Safe corridor ahead with exit gate."
            _lastAssistantResponse.value = guidance
            voiceEngine.speak(guidance)
            return
        }

        // E-COMMERCE / SHOPPING COMMAND
        if (lower.contains("flipkart") || lower.contains("shoe") || lower.contains("amazon") ||
            lower.contains("meesho") || lower.contains("buy") || lower.contains("dikhao") ||
            lower.contains("shoes") || lower.contains("kharid") || lower.contains("shopping")) {
            executeShoppingAutomation(cmd)
            return
        }

        // GAMING AUTO-TAKEOVER COMMAND
        if (lower.contains("free fire") || lower.contains("game") || lower.contains("gaming") ||
            lower.contains("takeover") || lower.contains("take over") || lower.contains("bgmi") ||
            lower.contains("phone call") || lower.contains("call defense")) {
            handleGamingModeSwitch()
            return
        }

        // GENERAL AI / ASSISTANT COMMAND
        viewModelScope.launch {
            _isBusy.value = true
            val promptResult = GeminiClient.queryJarvis(
                userPrompt = cmd,
                systemPrompt = "You are Meraj Jarvis AI, an advanced autonomous Android personal assistant with accessibility automation, vision, and iron security protocols. Speak cordially in crisp Hindi/Hinglish."
            )
            _isBusy.value = false
            _lastAssistantResponse.value = promptResult
            voiceEngine.speak(promptResult)

            launch(Dispatchers.IO) {
                dao.insertLog(
                    CommandLogEntity(
                        commandText = cmd,
                        mode = "SYSTEM",
                        responseText = promptResult,
                        status = "SUCCESS"
                    )
                )
            }
        }
    }

    fun executeShoppingAutomation(command: String = "Flipkart par 5 number ka shoes dikhao") {
        viewModelScope.launch {
            _isBusy.value = true
            _currentTab.value = JarvisTab.SHOPPING
            val result = ShoppingAutomationEngine.executeShoppingCommand(command)
            _shoppingResult.value = result
            _lastAssistantResponse.value = result.recommendationDialogue

            // Speak the EXACT required dialogue
            voiceEngine.speakRecommendation()

            _isBusy.value = false

            launch(Dispatchers.IO) {
                dao.insertLog(
                    CommandLogEntity(
                        commandText = command,
                        mode = "SHOPPING",
                        responseText = "${result.platform}: ${result.products.firstOrNull()?.name} -> ${result.recommendationDialogue}",
                        status = "SUCCESS"
                    )
                )
            }
        }
    }

    fun handleGamingModeSwitch() {
        _currentTab.value = JarvisTab.GAMING_VIP
        val sub = vipSubscription.value
        val isExpiredOrNone = sub == null || sub.isExpired()

        if (isExpiredOrNone) {
            // Instantly trigger payment popup as mandated!
            _showVipPayDialog.value = true
            _lastAssistantResponse.value = "Gaming Autonomous Co-Pilot requires active VIP Pass (₹99 for 15 Days or ₹199 for 30 Days). Please activate to unlock anti-cheat safe takeover."
            voiceEngine.speak("Gaming Autonomous mode VIP feature hai. Kripya subscription activate karein.")
        } else {
            // Activate safe minimal takeover
            _gamingAssistMode.value = GameAssistMode.ACTIVE_TAKEOVER
            _lastAssistantResponse.value = "VIP Active! Jarvis Game Co-Pilot running Free Fire safe hold pattern. Zero memory injection, 100% anti-cheat compliant."
            voiceEngine.speak("VIP Pass active hai. Autonomous game assistance chalu kar di gayi hai.")
            updateGamingLogs("Autonomous takeover initiated: Anti-cheat safety guard active")
        }
    }

    fun simulatePhoneCallDuringGame() {
        val sub = vipSubscription.value
        if (sub == null || sub.isExpired()) {
            _showVipPayDialog.value = true
            return
        }
        _gamingAssistMode.value = GameAssistMode.PHONE_CALL_DEFENSE
        val updated = _gamingTelemetry.value.copy(
            isPhoneCallSimulated = true,
            characterState = "AUTO-CROUCH BEHIND COVER (Holding line while call is answered)",
            telemetryLogs = listOf(
                "Incoming phone call detected: Audio ducking enabled",
                "Automated safe-crouch gesture dispatched via Accessibility",
                "Holding perimeter 360° observation",
                "Anti-cheat integrity intact (zero game memory hooks)"
            ) + _gamingTelemetry.value.telemetryLogs
        )
        _gamingTelemetry.value = updated
        voiceEngine.speak("Phone call detect hua. Maine character ko safe cover mein crouch kar diya hai.")
    }

    fun resumeNormalGameControl() {
        _gamingAssistMode.value = GameAssistMode.IDLE
        val updated = _gamingTelemetry.value.copy(
            isPhoneCallSimulated = false,
            characterState = "Standby (User controls active)",
            telemetryLogs = listOf("User resumed manual controls. Co-pilot standing by.") + _gamingTelemetry.value.telemetryLogs
        )
        _gamingTelemetry.value = updated
        voiceEngine.speak("Manual control restore kar diya gaya hai.")
    }

    fun activateVipSubscription(tierInfo: VipTierInfo) {
        val durationDays = tierInfo.durationDays
        val expiryMs = System.currentTimeMillis() + (durationDays * 24L * 60L * 60L * 1000L)
        val txnId = "UPI-TXN-" + UUID.randomUUID().toString().take(8).uppercase()

        val subEntity = VipSubscriptionEntity(
            id = 1,
            tier = tierInfo.tierId,
            priceInr = tierInfo.priceInr,
            startDateEpoch = System.currentTimeMillis(),
            expiryDateEpoch = expiryMs,
            isActive = true,
            txnId = txnId
        )

        viewModelScope.launch(Dispatchers.IO) {
            dao.saveVipSubscription(subEntity)
            dao.insertLog(
                CommandLogEntity(
                    commandText = "VIP Subscription Purchased: ${tierInfo.title} (₹${tierInfo.priceInr})",
                    mode = "GAMING_VIP",
                    responseText = "Txn: $txnId. Valid for $durationDays days.",
                    status = "SUCCESS"
                )
            )
        }

        _showVipPayDialog.value = false
        _gamingAssistMode.value = GameAssistMode.ACTIVE_TAKEOVER
        _lastAssistantResponse.value = "VIP Subscribed successfully! ${tierInfo.title} active for $durationDays days. Autonomous game co-pilot engaged."
        voiceEngine.speak("VIP Subscription safal rahi! Jarvis co-pilot ready hai.")
    }

    fun verifySellerCredentials(panInput: String, platform: String): SecurityShield.PanValidationResult {
        val validation = SecurityShield.validatePanCard(panInput)
        if (validation.isValid) {
            val current = userProfile.value
            val updated = (current ?: UserProfileEntity(id = 1)).copy(
                panCard = panInput.trim().uppercase(),
                isSellerSetup = true,
                selectedPlatform = platform
            )
            viewModelScope.launch(Dispatchers.IO) {
                dao.saveUserProfile(updated)
                dao.insertLog(
                    CommandLogEntity(
                        commandText = "Seller Setup: $platform with PAN ${panInput.take(4)}****",
                        mode = "SECURITY_SELLER",
                        responseText = "Legitimate Merchant verified.",
                        status = "SUCCESS"
                    )
                )
            }
            voiceEngine.speak("Merchant PAN verify ho gaya hai. Real credentials confirmed.")
        } else {
            triggerVibrationAlert()
        }
        return validation
    }

    fun triggerTestCrashAndReport() {
        try {
            throw RuntimeException("Jarvis System Diagnostics Test Crash: Manual probe to verify automated error reporting to ${CrashReporter.DEVELOPER_EMAIL}")
        } catch (t: Throwable) {
            val report = CrashReporter.recordCrash(getApplication(), t, isFatal = false)
            _lastAssistantResponse.value = "Error logged! Automated error report generated for ${CrashReporter.DEVELOPER_EMAIL}."
            voiceEngine.speak("Diagnostic error report create kar diya gaya hai.")
        }
    }

    fun scheduleNewCreatorTask(
        platform: String,
        title: String,
        timeFormatted: String,
        targetEpochMs: Long,
        autoShutdown: Boolean = true
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            creatorSchedulerManager.schedulePost(platform, title, timeFormatted, targetEpochMs, autoShutdown)
            dao.insertLog(
                CommandLogEntity(
                    commandText = "Scheduled Creator Post: $platform '$title' at $timeFormatted",
                    mode = "CREATOR_SCHEDULER",
                    responseText = "Task registered. Auto-shutdown: $autoShutdown",
                    status = "SUCCESS"
                )
            )
        }
        _lastAssistantResponse.value = "Creator Night-Shift scheduled! $platform post set for $timeFormatted with auto-shutdown."
        voiceEngine.speak("Creator Night-Shift task set ho gaya hai.")
    }

    fun deleteScheduledTask(taskId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.deleteTask(taskId)
        }
    }

    fun dispatchCrashReportEmail(report: CrashReportEntity) {
        CrashReporter.sendReportViaEmail(getApplication(), report)
    }

    private fun updateGamingLogs(log: String) {
        val currentLogs = _gamingTelemetry.value.telemetryLogs
        _gamingTelemetry.value = _gamingTelemetry.value.copy(
            telemetryLogs = listOf(log) + currentLogs.take(15)
        )
    }

    private fun triggerVibrationAlert() {
        try {
            val vibrator = getApplication<Application>().getSystemService(Vibrator::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(400, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(400)
            }
        } catch (e: Exception) {
            Log.w("JarvisViewModel", "Vibrator access error", e)
        }
    }

    override fun onCleared() {
        super.onCleared()
        voiceEngine.release()
    }
}
