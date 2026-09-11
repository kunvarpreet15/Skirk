package com.kunvarpreet.skirk.widget.system

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kunvarpreet.skirk.system.model.SystemDashboardData

@Composable
fun RingsSystemDashboardDesign(
    data: SystemDashboardData,
    config: SystemDashboardConfig,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF121316))
            .padding(14.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "SYSTEM METRICS",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.2.sp,
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 10.sp
                )
            )

            // Network pill badge
            if (config.showNetwork) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF1E2026))
                        .padding(horizontal = 8.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (data.network.isConnected) "📶" else "📵",
                        fontSize = 10.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = data.network.displayLabel,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 10.sp
                        )
                    )
                }
            }
        }

        // Circular progress rings row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (config.showRam) {
                MetricRing(
                    label = "RAM",
                    percentage = data.ram.usedPercentage,
                    subtext = data.ram.usedGbFormatted,
                    accentColor = Color(0xFF0A84FF)
                )
            }
            if (config.showStorage) {
                MetricRing(
                    label = "STORAGE",
                    percentage = data.storage.usedPercentage,
                    subtext = data.storage.usedGbFormatted,
                    accentColor = Color(0xFFBF5AF2)
                )
            }
            if (config.showBattery) {
                val batteryAccent = if (data.battery.percentage <= 20) Color(0xFFFF453A) else Color(0xFF30D158)
                MetricRing(
                    label = "BATTERY",
                    percentage = data.battery.percentage,
                    subtext = if (data.battery.isCharging) "Charging" else "Discharging",
                    accentColor = batteryAccent
                )
            }
        }

        // Bottom stats info
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${data.processorCount} Cores Available",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Color.White.copy(alpha = 0.4f),
                    fontSize = 10.sp
                )
            )
            Text(
                text = "${data.ram.totalGbFormatted} Total Memory",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Color.White.copy(alpha = 0.4f),
                    fontSize = 10.sp
                )
            )
        }
    }
}

@Composable
private fun MetricRing(
    label: String,
    percentage: Int,
    subtext: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier.size(68.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                progress = { 1.0f },
                modifier = Modifier.fillMaxSize(),
                color = Color(0xFF22242A),
                strokeWidth = 6.dp
            )
            CircularProgressIndicator(
                progress = { (percentage / 100f).coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxSize(),
                color = accentColor,
                strokeWidth = 6.dp,
                strokeCap = StrokeCap.Round
            )
            Text(
                text = "$percentage%",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 13.sp
                )
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(alpha = 0.6f),
                letterSpacing = 0.6.sp,
                fontSize = 9.sp
            )
        )

        Text(
            text = subtext,
            style = MaterialTheme.typography.labelSmall.copy(
                color = Color.White.copy(alpha = 0.4f),
                fontSize = 9.sp
            )
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000, widthDp = 340, heightDp = 240)
@Composable
private fun RingsSystemDashboardDesignPreview() {
    RingsSystemDashboardDesign(
        data = SystemDashboardData.sample(),
        config = SystemDashboardConfig(selectedDesign = SystemDashboardConfig.DESIGN_RINGS)
    )
}
