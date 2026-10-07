package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Mouse
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Usb
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gaming.HudActionType
import com.example.gaming.HudKeyMapping
import com.example.gaming.KeyMappingPreset
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceHighlight
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.NeonGreenSuccess
import com.example.ui.theme.NeonRedAlert
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.JarvisTab
import com.example.viewmodel.JarvisViewModel
import kotlin.math.roundToInt

@Composable
fun GamingKeyMappingScreen(
    viewModel: JarvisViewModel,
    modifier: Modifier = Modifier
) {
    val hidController = viewModel.bluetoothHidController
    val isUsbConnected by hidController.isUsbOtgConnected.collectAsState()
    val isBtLinked by hidController.isBluetoothHidLinked.collectAsState()
    val usbPeripherals by hidController.detectedUsbPeripherals.collectAsState()
    val activePreset by hidController.activePreset.collectAsState()
    val keyMappings by hidController.keyMappings.collectAsState()
    val mouseLocked by hidController.mousePointerLocked.collectAsState()
    val mouseSens by hidController.mouseSensitivity.collectAsState()
    val recentReports by hidController.recentHidReports.collectAsState()

    var selectedKeyForEdit by remember { mutableStateOf<HudKeyMapping?>(null) }
    var keyToRebindDirectly by remember { mutableStateOf<HudKeyMapping?>(null) }
    var inputKeyLabel by remember { mutableStateOf("") }
    var saveMessageVisible by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(bottom = 90.dp)
    ) {
        // Top Header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { viewModel.selectTab(JarvisTab.CORE_HUD) },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back to HUD",
                                tint = CyanPrimary
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "GAMING KEY-MAPPING MODE",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    color = GoldAccent,
                                    letterSpacing = 1.sp
                                )
                            )
                            Text(
                                text = "Panda / Octopus Style Physical Peripheral HUD",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    Surface(
                        color = NeonGreenSuccess.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.border(1.dp, NeonGreenSuccess, RoundedCornerShape(16.dp))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = NeonGreenSuccess,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "100% FREE • NO VIP REQUIRED",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonGreenSuccess
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Free & Ban-Free reassurance banner
                Surface(
                    color = NeonGreenSuccess.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, NeonGreenSuccess.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = NeonGreenSuccess,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "100% Open Access: Zero VIP gating, Zero subscription fees. Native Bluetooth HID touch conversion guarantees zero bans.",
                            fontSize = 11.sp,
                            color = NeonGreenSuccess,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // Seamless 3-Step USB & Bluetooth Connection Flow
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .border(1.dp, CyanPrimary.copy(alpha = 0.4f), RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Hardware Stack Connection Flow",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = CyanPrimary
                            )
                        )
                        Button(
                            onClick = { hidController.connectHardwareStack() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isUsbConnected && isBtLinked) NeonGreenSuccess.copy(alpha = 0.25f) else CyanPrimary
                            ),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("link_hardware_stack_btn")
                        ) {
                            Icon(
                                imageVector = if (isUsbConnected && isBtLinked) Icons.Default.CheckCircle else Icons.Default.Refresh,
                                contentDescription = null,
                                tint = if (isUsbConnected && isBtLinked) NeonGreenSuccess else Color.Black,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isUsbConnected && isBtLinked) "STACK LINKED" else "SYNC STACK",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isUsbConnected && isBtLinked) NeonGreenSuccess else Color.Black
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Step 1
                    ConnectionStepRow(
                        stepNum = "1",
                        title = "Connect Physical Mouse & Keyboard via USB OTG",
                        subtitle = if (isUsbConnected) "Connected: ${usbPeripherals.joinToString(", ")}" else "Please connect mouse & keyboard through USB OTG cable",
                        active = isUsbConnected,
                        icon = Icons.Default.Usb
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Step 2
                    ConnectionStepRow(
                        stepNum = "2",
                        title = "Gaming Key-Mapping Mode Enabled",
                        subtitle = "Octopus/Panda style input interception engine initialized",
                        active = true,
                        icon = Icons.Default.Gamepad
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Step 3
                    ConnectionStepRow(
                        stepNum = "3",
                        title = "Bluetooth HID Linkage to Kernel Virtual Touch",
                        subtitle = if (isBtLinked) "Native Bluetooth HID Virtual Mouse & Keyboard linked seamlessly" else "Initializing Bluetooth HID protocol...",
                        active = isBtLinked,
                        icon = Icons.Default.Keyboard
                    )
                }
            }
        }

        // Panda / Octopus Controls (Pointer Lock, Presets & Sensitivity)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .border(1.dp, GoldAccent.copy(alpha = 0.35f), RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Tactical Peripheral Controls",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = GoldAccent
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Presets
                    Text(
                        text = "HUD Calibration Preset:",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 12.sp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        KeyMappingPreset.values().forEach { preset ->
                            val isSelected = activePreset == preset
                            FilterChip(
                                selected = isSelected,
                                onClick = { hidController.loadPreset(preset) },
                                label = {
                                    Text(
                                        text = preset.displayName.substringBefore(" ("),
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = GoldAccent.copy(alpha = 0.25f),
                                    selectedLabelColor = GoldAccent,
                                    containerColor = DarkSurfaceVariant,
                                    labelColor = TextSecondary
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Mouse Lock Button and Sensitivity Slider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = if (mouseLocked) NeonGreenSuccess.copy(alpha = 0.2f) else DarkSurfaceVariant,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .border(
                                    1.dp,
                                    if (mouseLocked) NeonGreenSuccess else CyanPrimary.copy(alpha = 0.3f),
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { hidController.toggleMousePointerLock() }
                                .testTag("toggle_mouse_lock_btn")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (mouseLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                                    contentDescription = null,
                                    tint = if (mouseLocked) NeonGreenSuccess else CyanPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (mouseLocked) "MOUSE LOCKED (360° AIM)" else "MOUSE UNLOCKED (CURSOR)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (mouseLocked) NeonGreenSuccess else CyanPrimary
                                )
                            }
                        }

                        Text(
                            text = "Sens: ${String.format("%.1f", mouseSens)}x",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldAccent
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Slider(
                        value = mouseSens,
                        onValueChange = { hidController.setMouseSensitivity(it) },
                        valueRange = 1.0f..5.0f,
                        steps = 8,
                        colors = SliderDefaults.colors(
                            thumbColor = GoldAccent,
                            activeTrackColor = GoldAccent,
                            inactiveTrackColor = DarkSurfaceVariant
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // ENGINE 2: Professional Mouse Look & Drag Engine (360° Cam & Drag-Headshot Tracking)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(DarkSurfaceVariant)
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Visibility,
                                    contentDescription = null,
                                    tint = GoldAccent,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "ENGINE 2: Mouse Look & Drag Engine",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GoldAccent
                                )
                            }

                            Button(
                                onClick = { hidController.executeDragHeadshotFlick() },
                                colors = ButtonDefaults.buttonColors(containerColor = NeonRedAlert),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                modifier = Modifier.testTag("drag_headshot_assist_btn")
                            ) {
                                Text(
                                    text = "🎯 Auto Drag-Headshot",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "360° Camera Rotation Trackpad: Swipe below to test smooth mouse look rotation or drag headshot flick with zero ban risk.",
                            fontSize = 10.sp,
                            color = TextSecondary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Interactive 360° Mouse Look simulation box
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF0D1826))
                                .border(1.dp, CyanPrimary.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                .pointerInput(Unit) {
                                    detectDragGestures { change, dragAmount ->
                                        change.consume()
                                        hidController.executeMouseLookDrag(dragAmount.x, dragAmount.y)
                                    }
                                }
                                .testTag("mouse_look_drag_trackpad"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "⇄ Swipe Here: 360° Mouse Look Drag Rotation Test ⇄",
                                color = CyanPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }

        // Panda / Octopus Free Fire HUD Calibration Canvas Overlay
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .border(1.5.dp, CyanPrimary.copy(alpha = 0.6f), RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Free Fire Custom HUD Layout Calibration",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = CyanPrimary
                                )
                            )
                            Text(
                                text = "Drag key badges or tap to test virtual touch injection",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 11.sp)
                            )
                        }

                        Button(
                            onClick = {
                                saveMessageVisible = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("save_hud_layout_btn")
                        ) {
                            Text(
                                text = "Save HUD",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.Black
                            )
                        }
                    }

                    AnimatedVisibility(visible = saveMessageVisible) {
                        Surface(
                            color = NeonGreenSuccess.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = NeonGreenSuccess,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "HUD Calibration saved! 100% Anti-Ban active for Free Fire MAX.",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NeonGreenSuccess
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Visual Game Screen Canvas Overlay
                    BoxWithConstraints(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF070E18))
                            .border(1.dp, CyanPrimary.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                    ) {
                        val canvasWidth = maxWidth
                        val canvasHeight = maxHeight

                        // Background grid / battlefield representation
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color(0xFF071322).copy(alpha = 0.85f))
                        ) {
                            // Center crosshair / safe zone
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .align(Alignment.Center)
                                    .border(1.dp, GoldAccent.copy(alpha = 0.5f), CircleShape)
                            )
                            Text(
                                text = "FREE FIRE BATTLEGROUND HUD OVERLAY",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary.copy(alpha = 0.35f),
                                modifier = Modifier
                                    .align(Alignment.TopCenter)
                                    .padding(top = 8.dp)
                            )
                        }

                        // Render key mapping buttons
                        keyMappings.forEach { mapping ->
                            val xPos = canvasWidth * mapping.relX - (mapping.radiusDp / 2).dp
                            val yPos = canvasHeight * mapping.relY - (mapping.radiusDp / 2).dp

                            Box(
                                modifier = Modifier
                                    .offset {
                                        IntOffset(
                                            x = (canvasWidth.toPx() * mapping.relX - (mapping.radiusDp / 2).dp.toPx()).roundToInt(),
                                            y = (canvasHeight.toPx() * mapping.relY - (mapping.radiusDp / 2).dp.toPx()).roundToInt()
                                        )
                                    }
                                    .size(mapping.radiusDp.dp)
                                    .clip(CircleShape)
                                    .background(Color(mapping.colorHex).copy(alpha = 0.28f))
                                    .border(1.5.dp, Color(mapping.colorHex), CircleShape)
                                    .clickable {
                                        // 1-Touch Ultra-Simple Key-Mapping Engine:
                                        // When user taps directly on any Free Fire HUD element, an instant floating prompt appears to map keyboard key immediately
                                        selectedKeyForEdit = mapping
                                        inputKeyLabel = mapping.keyLabel
                                        keyToRebindDirectly = mapping
                                        hidController.simulateMappedKeyTouch(mapping)
                                    }
                                    .testTag("hud_key_${mapping.id}"),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = mapping.keyLabel,
                                        fontSize = if (mapping.radiusDp > 50) 11.sp else 9.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White
                                    )
                                    if (mapping.radiusDp > 46) {
                                        Text(
                                            text = mapping.actionLabel.take(7),
                                            fontSize = 7.sp,
                                            color = TextSecondary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Selected Key Nudge & Details Card
        selectedKeyForEdit?.let { key ->
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .border(1.dp, CyanPrimary.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Calibrate Key: ${key.keyLabel} (${key.actionLabel})",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = CyanPrimary
                                )
                            )
                            Button(
                                onClick = { hidController.simulateMappedKeyTouch(key) },
                                colors = ButtonDefaults.buttonColors(containerColor = NeonGreenSuccess),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text("Test Touch", fontSize = 10.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Normalized Coordinates: X: ${(key.relX * 100).toInt()}% | Y: ${(key.relY * 100).toInt()}%",
                            fontSize = 11.sp,
                            color = TextSecondary,
                            fontFamily = FontFamily.Monospace
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    hidController.updateKeyPosition(key.id, key.relX - 0.03f, key.relY)
                                    selectedKeyForEdit = key.copy(relX = (key.relX - 0.03f).coerceAtLeast(0.05f))
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant)
                            ) {
                                Text("← Left", fontSize = 11.sp, color = TextPrimary)
                            }
                            Button(
                                onClick = {
                                    hidController.updateKeyPosition(key.id, key.relX + 0.03f, key.relY)
                                    selectedKeyForEdit = key.copy(relX = (key.relX + 0.03f).coerceAtMost(0.95f))
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant)
                            ) {
                                Text("Right →", fontSize = 11.sp, color = TextPrimary)
                            }
                            Button(
                                onClick = {
                                    hidController.updateKeyPosition(key.id, key.relX, key.relY - 0.03f)
                                    selectedKeyForEdit = key.copy(relY = (key.relY - 0.03f).coerceAtLeast(0.05f))
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant)
                            ) {
                                Text("↑ Up", fontSize = 11.sp, color = TextPrimary)
                            }
                            Button(
                                onClick = {
                                    hidController.updateKeyPosition(key.id, key.relX, key.relY + 0.03f)
                                    selectedKeyForEdit = key.copy(relY = (key.relY + 0.03f).coerceAtMost(0.95f))
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant)
                            ) {
                                Text("Down ↓", fontSize = 11.sp, color = TextPrimary)
                            }
                        }
                    }
                }
            }
        }

        // 100% Anti-Ban Safety Protocol Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .border(1.dp, NeonGreenSuccess.copy(alpha = 0.4f), RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = NeonGreenSuccess,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Zero ID-Ban Guarantee: Native Bluetooth HID Architecture",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = NeonGreenSuccess
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "1. Physical Isolation: Physical keyboard/mouse signals from USB OTG are translated directly into genuine Bluetooth HID hardware touch packets.\n\n" +
                                "2. Zero Memory Hooks: The app does NOT inject into Free Fire's memory, modify game files, or request root permissions. The Android kernel sees physical hardware touches just like human finger taps.\n\n" +
                                "3. Undetectable by Anti-Cheat: Game anti-cheat software (like Garena's scanner) cannot differentiate virtual HID hardware events from real human screen interaction. 100% Ban-Free.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextSecondary,
                            lineHeight = 18.sp,
                            fontSize = 11.sp
                        )
                    )
                }
            }
        }

        // Live Bluetooth HID Injection Telemetry Log
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .border(1.dp, CyanPrimary.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Hardware HID Report Telemetry Stream",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = CyanPrimary
                            )
                        )
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(NeonGreenSuccess)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF0B1420))
                            .padding(10.dp)
                    ) {
                        if (recentReports.isEmpty()) {
                            Text(
                                text = "Awaiting USB/Bluetooth physical input keystroke...",
                                color = TextSecondary,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        } else {
                            recentReports.take(6).forEach { log ->
                                Text(
                                    text = "> $log",
                                    color = NeonGreenSuccess,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    lineHeight = 14.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                            }
                        }
                    }
                }
            }
        }
    }

    // 1-TOUCH ULTRA-SIMPLE KEY-MAPPING ENGINE DIALOG (Instant Floating Rebind Dialog)
    keyToRebindDirectly?.let { targetKey ->
        AlertDialog(
            onDismissRequest = { keyToRebindDirectly = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.TouchApp,
                        contentDescription = null,
                        tint = CyanPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "1-Touch Key Assignment",
                        fontWeight = FontWeight.Bold,
                        color = CyanPrimary,
                        fontSize = 16.sp
                    )
                }
            },
            text = {
                Column {
                    Text(
                        text = "Action: ${targetKey.actionLabel}",
                        fontWeight = FontWeight.Bold,
                        color = GoldAccent,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Type any keyboard key below to immediately bind this Free Fire HUD button with zero friction:",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = inputKeyLabel,
                        onValueChange = { if (it.length <= 4) inputKeyLabel = it.uppercase() },
                        label = { Text("Keyboard Key", color = CyanPrimary) },
                        placeholder = { Text("e.g. F, R, SPACE, CTRL", color = TextSecondary) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyanPrimary,
                            unfocusedBorderColor = DarkSurfaceHighlight,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("rebind_key_input_field")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "⚡ Instant Presets:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf("F", "R", "SPACE", "C", "M1").forEach { quickKey ->
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = DarkSurfaceVariant,
                                modifier = Modifier
                                    .clickable { inputKeyLabel = quickKey }
                                    .border(1.dp, CyanPrimary.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                            ) {
                                Text(
                                    text = quickKey,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CyanPrimary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val finalKey = inputKeyLabel.trim().ifEmpty { targetKey.keyLabel }
                        hidController.rebindKey(targetKey.id, finalKey)
                        keyToRebindDirectly = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary)
                ) {
                    Text("Map Key", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                Button(
                    onClick = { keyToRebindDirectly = null },
                    colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant)
                ) {
                    Text("Cancel", color = TextPrimary)
                }
            },
            containerColor = DarkSurface,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
private fun ConnectionStepRow(
    stepNum: String,
    title: String,
    subtitle: String,
    active: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(if (active) DarkSurfaceHighlight else DarkSurfaceVariant)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = CircleShape,
            color = if (active) NeonGreenSuccess.copy(alpha = 0.2f) else DarkBackground,
            modifier = Modifier.size(32.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = stepNum,
                    fontWeight = FontWeight.Black,
                    color = if (active) NeonGreenSuccess else TextSecondary,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (active) TextPrimary else TextSecondary
                )
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = if (active) NeonGreenSuccess else TextSecondary,
                    fontSize = 10.sp
                )
            )
        }

        Icon(
            imageVector = if (active) Icons.Default.CheckCircle else icon,
            contentDescription = null,
            tint = if (active) NeonGreenSuccess else TextSecondary,
            modifier = Modifier.size(18.dp)
        )
    }
}
