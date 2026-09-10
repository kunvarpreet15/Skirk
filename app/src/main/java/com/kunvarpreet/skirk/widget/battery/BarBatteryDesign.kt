package com.kunvarpreet.skirk.widget.battery

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Horizontal Bar battery design.
 * Features an authentic battery cell gauge with animated fluid fill and power source tags.
 */
@Composable
fun BarBatteryDesign(
    batteryInfo: BatteryInfo,
    config: BatteryWidgetConfig,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = (batteryInfo.percentage / 100f).coerceIn(0.02f, 1f),
        animationSpec = tween(durationMillis = 600),
        label = "BatteryBarProgress"
    )

    val barColor = when {
        batteryInfo.isCharging -> Color(0xFF10B981) // Emerald Green
        batteryInfo.percentage <= 20 -> Color(0xFFEF4444) // Red alert
        batteryInfo.percentage <= 40 -> Color(0xFFF59E0B) // Amber warning
        else -> Color(0xFF10B981)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFF064E3B).copy(alpha = 0.4f), Color(0xFF021C16))
                )
            )
            .border(1.dp, Color.White.copy(alpha = 0.10f), RoundedCornerShape(20.dp))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Battery Header: Percentage & Charging Flag
            Row(
                modifier = Modifier.fillMaxWidth(0.9f),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = "${batteryInfo.percentage}%",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFFF8FAFC)
                )

                if (batteryInfo.isCharging) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFF10B981).copy(alpha = 0.20f),
                        border = androidx.compose.foundation.BorderStroke(0.5.dp, Color(0xFF10B981))
                    ) {
                        Text(
                            text = "⚡ CHARGING",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF34D399),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                } else {
                    Text(
                        text = "DISCHARGING",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF94A3B8)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Battery Cell Gauge with Terminal Cap
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {
                // Main battery container
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(28.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White.copy(alpha = 0.08f))
                        .border(1.5.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(8.dp))
                        .padding(3.dp)
                ) {
                    // Fluid fill
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(animatedProgress)
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(barColor.copy(alpha = 0.7f), barColor)
                                )
                            )
                    )
                }

                // Battery positive terminal nub
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .height(12.dp)
                        .clip(RoundedCornerShape(topEnd = 2.dp, bottomEnd = 2.dp))
                        .background(Color.White.copy(alpha = 0.35f))
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Power Source & Health Subtitle
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (batteryInfo.isCharging) batteryInfo.source.displayName else "Battery Power",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF6EE7B7)
                )

                if (config.showTemperature) {
                    Text(
                        text = "• ${String.format("%.1f", batteryInfo.temperatureCelsius)}°C",
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }
        }
    }
}

@Preview(widthDp = 300, heightDp = 180)
@Composable
fun BarBatteryDesignPreview() {
    BarBatteryDesign(
        batteryInfo = BatteryInfo.Sample,
        config = BatteryWidgetConfig()
    )
}
