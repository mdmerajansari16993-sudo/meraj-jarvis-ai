package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.ArcReactorOrb
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceHighlight
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.NeonGreenSuccess
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.AssistantExecutionMode
import com.example.viewmodel.JarvisTab
import com.example.viewmodel.JarvisViewModel

@Composable
fun CoreHudScreen(
    viewModel: JarvisViewModel,
    modifier: Modifier = Modifier
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val commandInput by viewModel.commandInput.collectAsState()
    val isBusy by viewModel.isBusy.collectAsState()
    val lastResponse by viewModel.lastAssistantResponse.collectAsState()
    val vipSub by viewModel.vipSubscription.collectAsState()
    val isAccessibilityActive by viewModel.isAccessibilityConnected.collectAsState()
    val isSpeaking by viewModel.voiceEngine.isSpeaking.collectAsState()
    val isListening by viewModel.voiceEngine.isListening.collectAsState()
    val executionMode by viewModel.executionMode.collectAsState()
    val trialHours by viewModel.trialRemainingHours.collectAsState()

    val quickCommands = listOf(
        "Bohot thak gaya hoon, sar dard hai (Mood Sensing)",
        "Lunch reminder check (1:00 PM)",
        "Dinner reminder check (9:00 PM)",
        "Screen-time 2-hour break check",
        "Flipkart par 5 number ka shoes dikhao",
        "Post video on YouTube at 5:00 AM (Night-Shift)",
        "Diagnose phone lag & auto-repair settings",
        "Main kahan hoon, yahan se kaise nikalun?",
        "Free Fire auto gaming takeover (Bluetooth HID)",
        "Amazon / Flipkart seller setup"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 90.dp)
    ) {
        // Hero HUD Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.jarvis_hero_hud_1791313622273),
                contentDescription = "Meraj Jarvis AI Hero HUD",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                DarkBackground.copy(alpha = 0.85f),
                                DarkBackground
                            )
                        )
                    )
            )

            // Header telemetry
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp)
                    .align(Alignment.BottomStart)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "MERAJ JARVIS AI",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                color = CyanPrimary,
                                letterSpacing = 2.sp
                            )
                        )
                        Text(
                            text = "Autonomous Android Assistant • v2.4",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            color = CyanPrimary.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier
                                .border(1.dp, CyanPrimary.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                                .clickable { viewModel.toggleMinimizeToFloatingWidget() }
                                .testTag("minimize_power_btn")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PowerSettingsNew,
                                    contentDescription = "Minimize to Floating Widget",
                                    tint = CyanPrimary,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "MINIMIZE",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = CyanPrimary,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }

                        Surface(
                            color = if (vipSub != null && !vipSub!!.isExpired()) GoldAccent.copy(alpha = 0.2f) else DarkSurfaceHighlight,
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier
                                .border(
                                    1.dp,
                                    if (vipSub != null && !vipSub!!.isExpired()) GoldAccent else DarkSurfaceHighlight,
                                    RoundedCornerShape(20.dp)
                                )
                                .clickable {
                                    if (vipSub == null || vipSub!!.isExpired()) {
                                        viewModel.openVipPayDialog()
                                    }
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (vipSub != null && !vipSub!!.isExpired()) "VIP ACTIVE (${vipSub!!.remainingDays()}d)" else "VIP: UPGRADE",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (vipSub != null && !vipSub!!.isExpired()) GoldAccent else CyanPrimary,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Center Arc Reactor Visualizer
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            ArcReactorOrb(
                sizeDp = 130.dp,
                isActive = true,
                isSpeaking = isSpeaking,
                onClick = {
                    viewModel.voiceEngine.speakRecommendation()
                }
            )
        }

        // Live Voice & Terminal Speech Box
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .border(1.dp, CyanPrimary.copy(alpha = 0.35f), RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            color = DarkSurface
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (isSpeaking) GoldAccent else NeonGreenSuccess)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isSpeaking) "JARVIS SPEAKING..." else "SYSTEM ONLINE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isSpeaking) GoldAccent else NeonGreenSuccess,
                                letterSpacing = 1.sp
                            )
                        )
                    }

                    IconButton(
                        onClick = {
                            viewModel.voiceEngine.speak(lastResponse)
                        },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "Repeat speech",
                            tint = CyanPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = lastResponse,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = TextPrimary,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    ),
                    modifier = Modifier.testTag("assistant_speech_text")
                )

                if (lastResponse.contains("Bhai yeh theek hai")) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        color = GoldAccent.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.selectTab(JarvisTab.SHOPPING)
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "🛒 View Recommended Shoes (Size 5)",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = GoldAccent
                                )
                            )
                            Text(
                                text = "Go to Deals >",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = CyanPrimary
                                )
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // System Telemetry Chips (Accessibility, Vision, Anti-Cheat, Ethical Shield)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TelemetryBadge(
                label = if (isAccessibilityActive) "Accessibility ON" else "Accessibility Standby",
                active = true,
                modifier = Modifier.weight(1f),
                onClick = { viewModel.selectTab(JarvisTab.DIAGNOSTICS) }
            )
            TelemetryBadge(
                label = "Ethical Shield ACTIVE",
                active = true,
                modifier = Modifier.weight(1f),
                onClick = { viewModel.selectTab(JarvisTab.SECURITY_SELLER) }
            )
            TelemetryBadge(
                label = "Anti-Cheat SAFE",
                active = true,
                modifier = Modifier.weight(1f),
                onClick = { viewModel.selectTab(JarvisTab.GAMING_VIP) }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Quick Command Chips
        Text(
            text = "Autonomous Quick Directives",
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold,
                color = CyanPrimary
            ),
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(quickCommands) { cmd ->
                Surface(
                    color = DarkSurfaceVariant,
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .border(1.dp, CyanPrimary.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                        .clickable { viewModel.executeUserCommand(cmd) }
                ) {
                    Text(
                        text = cmd,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextPrimary,
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp
                        ),
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Input Command Bar
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            color = DarkSurface,
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, CyanPrimary.copy(alpha = 0.5f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        if (isListening) {
                            viewModel.voiceEngine.stopListening()
                        } else {
                            viewModel.voiceEngine.startSpeechRecognition { recognized ->
                                viewModel.setCommandInput(recognized)
                                viewModel.executeUserCommand(recognized)
                            }
                        }
                    },
                    modifier = Modifier.testTag("voice_assistant_mic_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Voice Directive",
                        tint = if (isListening) GoldAccent else CyanPrimary
                    )
                }

                OutlinedTextField(
                    value = commandInput,
                    onValueChange = { viewModel.setCommandInput(it) },
                    placeholder = {
                        Text(
                            text = if (isListening) "Listening to voice directive..." else "Command Jarvis (e.g. 'Flipkart par 5 no shoes dikhao')...",
                            fontSize = 13.sp,
                            color = if (isListening) GoldAccent else TextSecondary
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("jarvis_command_input"),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                if (isBusy) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(28.dp)
                            .padding(4.dp),
                        color = CyanPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    IconButton(
                        onClick = {
                            viewModel.executeUserCommand(commandInput)
                        },
                        modifier = Modifier.testTag("jarvis_send_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Execute Command",
                            tint = CyanPrimary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // CORE OPERATING MODES (Medium Mode, Automatic Mode & Gaming Key-Mapping Mode)
        Text(
            text = "Assistant Operating Modes",
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold,
                color = CyanPrimary
            ),
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // 1. Medium Mode Card
            OperatingModeCard(
                title = "Medium Mode",
                subtitle = "General queries, web search, app opening assistance, and screenshot/visual interactions.",
                badge = "Standard • Free",
                badgeColor = CyanPrimary,
                icon = Icons.Default.Explore,
                isActive = executionMode == AssistantExecutionMode.MEDIUM,
                onClick = { viewModel.launchMediumMode() },
                tag = "dashboard_medium_mode_card"
            )

            // 2. Automatic Mode Card (1-Day Free Trial)
            OperatingModeCard(
                title = "Automatic Mode",
                subtitle = "Full-device automated hands-free controls & task execution. No screen touching required.",
                badge = if (trialHours > 0) "1-Day Free Trial (${trialHours}h left)" else "VIP Pass",
                badgeColor = GoldAccent,
                icon = Icons.Default.AutoAwesome,
                isActive = executionMode == AssistantExecutionMode.AUTOMATIC,
                onClick = { viewModel.launchAutomaticMode() },
                tag = "dashboard_automatic_mode_card"
            )

            // 3. Gaming Key-Mapping Mode Card (Right below Medium Mode and Automatic Mode cards!)
            OperatingModeCard(
                title = "Gaming Key-Mapping Mode",
                subtitle = "Panda / Octopus style physical peripheral mapping with 1-Tap Key Assignment & 360° Mouse Look. Completely FREE with zero VIP gating and 100% anti-ban safety.",
                badge = "100% Free • Zero VIP Gating",
                badgeColor = NeonGreenSuccess,
                icon = Icons.Default.Keyboard,
                isActive = executionMode == AssistantExecutionMode.GAMING_KEY_MAPPING,
                onClick = { viewModel.launchGamingKeyMappingMode() },
                tag = "dashboard_gaming_keymap_mode_card"
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Autonomous Systems Directory
        Text(
            text = "Autonomous Systems Directory",
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold,
                color = CyanPrimary
            ),
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        val modules = listOf(
            Triple(JarvisTab.HEALTH_CARETAKER, "24/7 Caretaker & Health", "Fatigue mood sensing, screen breaks & 1 PM / 9 PM meal alerts") to Icons.Default.Favorite,
            Triple(JarvisTab.GAMING_KEY_MAPPING, "Gaming Key-Mapping Mode", "Panda / Octopus style physical mouse & keyboard USB OTG HUD mapping with 100% anti-ban") to Icons.Default.Keyboard,
            Triple(JarvisTab.SHOPPING, "Shopping Automation", "Flipkart / Amazon shoes search with voice buy advice") to Icons.Default.ShoppingCart,
            Triple(JarvisTab.GAMING_VIP, "Free Fire Gaming VIP", "Anti-cheat Bluetooth HID takeover & phone call defense") to Icons.Default.Gamepad,
            Triple(JarvisTab.CREATOR_SCHEDULER, "Creator Night-Shift", "Scheduled YouTube / Instagram post at 5:00 AM") to Icons.Default.Schedule,
            Triple(JarvisTab.AUTO_REPAIR, "Auto-Diagnosis & Repair", "Diagnose phone lag, optimize RAM & fix settings") to Icons.Default.Speed,
            Triple(JarvisTab.CAMERA_VISION, "Situational Camera Vision", "Live camera guidance & privacy assurance") to Icons.Default.CameraAlt,
            Triple(JarvisTab.SECURITY_SELLER, "Ethical Shield & Seller PAN", "Zero-bypass security & merchant PAN verification") to Icons.Default.Security,
            Triple(JarvisTab.DIAGNOSTICS, "Diagnostics & Error Reporting", "Accessibility status & automated email to developer") to Icons.Default.BugReport
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            modules.forEach { (moduleInfo, icon) ->
                val (tab, title, desc) = moduleInfo
                val isVip = tab == JarvisTab.GAMING_VIP

                Surface(
                    color = DarkSurface,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            1.dp,
                            if (isVip) GoldAccent.copy(alpha = 0.4f) else CyanPrimary.copy(alpha = 0.25f),
                            RoundedCornerShape(12.dp)
                        )
                        .clickable { viewModel.selectTab(tab) }
                        .testTag("core_module_card_${tab.name.lowercase()}")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = if (isVip) GoldAccent.copy(alpha = 0.15f) else CyanPrimary.copy(alpha = 0.15f),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = title,
                                    tint = if (isVip) GoldAccent else CyanPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                )
                                if (isVip) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        color = GoldAccent.copy(alpha = 0.2f),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = "VIP",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Black,
                                            color = GoldAccent,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                }
                            }
                            Text(
                                text = desc,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Open $title",
                            tint = TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TelemetryBadge(
    label: String,
    active: Boolean,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Surface(
        color = DarkSurfaceVariant,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier.then(
            if (onClick != null) Modifier.clickable { onClick() } else Modifier
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(if (active) NeonGreenSuccess else Color.Gray)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                ),
                maxLines = 1
            )
        }
    }
}

@Composable
private fun OperatingModeCard(
    title: String,
    subtitle: String,
    badge: String,
    badgeColor: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isActive: Boolean,
    onClick: () -> Unit,
    tag: String
) {
    Surface(
        color = if (isActive) DarkSurfaceHighlight else DarkSurface,
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.5.dp,
                if (isActive) badgeColor else badgeColor.copy(alpha = 0.35f),
                RoundedCornerShape(14.dp)
            )
            .clickable { onClick() }
            .testTag(tag)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = badgeColor.copy(alpha = 0.15f),
                modifier = Modifier.size(42.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = badgeColor,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                        if (isActive) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(badgeColor)
                            )
                        }
                    }

                    Surface(
                        color = badgeColor.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = badge,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            color = badgeColor,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Open $title",
                tint = if (isActive) badgeColor else TextSecondary,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

