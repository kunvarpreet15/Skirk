package com.kunvarpreet.skirk.widget.analogclock

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kunvarpreet.skirk.widget.common.TimeSnapshot
import java.time.LocalDateTime
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

/**
 * Chronograph / Technical instrument analog clock design.
 * Features inner concentric tracks, sub-dial styling, and high-contrast sword hands.
 */
@Composable
fun ChronographAnalogClock(
    timeSnapshot: TimeSnapshot,
    config: AnalogClockConfig,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.radialGradient(
                    listOf(Color(0xFF18181B), Color(0xFF09090B))
                )
            )
            .border(1.dp, Color(0xFFF97316).copy(alpha = 0.25f), RoundedCornerShape(20.dp))
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize(0.92f)
                .aspectRatio(1f)
        ) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = min(size.width, size.height) / 2f

            // Outer Tachymeter / Chronograph Track
            drawCircle(
                color = Color.White.copy(alpha = 0.15f),
                radius = radius,
                center = center,
                style = Stroke(width = 1.5.dp.toPx())
            )

            // Inner Accent Ring
            drawCircle(
                color = Color.White.copy(alpha = 0.05f),
                radius = radius * 0.76f,
                center = center,
                style = Stroke(width = 1.dp.toPx())
            )

            // Stylized Sub-dial (Seconds / Instrument Accent at 6 o'clock)
            val subDialCenter = Offset(center.x, center.y + radius * 0.38f)
            val subDialRadius = radius * 0.24f
            drawCircle(
                color = Color.Black.copy(alpha = 0.35f),
                radius = subDialRadius,
                center = subDialCenter
            )
            drawCircle(
                color = Color(0xFFF97316).copy(alpha = 0.3f),
                radius = subDialRadius,
                center = subDialCenter,
                style = Stroke(width = 1.dp.toPx())
            )

            // Draw 60 precision tick marks
            for (i in 0 until 60) {
                val angleRad = Math.toRadians((i * 6.0) - 90.0)
                val isMajorHour = i % 15 == 0 // 12, 3, 6, 9
                val isHour = i % 5 == 0

                val tickLength = when {
                    isMajorHour -> radius * 0.16f
                    isHour -> radius * 0.10f
                    else -> radius * 0.04f
                }
                val tickWidth = when {
                    isMajorHour -> 3.dp.toPx()
                    isHour -> 1.8.dp.toPx()
                    else -> 0.8.dp.toPx()
                }
                val tickColor = when {
                    isMajorHour -> Color(0xFFF97316)
                    isHour -> Color(0xFFF4F4F5)
                    else -> Color.White.copy(alpha = 0.20f)
                }

                val startX = center.x + (radius - tickLength - 3.dp.toPx()) * cos(angleRad).toFloat()
                val startY = center.y + (radius - tickLength - 3.dp.toPx()) * sin(angleRad).toFloat()
                val endX = center.x + (radius - 3.dp.toPx()) * cos(angleRad).toFloat()
                val endY = center.y + (radius - 3.dp.toPx()) * sin(angleRad).toFloat()

                drawLine(
                    color = tickColor,
                    start = Offset(startX, startY),
                    end = Offset(endX, endY),
                    strokeWidth = tickWidth,
                    cap = StrokeCap.Round
                )
            }

            // Calculate hand angles
            val hours = timeSnapshot.hours12
            val minutes = timeSnapshot.minutes
            val seconds = timeSnapshot.seconds

            val hourAngleRad = Math.toRadians(((hours + minutes / 60f + seconds / 3600f) * 30.0) - 90.0)
            val minuteAngleRad = Math.toRadians(((minutes + seconds / 60f) * 6.0) - 90.0)

            // Sword-style Hour Hand
            val hourLength = radius * 0.50f
            val hourEnd = Offset(
                center.x + hourLength * cos(hourAngleRad).toFloat(),
                center.y + hourLength * sin(hourAngleRad).toFloat()
            )
            drawLine(
                color = Color(0xFFFAFAFA),
                start = center,
                end = hourEnd,
                strokeWidth = 4.5.dp.toPx(),
                cap = StrokeCap.Round
            )

            // Sword-style Minute Hand
            val minuteLength = radius * 0.75f
            val minuteEnd = Offset(
                center.x + minuteLength * cos(minuteAngleRad).toFloat(),
                center.y + minuteLength * sin(minuteAngleRad).toFloat()
            )
            drawLine(
                color = Color(0xFFE4E4E7),
                start = center,
                end = minuteEnd,
                strokeWidth = 3.2.dp.toPx(),
                cap = StrokeCap.Round
            )

            // High-visibility Orange Chrono Second Hand
            if (config.showSeconds) {
                val secondAngleRad = Math.toRadians((seconds * 6.0) - 90.0)
                val secondLength = radius * 0.84f
                val secondTailLength = radius * 0.20f

                val secondEnd = Offset(
                    center.x + secondLength * cos(secondAngleRad).toFloat(),
                    center.y + secondLength * sin(secondAngleRad).toFloat()
                )
                val secondTail = Offset(
                    center.x - secondTailLength * cos(secondAngleRad).toFloat(),
                    center.y - secondTailLength * sin(secondAngleRad).toFloat()
                )

                drawLine(
                    color = Color(0xFFF97316),
                    start = secondTail,
                    end = secondEnd,
                    strokeWidth = 1.8.dp.toPx(),
                    cap = StrokeCap.Round
                )

                drawCircle(
                    color = Color(0xFFF97316),
                    radius = 3.5.dp.toPx(),
                    center = center
                )
            }

            // Center Pin
            drawCircle(
                color = Color(0xFF09090B),
                radius = 2.dp.toPx(),
                center = center
            )
        }
    }
}

@Preview(widthDp = 240, heightDp = 240)
@Composable
fun ChronographAnalogClockPreview() {
    val sampleTime = TimeSnapshot(LocalDateTime.of(2026, 9, 10, 10, 10, 20))
    ChronographAnalogClock(
        timeSnapshot = sampleTime,
        config = AnalogClockConfig(showSeconds = true)
    )
}
