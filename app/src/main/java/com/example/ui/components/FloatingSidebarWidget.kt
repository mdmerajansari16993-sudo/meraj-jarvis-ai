package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
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
import com.example.viewmodel.JarvisViewModel

@Composable
fun FloatingSidebarWidget(
    viewModel: JarvisViewModel,
    modifier: Modifier = Modifier
) {
    val isMenuOpen by viewModel.isFloatingSidebarMenuOpen.collectAsState()
    val isMinimized by viewModel.isMinimizedToFloatingWidget.collectAsState()
    val executionMode by viewModel.executionMode.collectAsState()
    val trialHours by viewModel.trialRemainingHours.collectAsState()
    val isSpeaking by viewModel.voiceEngine.isSpeaking.collectAsState()

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    // Floating Edge Pill Button (24/7 Persistent on Screen Edge)
    Box(
        modifier = modifier
            .fillMaxSize(),
        contentAlignment = Alignment.CenterEnd
    ) {
        Surface(
            color = DarkSurface.copy(alpha = 0.95f),
            shape = RoundedCornerShape(topStart = 24.dp, bottomStart = 24.dp),
            modifier = Modifier
                .border(
                    1.5.dp,
                    if (isSpeaking) GoldAccent else CyanPrimary,
                    RoundedCornerShape(topStart = 24.dp, bottomStart = 24.dp)
                )
                .clickable { viewModel.openFloatingSidebarMenu() }
                .padding(vertical = 4.dp, horizontal = 4.dp)
                .testTag("floating_sidebar_pill_icon")
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 6.dp)
            ) {
                // Mini Arc Reactor / Jarvis Glowing Core
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .scale(if (isSpeaking) pulseScale else 1f)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    if (isSpeaking) GoldAccent else CyanPrimary,
                                    DarkBackground
                                )
                            )
                        )
                        .border(1.dp, if (isSpeaking) GoldAccent else CyanPrimary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (executionMode == AssistantExecutionMode.AUTOMATIC) Icons.Default.AutoAwesome else Icons.Default.Speed,
                        contentDescription = "Meraj Jarvis AI Floating Sidebar",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                Column {
                    Text(
                        text = "JARVIS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Black,
                            color = CyanPrimary,
                            letterSpacing = 1.sp,
                            fontSize = 9.sp
                        )
                    )
                    Text(
                        text = when (executionMode) {
                            AssistantExecutionMode.AUTOMATIC -> "AUTO"
                            AssistantExecutionMode.GAMING_KEY_MAPPING -> "KEY-MAP"
                            else -> "MEDIUM"
                        },
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (executionMode == AssistantExecutionMode.AUTOMATIC) GoldAccent else NeonGreenSuccess,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }

    // Modal Dual-Mode + Gaming Key-Mapping Menu
    if (isMenuOpen) {
        Dialog(onDismissRequest = { viewModel.closeFloatingSidebarMenu() }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .border(1.5.dp, CyanPrimary, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                color = DarkSurface
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(NeonGreenSuccess)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "JARVIS 24/7 DUAL-MODE HUB",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    color = CyanPrimary,
                                    letterSpacing = 1.sp
                                )
                            )
                        }

                        IconButton(
                            onClick = { viewModel.closeFloatingSidebarMenu() },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close Menu",
                                tint = TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 1. Medium Mode Option
                    ModeSelectCard(
                        title = "Medium Mode",
                        badge = "Standard • Free",
                        badgeColor = CyanPrimary,
                        desc = "Handles general queries, search commands, web navigation, and screenshot/visual interactions.",
                        icon = Icons.Default.Explore,
                        isSelected = executionMode == AssistantExecutionMode.MEDIUM,
                        onClick = {
                            viewModel.launchMediumMode()
                        },
                        tag = "mode_medium_card"
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // 2. Automatic Mode Option (1-Day Free Trial)
                    ModeSelectCard(
                        title = "Automatic Mode",
                        badge = if (trialHours > 0) "1-Day Free Trial (${trialHours}h left)" else "VIP Required",
                        badgeColor = GoldAccent,
                        desc = "Full-device automated controls, app openings, and zero-touch tasks. Test completely free for 24 hours.",
                        icon = Icons.Default.AutoAwesome,
                        isSelected = executionMode == AssistantExecutionMode.AUTOMATIC,
                        onClick = {
                            viewModel.launchAutomaticMode()
                        },
                        tag = "mode_automatic_card"
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // 3. Gaming Key-Mapping Mode Option (Dedicated Panda/Octopus style)
                    ModeSelectCard(
                        title = "Gaming Key-Mapping Mode",
                        badge = "100% Free • Zero VIP Gating",
                        badgeColor = NeonGreenSuccess,
                        desc = "1-Tap Key Assignment & 360° Mouse Look. Completely FREE for all users with 100% ban-free native Bluetooth HID.",
                        icon = Icons.Default.Keyboard,
                        isSelected = executionMode == AssistantExecutionMode.GAMING_KEY_MAPPING,
                        onClick = {
                            viewModel.launchGamingKeyMappingMode()
                        },
                        tag = "mode_gaming_keymap_card"
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Bottom Quick Actions (Maximize / Voice)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.closeFloatingSidebarMenu()
                                if (isMinimized) {
                                    viewModel.toggleMinimizeToFloatingWidget()
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("maximize_dashboard_btn"),
                            colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceHighlight),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Fullscreen,
                                contentDescription = null,
                                tint = CyanPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Full App", fontSize = 11.sp, color = TextPrimary)
                        }

                        Button(
                            onClick = {
                                viewModel.closeFloatingSidebarMenu()
                                viewModel.voiceEngine.startSpeechRecognition { cmd ->
                                    viewModel.setCommandInput(cmd)
                                    viewModel.executeUserCommand(cmd)
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("floating_quick_voice_btn"),
                            colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Voice Mic", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ModeSelectCard(
    title: String,
    badge: String,
    badgeColor: Color,
    desc: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    tag: String
) {
    Surface(
        color = if (isSelected) DarkSurfaceHighlight else DarkSurfaceVariant,
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                if (isSelected) badgeColor else badgeColor.copy(alpha = 0.3f),
                RoundedCornerShape(14.dp)
            )
            .clickable { onClick() }
            .testTag(tag)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = badgeColor.copy(alpha = 0.15f),
                modifier = Modifier.size(38.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = badgeColor,
                        modifier = Modifier.size(20.dp)
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
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )

                    Surface(
                        color = badgeColor.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = badge,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = badgeColor,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = desc,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                )
            }
        }
    }
}
