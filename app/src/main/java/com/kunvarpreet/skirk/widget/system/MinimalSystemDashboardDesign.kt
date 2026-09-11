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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kunvarpreet.skirk.system.model.SystemDashboardData

@Composable
fun MinimalSystemDashboardDesign(
    data: SystemDashboardData,
    config: SystemDashboardConfig,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF0F1012))
            .padding(14.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Minimalist Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "SYS / STATS",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.45f),
                    letterSpacing = 1.sp,
                    fontSize = 10.sp
                )
            )

            Text(
                text = "${data.processorCount}x CPU",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    color = Color.White.copy(alpha = 0.35f),
                    fontSize = 10.sp
                )
            )
        }

        HorizontalDivider(color = Color(0xFF1E2024), thickness = 1.dp)

        // Row 1: RAM & STORAGE
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (config.showRam) {
                MinimalMetricItem(
                    label = "RAM",
                    value = "${data.ram.usedPercentage}%",
                    detail = "${data.ram.usedGbFormatted} USED",
                    accentColor = Color(0xFF0A84FF),
                    modifier = Modifier.weight(1f)
                )
            }
            if (config.showStorage) {
                MinimalMetricItem(
                    label = "STORAGE",
                    value = "${data.storage.usedPercentage}%",
                    detail = "${data.storage.usedGbFormatted} USED",
                    accentColor = Color(0xFFBF5AF2),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        HorizontalDivider(color = Color(0xFF1E2024), thickness = 1.dp)

        // Row 2: BATTERY & NETWORK
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (config.showBattery) {
                val battColor = if (data.battery.percentage <= 20) Color(0xFFFF453A) else Color(0xFF30D158)
                MinimalMetricItem(
                    label = "BATTERY",
                    value = "${data.battery.percentage}%",
                    detail = if (data.battery.isCharging) "CHARGING" else "DISCHARGING",
                    accentColor = battColor,
                    modifier = Modifier.weight(1f)
                )
            }
            if (config.showNetwork) {
                val netColor = if (data.network.isConnected) Color(0xFF30D158) else Color(0xFF8E8E93)
                MinimalMetricItem(
                    label = "NETWORK",
                    value = data.network.displayLabel.uppercase(),
                    detail = if (data.network.isConnected) "ONLINE" else "DISCONNECTED",
                    accentColor = netColor,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun MinimalMetricItem(
    label: String,
    value: String,
    detail: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(2.dp))
                    .background(accentColor)
                    .padding(horizontal = 4.dp, vertical = 1.dp)
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        fontSize = 9.sp
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 19.sp
            )
        )

        Text(
            text = detail,
            style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                color = Color.White.copy(alpha = 0.4f),
                fontSize = 9.sp
            )
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000, widthDp = 340, heightDp = 240)
@Composable
private fun MinimalSystemDashboardDesignPreview() {
    MinimalSystemDashboardDesign(
        data = SystemDashboardData.sample(),
        config = SystemDashboardConfig(selectedDesign = SystemDashboardConfig.DESIGN_MINIMAL)
    )
}
