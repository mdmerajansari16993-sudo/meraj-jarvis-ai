package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.GoldAccent
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun ArcReactorOrb(
    modifier: Modifier = Modifier,
    sizeDp: Dp = 120.dp,
    isActive: Boolean = true,
    isSpeaking: Boolean = false,
    onClick: () -> Unit = {}
) {
    val infiniteTransition = rememberInfiniteTransition(label = "arc_reactor")

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (isSpeaking) 2500 else 6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    val counterRotation by infiniteTransition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (isSpeaking) 3500 else 9000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "counter_rotation"
    )

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (isSpeaking) 600 else 1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Box(
        modifier = modifier
            .size(sizeDp)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(sizeDp)) {
            val center = Offset(size.width / 2, size.height / 2)
            val outerRadius = (size.minDimension / 2) * 0.9f
            val midRadius = outerRadius * 0.72f
            val innerRadius = outerRadius * 0.45f * (if (isActive) pulseScale else 0.9f)

            // Outer glowing ring
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        CyanPrimary.copy(alpha = 0.35f),
                        CyanPrimary.copy(alpha = 0.05f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = outerRadius * 1.15f
                ),
                radius = outerRadius * 1.15f,
                center = center
            )

            // Outer dashed tech ring
            rotate(rotation, pivot = center) {
                drawCircle(
                    color = CyanPrimary.copy(alpha = 0.7f),
                    radius = outerRadius,
                    center = center,
                    style = Stroke(width = 3.dp.toPx())
                )

                val notchCount = 12
                for (i in 0 until notchCount) {
                    val angle = Math.toRadians((i * (360.0 / notchCount)))
                    val startX = (center.x + (outerRadius - 6.dp.toPx()) * cos(angle)).toFloat()
                    val startY = (center.y + (outerRadius - 6.dp.toPx()) * sin(angle)).toFloat()
                    val endX = (center.x + (outerRadius + 2.dp.toPx()) * cos(angle)).toFloat()
                    val endY = (center.y + (outerRadius + 2.dp.toPx()) * sin(angle)).toFloat()
                    drawLine(
                        color = GoldAccent.copy(alpha = 0.8f),
                        start = Offset(startX, startY),
                        end = Offset(endX, endY),
                        strokeWidth = 2.dp.toPx()
                    )
                }
            }

            // Middle counter-rotating ring
            rotate(counterRotation, pivot = center) {
                drawCircle(
                    color = GoldAccent.copy(alpha = 0.6f),
                    radius = midRadius,
                    center = center,
                    style = Stroke(width = 2.dp.toPx())
                )

                val innerNotches = 8
                for (i in 0 until innerNotches) {
                    val angle = Math.toRadians((i * (360.0 / innerNotches)))
                    val pX = (center.x + midRadius * cos(angle)).toFloat()
                    val pY = (center.y + midRadius * sin(angle)).toFloat()
                    drawCircle(
                        color = CyanPrimary,
                        radius = 2.5.dp.toPx(),
                        center = Offset(pX, pY)
                    )
                }
            }

            // Inner Core Arc Reactor
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White,
                        CyanPrimary,
                        CyanPrimary.copy(alpha = 0.4f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = innerRadius
                ),
                radius = innerRadius,
                center = center
            )
        }
    }
}
