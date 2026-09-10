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
 * Large Bold StandBy digital clock design.
 * Maximized numerals optimized for high visibility across a room in dark environments.
 */
@Composable
fun LargeDigitalClock(
    timeSnapshot: TimeSnapshot,
    config: DigitalClockConfig,
    modifier: Modifier = Modifier
) {
    val hoursStr = if (config.is24Hour) {
        String.format("%02d", timeSnapshot.hours24)
    } else {
        String.format("%d", timeSnapshot.hours12)
    }
    val minutesStr = String.format("%02d", timeSnapshot.minutes)

    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.radialGradient(
                    colors = listOf(Color(0xFF1E293B), Color(0xFF090D16))
                )
            )
            .border(1.dp, Color.White.copy(alpha = 0.10f), RoundedCornerShape(20.dp))
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "$hoursStr:$minutesStr",
                    fontSize = 68.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.SansSerif,
                    letterSpacing = (-2).sp,
                    color = Color(0xFFFFFFFF),
                    textAlign = TextAlign.Center
                )

                if (!config.is24Hour || config.showSeconds) {
                    Column(
                        modifier = Modifier.padding(start = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        if (!config.is24Hour) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color.White.copy(alpha = 0.12f)
                            ) {
                                Text(
                                    text = if (timeSnapshot.isAm) "AM" else "PM",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF38BDF8),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        if (config.showSeconds) {
                            Text(
                                text = String.format("%02d", timeSnapshot.seconds),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }
                }
            }

            if (config.showDate) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = timeSnapshot.formatDate("EEE, MMM d"),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 1.sp,
                    color = Color(0xFF64748B)
                )
            }
        }
    }
}

@Preview(widthDp = 300, heightDp = 200)
@Composable
fun LargeDigitalClockPreview() {
    val sampleTime = TimeSnapshot(LocalDateTime.of(2026, 9, 10, 21, 42, 35))
    LargeDigitalClock(
        timeSnapshot = sampleTime,
        config = DigitalClockConfig(is24Hour = false, showSeconds = true, showDate = true)
    )
}
