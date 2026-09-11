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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kunvarpreet.skirk.system.model.SystemDashboardData

@Composable
fun GridSystemDashboardDesign(
    data: SystemDashboardData,
    config: SystemDashboardConfig,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF141417))
            .padding(12.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (config.showRam) {
                MetricCard(
                    title = "RAM",
                    value = "${data.ram.usedPercentage}%",
                    subValue = "${data.ram.usedGbFormatted} / ${data.ram.totalGbFormatted}",
                    progress = data.ram.usedPercentage / 100f,
                    iconGlyph = "🧠",
                    accentColor = Color(0xFF0A84FF),
                    modifier = Modifier.weight(1f)
                )
            }
            if (config.showStorage) {
                MetricCard(
                    title = "STORAGE",
                    value = "${data.storage.usedPercentage}%",
                    subValue = "${data.storage.usedGbFormatted} / ${data.storage.totalGbFormatted}",
                    progress = data.storage.usedPercentage / 100f,
                    iconGlyph = "💾",
                    accentColor = Color(0xFFBF5AF2),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Bottom row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (config.showBattery) {
                val batteryAccent = if (data.battery.percentage <= 20) Color(0xFFFF453A) else Color(0xFF30D158)
                val battGlyph = if (data.battery.isCharging) "⚡" else "🔋"
                MetricCard(
                    title = "BATTERY",
                    value = "${data.battery.percentage}%",
                    subValue = if (data.battery.isCharging) "Charging" else "On Battery",
                    progress = data.battery.percentage / 100f,
                    iconGlyph = battGlyph,
                    accentColor = batteryAccent,
                    modifier = Modifier.weight(1f)
                )
            }
            if (config.showNetwork) {
                val netColor = if (data.network.isConnected) Color(0xFF32D74B) else Color(0xFF8E8E93)
                val netGlyph = if (data.network.isConnected) "📶" else "📵"
                MetricCard(
                    title = "NETWORK",
                    value = data.network.displayLabel,
                    subValue = if (data.network.isConnected) "Connected" else "No Connection",
                    progress = if (data.network.isConnected) 1.0f else 0.0f,
                    iconGlyph = netGlyph,
                    accentColor = netColor,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    subValue: String,
    progress: Float,
    iconGlyph: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF1E2024))
            .padding(10.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp,
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 10.sp
                    )
                )
                Text(
                    text = iconGlyph,
                    fontSize = 13.sp
                )
            }

            // Value & Subvalue
            Column {
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 18.sp
                    )
                )
                Text(
                    text = subValue,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color.White.copy(alpha = 0.55f),
                        fontSize = 10.sp
                    ),
                    maxLines = 1
                )
            }

            // Progress bar
            LinearProgressIndicator(
                progress = { progress.coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = accentColor,
                trackColor = Color(0xFF2C2F36)
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000, widthDp = 340, heightDp = 240)
@Composable
private fun GridSystemDashboardDesignPreview() {
    GridSystemDashboardDesign(
        data = SystemDashboardData.sample(),
        config = SystemDashboardConfig()
    )
}
