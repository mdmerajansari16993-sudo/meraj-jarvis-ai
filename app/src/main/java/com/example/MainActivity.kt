package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.FloatingSidebarWidget
import com.example.ui.components.MinimalVoiceVisualizer
import com.example.ui.components.OnboardingDialog
import com.example.ui.components.SecurityLockdownDialog
import com.example.ui.components.VipPaymentDialog
import com.example.ui.screens.AutoRepairScreen
import com.example.ui.screens.CameraVisionScreen
import com.example.ui.screens.CoreHudScreen
import com.example.ui.screens.CreatorNightShiftScreen
import com.example.ui.screens.DiagnosticsScreen
import com.example.ui.screens.GamingKeyMappingScreen
import com.example.ui.screens.GamingScreen
import com.example.ui.screens.HealthCaretakerScreen
import com.example.ui.screens.SecuritySellerScreen
import com.example.ui.screens.ShoppingScreen
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.JarvisTab
import com.example.viewmodel.JarvisViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: JarvisViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                JarvisMainScreen(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun JarvisMainScreen(viewModel: JarvisViewModel) {
    val currentTab by viewModel.currentTab.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val securityLockdown by viewModel.securityLockdown.collectAsState()
    val showVipPayDialog by viewModel.showVipPayDialog.collectAsState()
    val isMinimized by viewModel.isMinimizedToFloatingWidget.collectAsState()

    val isSpeaking by viewModel.voiceEngine.isSpeaking.collectAsState()
    val isListening by viewModel.voiceEngine.isListening.collectAsState()
    val lastSpeech by viewModel.lastAssistantResponse.collectAsState()

    // Back handler to return to CORE_HUD when in sub-screens
    BackHandler(enabled = currentTab != JarvisTab.CORE_HUD) {
        viewModel.selectTab(JarvisTab.CORE_HUD)
    }

    if (isMinimized) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBackground)
        ) {
            Surface(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(24.dp)
                    .border(1.5.dp, CyanPrimary.copy(alpha = 0.5f), RoundedCornerShape(20.dp)),
                color = DarkSurface,
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(CyanPrimary.copy(alpha = 0.2f))
                            .border(1.dp, CyanPrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Memory,
                            contentDescription = null,
                            tint = CyanPrimary,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "MERAJ JARVIS AI",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = CyanPrimary,
                            letterSpacing = 1.sp
                        )
                    )

                    Text(
                        text = "Background Autonomous Mode • 24/7 Active",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 11.sp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Jarvis is docked on your screen edge. Tap the glowing floating icon on the side to switch modes or speak directives.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextPrimary,
                            lineHeight = 16.sp,
                            fontSize = 11.sp
                        ),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = { viewModel.toggleMinimizeToFloatingWidget() },
                            colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Full App", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }

                        Button(
                            onClick = { viewModel.openFloatingSidebarMenu() },
                            colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Dual-Mode Hub", color = TextPrimary, fontSize = 11.sp)
                        }
                    }
                }
            }

            // 24/7 Persistent Floating Edge Sidebar Icon
            FloatingSidebarWidget(viewModel = viewModel)
        }
    } else {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                JarvisBottomBar(
                    currentTab = currentTab,
                    onTabSelected = { viewModel.selectTab(it) }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(DarkBackground)
                    .padding(innerPadding)
            ) {
                Crossfade(targetState = currentTab, label = "tab_crossfade") { tab ->
                    when (tab) {
                        JarvisTab.CORE_HUD -> CoreHudScreen(viewModel = viewModel)
                        JarvisTab.GAMING_KEY_MAPPING -> GamingKeyMappingScreen(viewModel = viewModel)
                        JarvisTab.SHOPPING -> ShoppingScreen(viewModel = viewModel)
                        JarvisTab.GAMING_VIP -> GamingScreen(viewModel = viewModel)
                        JarvisTab.CREATOR_SCHEDULER -> CreatorNightShiftScreen(viewModel = viewModel)
                        JarvisTab.AUTO_REPAIR -> AutoRepairScreen(viewModel = viewModel)
                        JarvisTab.CAMERA_VISION -> CameraVisionScreen(viewModel = viewModel)
                        JarvisTab.HEALTH_CARETAKER -> HealthCaretakerScreen(viewModel = viewModel)
                        JarvisTab.SECURITY_SELLER -> SecuritySellerScreen(viewModel = viewModel)
                        JarvisTab.DIAGNOSTICS -> DiagnosticsScreen(viewModel = viewModel)
                    }
                }

                // Minimal Glowing Voice Wave & Bouncing Dots (Ultra Clean - No intrusive popups)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                        .padding(top = 4.dp)
                ) {
                    MinimalVoiceVisualizer(
                        isSpeaking = isSpeaking,
                        isListening = isListening,
                        currentSpeechSnippet = lastSpeech.take(40)
                    )
                }

                // 24/7 Persistent Floating Sidebar Icon
                FloatingSidebarWidget(viewModel = viewModel)

                // 1. Mandatory First-Launch Onboarding Gate
                if (userProfile == null || !userProfile!!.isVerified) {
                    OnboardingDialog(
                        onConfirm = { name, age, location, spec ->
                            viewModel.saveOnboardingProfile(name, age, location, spec)
                        }
                    )
                }

                // 2. Strict Security & Ethical Shield Lockdown Modal
                securityLockdown?.let { lockdown ->
                    SecurityLockdownDialog(
                        lockdown = lockdown,
                        onDismiss = { viewModel.clearSecurityLockdown() }
                    )
                }

                // 3. VIP Subscription Payment Dialog for Gaming Co-Pilot
                if (showVipPayDialog) {
                    VipPaymentDialog(
                        onDismiss = { viewModel.closeVipPayDialog() },
                        onTierSelected = { tier ->
                            viewModel.activateVipSubscription(tier)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun JarvisBottomBar(
    currentTab: JarvisTab,
    onTabSelected: (JarvisTab) -> Unit
) {
    val navItems = listOf(
        Triple(JarvisTab.CORE_HUD, "Jarvis", Icons.Default.Memory),
        Triple(JarvisTab.GAMING_KEY_MAPPING, "Key-Map", Icons.Default.Keyboard),
        Triple(JarvisTab.SHOPPING, "Shop", Icons.Default.ShoppingCart),
        Triple(JarvisTab.GAMING_VIP, "Game VIP", Icons.Default.Gamepad),
        Triple(JarvisTab.CREATOR_SCHEDULER, "Night-Shift", Icons.Default.Schedule),
        Triple(JarvisTab.HEALTH_CARETAKER, "Care", Icons.Default.Favorite),
        Triple(JarvisTab.AUTO_REPAIR, "Auto-Fix", Icons.Default.Speed),
        Triple(JarvisTab.CAMERA_VISION, "Vision", Icons.Default.CameraAlt),
        Triple(JarvisTab.SECURITY_SELLER, "Security", Icons.Default.Security),
        Triple(JarvisTab.DIAGNOSTICS, "Diagnostics", Icons.Default.BugReport)
    )

    Surface(
        modifier = Modifier
            .navigationBarsPadding()
            .border(1.dp, CyanPrimary.copy(alpha = 0.25f), RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
        color = DarkSurface,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        tonalElevation = 8.dp
    ) {
        val selectedIndex = navItems.indexOfFirst { it.first == currentTab }.coerceAtLeast(0)
        ScrollableTabRow(
            selectedTabIndex = selectedIndex,
            containerColor = DarkSurface,
            contentColor = CyanPrimary,
            edgePadding = 8.dp,
            indicator = { tabPositions ->
                if (selectedIndex < tabPositions.size) {
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedIndex]),
                        color = if (currentTab == JarvisTab.GAMING_VIP) GoldAccent else CyanPrimary,
                        height = 3.dp
                    )
                }
            },
            divider = {}
        ) {
            navItems.forEach { (tab, label, icon) ->
                val isSelected = currentTab == tab
                val tintColor = if (tab == JarvisTab.GAMING_VIP && isSelected) {
                    GoldAccent
                } else if (isSelected) {
                    CyanPrimary
                } else {
                    TextSecondary
                }

                Tab(
                    selected = isSelected,
                    onClick = { onTabSelected(tab) },
                    modifier = Modifier
                        .testTag("nav_tab_${tab.name.lowercase()}")
                        .padding(horizontal = 4.dp, vertical = 6.dp),
                    text = {
                        Text(
                            text = label,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = tintColor
                        )
                    },
                    icon = {
                        Icon(
                            imageVector = icon,
                            contentDescription = label,
                            tint = tintColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                )
            }
        }
    }
}
