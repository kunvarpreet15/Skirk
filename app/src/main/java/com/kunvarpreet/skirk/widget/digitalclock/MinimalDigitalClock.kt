package com.kunvarpreet.skirk.widget.digitalclock

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
 * Minimalist digital clock design.
 * Features clean, elegant typography with large time and subtle date below.
 */
@Composable
fun MinimalDigitalClock(
    timeSnapshot: TimeSnapshot,
    config: DigitalClockConfig,
    modifier: Modifier = Modifier
) {
    val timeText = timeSnapshot.formatTime(is24Hour = config.is24Hour, showSeconds = config.showSeconds)
    val dateText = timeSnapshot.formatDate("EEEE, d MMMM")

    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFF0F172A), Color(0xFF020617))
                )
            )
            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(20.dp))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = timeText,
                fontSize = 54.sp,
                fontWeight = FontWeight.Light,
                fontFamily = FontFamily.SansSerif,
                letterSpacing = (-1).sp,
                color = Color(0xFFF8FAFC),
                textAlign = TextAlign.Center
            )

            if (config.showDate) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = dateText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    letterSpacing = 0.5.sp,
                    color = Color(0xFF94A3B8),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Preview(widthDp = 300, heightDp = 200)
@Composable
fun MinimalDigitalClockPreview() {
    val sampleTime = TimeSnapshot(LocalDateTime.of(2026, 9, 10, 21, 42, 15))
    MinimalDigitalClock(
        timeSnapshot = sampleTime,
        config = DigitalClockConfig(is24Hour = true, showSeconds = false, showDate = true)
    )
}
