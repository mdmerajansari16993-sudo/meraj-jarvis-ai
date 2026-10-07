package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shopping.ProductItem
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceHighlight
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.NeonGreenSuccess
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun LiveAutomationSimulator(
    platform: String = "Flipkart",
    searchQuery: String = "shoes for men size 5",
    products: List<ProductItem>,
    onVoiceTrigger: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    var isRunning by remember { mutableStateOf(true) }
    var stepIndex by remember { mutableIntStateOf(0) }
    var typedText by remember { mutableStateOf("") }
    var showRipple by remember { mutableStateOf(false) }
    var isItemHighlighted by remember { mutableStateOf(false) }
    var statusText by remember { mutableStateOf("Initializing Live Touch Simulation...") }

    // Virtual Touch Cursor Coordinates
    val cursorX = remember { Animatable(180f) }
    val cursorY = remember { Animatable(360f) }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse_tag")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    fun runLiveSequence() {
        coroutineScope.launch {
            isRunning = true
            typedText = ""
            isItemHighlighted = false
            stepIndex = 1
            statusText = "Jarvis Opening $platform via Accessibility & Bluetooth HID..."
            cursorX.snapTo(180f)
            cursorY.snapTo(360f)
            delay(700)

            // Step 1: Move pointer to Search Bar
            stepIndex = 2
            statusText = "Moving Virtual HID Pointer to Search Bar..."
            cursorX.animateTo(180f, tween(800, easing = FastOutSlowInEasing))
            cursorY.animateTo(52f, tween(800, easing = FastOutSlowInEasing))

            // Step 2: Click Search Bar
            showRipple = true
            statusText = "Hardware HID Click: Focused $platform Search Box"
            delay(350)
            showRipple = false

            // Step 3: Type query letter-by-letter live on screen
            stepIndex = 3
            statusText = "Injecting Query Text..."
            val fullQuery = "shoes size 5"
            for (i in 1..fullQuery.length) {
                typedText = fullQuery.substring(0, i)
                delay(90)
            }
            delay(400)

            // Step 4: Click Search Button
            cursorX.animateTo(310f, tween(300))
            showRipple = true
            statusText = "Search Dispatched. Fetching Product Catalog..."
            delay(350)
            showRipple = false

            // Step 5: Live scrolling through product cards
            stepIndex = 4
            statusText = "Live Scanning Feed: Inspecting 12 Shoes for Size 5..."
            cursorY.animateTo(160f, tween(400))
            for (scroll in 1..3) {
                listState.animateScrollToItem(scroll)
                delay(600)
            }
            listState.animateScrollToItem(0)
            delay(500)

            // Step 6: Move to Top Recommended Item
            stepIndex = 5
            cursorX.animateTo(180f, tween(600))
            cursorY.animateTo(220f, tween(600))
            isItemHighlighted = true
            statusText = "Top Value Pick Identified: Asian Jasper-02 Size 5 (₹699)"

            // Speak Dialogue
            onVoiceTrigger()
            delay(1200)

            // Step 7: Click Buy Now
            stepIndex = 6
            cursorX.animateTo(260f, tween(400))
            cursorY.animateTo(280f, tween(400))
            showRipple = true
            statusText = "Recommendation Confirmed: \"Bhai yeh theek hai, isko buy kar do.\""
            delay(400)
            showRipple = false
            isRunning = false
        }
    }

    LaunchedEffect(platform, searchQuery) {
        runLiveSequence()
    }

    Column(modifier = modifier.fillMaxWidth()) {
        // Control Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(if (isRunning) GoldAccent else NeonGreenSuccess)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isRunning) "LIVE AUTOMATION IN PROGRESS" else "AUTOMATION COMPLETE",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (isRunning) GoldAccent else NeonGreenSuccess,
                        letterSpacing = 1.sp
                    )
                )
            }

            IconButton(
                onClick = { runLiveSequence() },
                modifier = Modifier
                    .size(32.dp)
                    .testTag("replay_live_automation_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Replay,
                    contentDescription = "Replay Automation",
                    tint = CyanPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Live Status Banner
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            shape = RoundedCornerShape(8.dp),
            color = DarkSurfaceVariant
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.TouchApp,
                    contentDescription = null,
                    tint = CyanPrimary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = statusText,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextPrimary,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // Simulated Phone Frame
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .height(390.dp)
                .border(2.dp, CyanPrimary.copy(alpha = 0.6f), RoundedCornerShape(24.dp))
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFF0D131F))
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top App Bar of target app (Flipkart / Amazon)
                Surface(
                    color = if (platform == "Flipkart") Color(0xFF2874F0) else Color(0xFF131921),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = platform.uppercase(),
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    color = if (platform == "Flipkart") Color(0xFFFFE500) else Color.White,
                                    letterSpacing = 1.sp
                                )
                            )
                            Text(
                                text = "LIVE SIMULATOR",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.sp,
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Search Bar
                        Surface(
                            color = Color.White,
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = null,
                                    tint = Color.Gray,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (typedText.isEmpty()) "Search for shoes, clothes..." else typedText,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = if (typedText.isEmpty()) Color.Gray else Color.Black,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 12.sp
                                    )
                                )
                            }
                        }
                    }
                }

                // Filter Chips Bar
                Surface(
                    color = DarkSurfaceHighlight,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            color = CyanPrimary.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "✓ Size: 5",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = CyanPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                ),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Surface(
                            color = Color.DarkGray,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "Rating: 4.0★+",
                                style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary, fontSize = 10.sp),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Surface(
                            color = Color.DarkGray,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "Price: Low to High",
                                style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary, fontSize = 10.sp),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                // Product Feed Inside Simulated Viewport
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    items(products) { item ->
                        val isRec = item.isRecommended && isItemHighlighted
                        Surface(
                            color = if (isRec) DarkSurfaceVariant else DarkSurface,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .border(
                                    if (isRec) 2.dp else 1.dp,
                                    if (isRec) GoldAccent else DarkSurfaceHighlight,
                                    RoundedCornerShape(10.dp)
                                )
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                if (isRec) {
                                    Surface(
                                        color = GoldAccent.copy(alpha = 0.2f),
                                        shape = RoundedCornerShape(4.dp),
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    ) {
                                        Text(
                                            text = "★ JARVIS RECOMMENDED: \"Bhai yeh theek hai, isko buy kar do.\"",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = GoldAccent,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 9.sp
                                            ),
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = item.name,
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = TextPrimary,
                                                fontSize = 12.sp
                                            ),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = "${item.size} • ${item.rating}★ (${item.reviewCount})",
                                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 10.sp)
                                        )
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = "₹${item.discountedPrice}",
                                            style = MaterialTheme.typography.labelLarge.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = if (isRec) GoldAccent else CyanPrimary
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Simulated Floating Holographic Touch Pointer & Ripple
            Box(
                modifier = Modifier
                    .offset { IntOffset(cursorX.value.toInt(), cursorY.value.toInt()) }
                    .size(44.dp),
                contentAlignment = Alignment.Center
            ) {
                // Expanding tap ripple wave
                if (showRipple) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .border(2.dp, CyanPrimary, CircleShape)
                            .background(CyanPrimary.copy(alpha = 0.35f))
                    )
                }

                // Holographic cursor pointer
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .clip(CircleShape)
                        .border(1.5.dp, Color.White, CircleShape)
                        .background(CyanPrimary)
                )
            }
        }
    }
}
