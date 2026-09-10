package com.kunvarpreet.skirk.widget.digitalclock

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kunvarpreet.skirk.widget.common.TimeSnapshot
import java.time.LocalDateTime

/**
 * Modern structured digital clock design.
 * Features capitalized day header, sleek digital time with seconds pill, and formatted date footer.
 */
@Composable
fun ModernDigitalClock(
    timeSnapshot: TimeSnapshot,
    config: DigitalClockConfig,
    modifier: Modifier = Modifier
) {
    val timeText = timeSnapshot.formatTime(is24Hour = config.is24Hour, showSeconds = false)

    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF131C31), Color(0xFF0B101D))
                )
            )
            .border(1.dp, Color(0xFF38BDF8).copy(alpha = 0.25f), RoundedCornerShape(20.dp))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Day of Week Header Pill
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFF38BDF8).copy(alpha = 0.15f),
                border = androidx.compose.foundation.BorderStroke(0.5.dp, Color(0xFF38BDF8).copy(alpha = 0.35f))
            ) {
                Text(
                    text = timeSnapshot.dayOfWeek.uppercase(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp,
                    color = Color(0xFF38BDF8),
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Time with optional seconds / AM PM
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = timeText,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif,
                    letterSpacing = (-1).sp,
                    color = Color(0xFFF1F5F9),
                    textAlign = TextAlign.Center
                )

                if (config.showSeconds) {
                    Text(
                        text = ":${String.format("%02d", timeSnapshot.seconds)}",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF38BDF8),
                        modifier = Modifier.padding(bottom = 6.dp, start = 2.dp)
                    )
                }

                if (!config.is24Hour) {
                    Text(
                        text = if (timeSnapshot.isAm) " AM" else " PM",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF94A3B8),
                        modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
                    )
                }
            }

            if (config.showDate) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "${timeSnapshot.dayOfMonth} ${timeSnapshot.monthName.uppercase()}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.5.sp,
                    color = Color(0xFF94A3B8),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Preview(widthDp = 300, heightDp = 200)
@Composable
fun ModernDigitalClockPreview() {
    val sampleTime = TimeSnapshot(LocalDateTime.of(2026, 9, 10, 21, 42, 18))
    ModernDigitalClock(
        timeSnapshot = sampleTime,
        config = DigitalClockConfig(is24Hour = true, showSeconds = true, showDate = true)
    )
}
