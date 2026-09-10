package com.kunvarpreet.skirk.widget.battery

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Minimalist battery design.
 * Features a compact glanceable layout with large percentage and battery capsule icon.
 */
@Composable
fun MinimalBatteryDesign(
    batteryInfo: BatteryInfo,
    config: BatteryWidgetConfig,
    modifier: Modifier = Modifier
) {
    val levelFraction = (batteryInfo.percentage / 100f).coerceIn(0.05f, 1f)

    val accentColor = when {
        batteryInfo.isCharging -> Color(0xFF10B981)
        batteryInfo.percentage <= 20 -> Color(0xFFEF4444)
        batteryInfo.percentage <= 40 -> Color(0xFFF59E0B)
        else -> Color(0xFF10B981)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF062319))
            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(20.dp))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Compact Battery Capsule
                Box(
                    modifier = Modifier
                        .width(36.dp)
                        .height(18.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .border(1.5.dp, Color.White.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                        .padding(2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(levelFraction)
                            .clip(RoundedCornerShape(2.dp))
                            .background(accentColor)
                    )
                }

                Text(
                    text = "${batteryInfo.percentage}%",
                    fontSize = 44.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFF8FAFC)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Surface(
                shape = RoundedCornerShape(6.dp),
                color = if (batteryInfo.isCharging) Color(0xFF10B981).copy(alpha = 0.15f) else Color.White.copy(alpha = 0.06f)
            ) {
                Text(
                    text = if (batteryInfo.isCharging) "⚡ ${batteryInfo.source.displayName}" else "Battery",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (batteryInfo.isCharging) Color(0xFF34D399) else Color(0xFF94A3B8),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
        }
    }
}

@Preview(widthDp = 240, heightDp = 180)
@Composable
fun MinimalBatteryDesignPreview() {
    MinimalBatteryDesign(
        batteryInfo = BatteryInfo.Sample,
        config = BatteryWidgetConfig()
    )
}
