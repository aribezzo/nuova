package com.alessandro.batterytracker.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alessandro.batterytracker.ui.theme.CyanAccent
import com.alessandro.batterytracker.ui.theme.GreenAccent
import com.alessandro.batterytracker.ui.theme.OrangeAccent
import com.alessandro.batterytracker.ui.theme.RedAccent
import com.alessandro.batterytracker.ui.theme.TextPrimary
import kotlin.math.sin

@Composable
fun BatteryWaveIndicator(
    percentage: Int,
    isCharging: Boolean,
    modifier: Modifier = Modifier,
    diameter: androidx.compose.ui.unit.Dp = 220.dp
) {
    val targetFraction = (percentage.coerceIn(0, 100)) / 100f
    val animatedFraction by animateFloatAsState(
        targetValue = targetFraction,
        animationSpec = tween(900),
        label = "levelAnim"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "wave")
    val wavePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(if (isCharging) 1400 else 3200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    val waveColor = when {
        isCharging -> GreenAccent
        percentage <= 20 -> RedAccent
        percentage <= 45 -> OrangeAccent
        else -> CyanAccent
    }

    Box(modifier = modifier.size(diameter), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(diameter)) {
            val strokeWidth = 10.dp.toPx()
            val radius = this.size.minDimension / 2f - strokeWidth
            val center = Offset(this.size.width / 2f, this.size.height / 2f)

            // outer ring (container outline)
            drawCircle(
                color = Color(0xFF2A3350),
                radius = radius,
                center = center,
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeWidth)
            )

            // clip to inner circle then draw wave fill
            val clipPath = Path().apply {
                addOval(androidx.compose.ui.geometry.Rect(center = center, radius = radius - strokeWidth / 2f))
            }
            clipPath(clipPath) {
                val waveHeight = radius * 0.06f
                val baseY = center.y + radius - (radius * 2f * animatedFraction)
                val wavePath = Path()
                val width = this.size.width
                wavePath.moveTo(0f, baseY)
                var x = 0f
                val step = 4f
                val piFloat = Math.PI.toFloat()
                while (x <= width) {
                    val y = baseY + sin(((x / width) * 4f * piFloat + wavePhase).toDouble()).toFloat() * waveHeight
                    wavePath.lineTo(x, y)
                    x += step
                }
                wavePath.lineTo(width, this.size.height)
                wavePath.lineTo(0f, this.size.height)
                wavePath.close()

                drawPath(
                    path = wavePath,
                    brush = Brush.verticalGradient(
                        colors = listOf(waveColor.copy(alpha = 0.9f), waveColor.copy(alpha = 0.55f))
                    )
                )
            }

            // outer glow ring accent proportional to charge
            drawCircle(
                color = waveColor,
                radius = radius,
                center = center,
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3.dp.toPx())
            )
        }

        Text(
            text = "$percentage%",
            style = TextStyle(
                color = TextPrimary,
                fontSize = 42.sp,
                fontWeight = FontWeight.Bold
            )
        )
    }
}
