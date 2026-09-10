package com.kunvarpreet.skirk.widget.battery

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.min

/**
 * Circular ring battery design.
 * Features an animated circular arc gauge, centered percentage, and charging status badge.
 */
@Composable
fun RingBatteryDesign(
    batteryInfo: BatteryInfo,
    config: BatteryWidgetConfig,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = batteryInfo.percentage / 100f,
        animationSpec = tween(durationMillis = 600),
        label = "BatteryRingProgress"
    )

    val ringColor = when {
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
                Brush.radialGradient(
                    listOf(Color(0xFF064E3B).copy(alpha = 0.5f), Color(0xFF022C22), Color(0xFF03100B))
                )
            )
            .border(1.dp, ringColor.copy(alpha = 0.25f), RoundedCornerShape(20.dp))
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .aspectRatio(1f),
                contentAlignment = Alignment.Center
            ) {
                // Circular Ring Gauge
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidth = 8.dp.toPx()
                    val diameter = min(size.width, size.height) - strokeWidth
                    val topLeft = Offset(
                        (size.width - diameter) / 2f,
                        (size.height - diameter) / 2f
                    )

                    // Background Track
                    drawArc(
                        color = Color.White.copy(alpha = 0.10f),
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        topLeft = topLeft,
                        size = Size(diameter, diameter),
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )

                    // Active Progress Arc
                    drawArc(
                        color = ringColor,
                        startAngle = -90f,
                        sweepAngle = animatedProgress * 360f,
                        useCenter = false,
                        topLeft = topLeft,
                        size = Size(diameter, diameter),
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                }

                // Centered Percentage
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${batteryInfo.percentage}%",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFF8FAFC)
                    )
                    if (batteryInfo.isCharging) {
                        Text(
                            text = "⚡ Charging",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF34D399)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Power Source & Status Badge
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = ringColor.copy(alpha = 0.15f),
                border = androidx.compose.foundation.BorderStroke(0.5.dp, ringColor.copy(alpha = 0.35f))
            ) {
                Text(
                    text = if (batteryInfo.isCharging) batteryInfo.source.displayName else "Battery",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = ringColor,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }

            if (config.showTemperature) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${String.format("%.1f", batteryInfo.temperatureCelsius)}°C • ${batteryInfo.health}",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF6EE7B7)
                )
            }
        }
    }
}

@Preview(widthDp = 240, heightDp = 220)
@Composable
fun RingBatteryDesignPreview() {
    RingBatteryDesign(
        batteryInfo = BatteryInfo.Sample,
        config = BatteryWidgetConfig()
    )
}
