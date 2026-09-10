package com.kunvarpreet.skirk.widget.analogclock

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
 * Classic analog clock design.
 * Traditional circular dial with radial hour markers, tapered hands, and a central hub.
 */
@Composable
fun ClassicAnalogClock(
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
                    listOf(Color(0xFF1E293B), Color(0xFF0F172A), Color(0xFF020617))
                )
            )
            .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(20.dp))
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

            // Outer dial rim
            drawCircle(
                color = Color.White.copy(alpha = 0.08f),
                radius = radius,
                center = center,
                style = Stroke(width = 2.dp.toPx())
            )

            // Inner subtle track
            drawCircle(
                color = Color.White.copy(alpha = 0.04f),
                radius = radius * 0.96f,
                center = center
            )

            // Draw 60 tick marks
            for (i in 0 until 60) {
                val angleRad = Math.toRadians((i * 6.0) - 90.0)
                val isHour = i % 5 == 0
                val tickLength = if (isHour) radius * 0.14f else radius * 0.06f
                val tickWidth = if (isHour) 2.5.dp.toPx() else 1.dp.toPx()
                val tickColor = if (isHour) Color(0xFFE2E8F0) else Color.White.copy(alpha = 0.25f)

                val startX = center.x + (radius - tickLength - 4.dp.toPx()) * cos(angleRad).toFloat()
                val startY = center.y + (radius - tickLength - 4.dp.toPx()) * sin(angleRad).toFloat()
                val endX = center.x + (radius - 4.dp.toPx()) * cos(angleRad).toFloat()
                val endY = center.y + (radius - 4.dp.toPx()) * sin(angleRad).toFloat()

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

            // Hour Hand
            val hourLength = radius * 0.52f
            val hourEnd = Offset(
                center.x + hourLength * cos(hourAngleRad).toFloat(),
                center.y + hourLength * sin(hourAngleRad).toFloat()
            )
            drawLine(
                color = Color(0xFFF8FAFC),
                start = center,
                end = hourEnd,
                strokeWidth = 4.5.dp.toPx(),
                cap = StrokeCap.Round
            )

            // Minute Hand
            val minuteLength = radius * 0.74f
            val minuteEnd = Offset(
                center.x + minuteLength * cos(minuteAngleRad).toFloat(),
                center.y + minuteLength * sin(minuteAngleRad).toFloat()
            )
            drawLine(
                color = Color(0xFFCBD5E1),
                start = center,
                end = minuteEnd,
                strokeWidth = 3.dp.toPx(),
                cap = StrokeCap.Round
            )

            // Second Hand
            if (config.showSeconds) {
                val secondAngleRad = Math.toRadians((seconds * 6.0) - 90.0)
                val secondLength = radius * 0.82f
                val secondTailLength = radius * 0.18f

                val secondEnd = Offset(
                    center.x + secondLength * cos(secondAngleRad).toFloat(),
                    center.y + secondLength * sin(secondAngleRad).toFloat()
                )
                val secondTail = Offset(
                    center.x - secondTailLength * cos(secondAngleRad).toFloat(),
                    center.y - secondTailLength * sin(secondAngleRad).toFloat()
                )

                drawLine(
                    color = Color(0xFFF59E0B), // Warm amber second hand
                    start = secondTail,
                    end = secondEnd,
                    strokeWidth = 1.8.dp.toPx(),
                    cap = StrokeCap.Round
                )

                // Second hand center pivot
                drawCircle(
                    color = Color(0xFFF59E0B),
                    radius = 3.5.dp.toPx(),
                    center = center
                )
            }

            // Center Hub Nut
            drawCircle(
                color = Color(0xFF0F172A),
                radius = 5.dp.toPx(),
                center = center
            )
            drawCircle(
                color = Color(0xFFF8FAFC),
                radius = 2.dp.toPx(),
                center = center
            )
        }
    }
}

@Preview(widthDp = 240, heightDp = 240)
@Composable
fun ClassicAnalogClockPreview() {
    val sampleTime = TimeSnapshot(LocalDateTime.of(2026, 9, 10, 10, 10, 30))
    ClassicAnalogClock(
        timeSnapshot = sampleTime,
        config = AnalogClockConfig(showSeconds = true)
    )
}
