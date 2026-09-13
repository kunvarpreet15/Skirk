package com.kunvarpreet.skirk.widget.analogclock

import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kunvarpreet.skirk.widget.common.TimeSnapshot
import java.time.LocalDateTime
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

/**
 * Bauhaus / Modernist analog clock design.
 * Features minimalist geometric baton hour markers, rectilinear hands, and clean aesthetics.
 */
@Composable
fun BauhausAnalogClock(
    timeSnapshot: TimeSnapshot,
    config: AnalogClockConfig,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFF0D1527), Color(0xFF050B14))
                )
            )
            .border(1.dp, Color(0xFF38BDF8).copy(alpha = 0.2f), RoundedCornerShape(20.dp))
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

            // 12 Geometric Baton Hour Markers
            for (i in 0 until 12) {
                val angleRad = Math.toRadians((i * 30.0) - 90.0)
                val isCardinal = i % 3 == 0 // 12, 3, 6, 9
                val markerLength = if (isCardinal) radius * 0.18f else radius * 0.10f
                val markerWidth = if (isCardinal) 3.5.dp.toPx() else 2.dp.toPx()
                val markerColor = if (isCardinal) Color(0xFF38BDF8) else Color.White.copy(alpha = 0.45f)

                val startX = center.x + (radius - markerLength - 6.dp.toPx()) * cos(angleRad).toFloat()
                val startY = center.y + (radius - markerLength - 6.dp.toPx()) * sin(angleRad).toFloat()
                val endX = center.x + (radius - 6.dp.toPx()) * cos(angleRad).toFloat()
                val endY = center.y + (radius - 6.dp.toPx()) * sin(angleRad).toFloat()

                drawLine(
                    color = markerColor,
                    start = Offset(startX, startY),
                    end = Offset(endX, endY),
                    strokeWidth = markerWidth,
                    cap = StrokeCap.Square
                )
            }

            // Calculate hand angles
            val hours = timeSnapshot.hours12
            val minutes = timeSnapshot.minutes
            val seconds = timeSnapshot.seconds

            val hourAngleRad = Math.toRadians(((hours + minutes / 60f + seconds / 3600f) * 30.0) - 90.0)
            val minuteAngleRad = Math.toRadians(((minutes + seconds / 60f) * 6.0) - 90.0)

            // Hour Hand - Rectilinear Baton
            val hourLength = radius * 0.48f
            val hourEnd = Offset(
                center.x + hourLength * cos(hourAngleRad).toFloat(),
                center.y + hourLength * sin(hourAngleRad).toFloat()
            )
            drawLine(
                color = Color(0xFFFFFFFF),
                start = center,
                end = hourEnd,
                strokeWidth = 5.dp.toPx(),
                cap = StrokeCap.Square
            )

            // Minute Hand - Sleek Long Baton
            val minuteLength = radius * 0.72f
            val minuteEnd = Offset(
                center.x + minuteLength * cos(minuteAngleRad).toFloat(),
                center.y + minuteLength * sin(minuteAngleRad).toFloat()
            )
            drawLine(
                color = Color(0xFFE2E8F0),
                start = center,
                end = minuteEnd,
                strokeWidth = 3.dp.toPx(),
                cap = StrokeCap.Square
            )

            // Second Hand - Slender Cyan Needle
            if (config.showSeconds) {
                val secondAngleRad = Math.toRadians((seconds * 6.0) - 90.0)
                val secondLength = radius * 0.82f
                val secondTailLength = radius * 0.12f

                val secondEnd = Offset(
                    center.x + secondLength * cos(secondAngleRad).toFloat(),
                    center.y + secondLength * sin(secondAngleRad).toFloat()
                )
                val secondTail = Offset(
                    center.x - secondTailLength * cos(secondAngleRad).toFloat(),
                    center.y - secondTailLength * sin(secondAngleRad).toFloat()
                )

                drawLine(
                    color = Color(0xFF38BDF8),
                    start = secondTail,
                    end = secondEnd,
                    strokeWidth = 1.5.dp.toPx(),
                    cap = StrokeCap.Round
                )

                drawCircle(
                    color = Color(0xFF38BDF8),
                    radius = 3.dp.toPx(),
                    center = center
                )
            }

            // Pivot Dot
            drawCircle(
                color = Color(0xFF0D1527),
                radius = 4.dp.toPx(),
                center = center
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(widthDp = 240, heightDp = 240)
@Composable
fun BauhausAnalogClockPreview() {
    val sampleTime = TimeSnapshot(LocalDateTime.of(2026, 9, 10, 10, 10, 45))
    BauhausAnalogClock(
        timeSnapshot = sampleTime,
        config = AnalogClockConfig(showSeconds = true)
    )
}
