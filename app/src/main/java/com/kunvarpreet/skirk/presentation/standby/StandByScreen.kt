package com.kunvarpreet.skirk.presentation.standby

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kunvarpreet.skirk.domain.standby.StandByController

@Composable
fun StandByScreen(
    standByController: StandByController,
    onExitStandBy: () -> Unit,
    modifier: Modifier = Modifier
) {
    val chargingState by standByController.chargingState.collectAsState()
    val standByState by standByController.standByState.collectAsState()

    Surface(
        modifier = modifier.fillMaxSize(),
        color = Color(0xFF0C0E14) // Deep AMOLED StandBy background
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // StandBy Placeholder Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color(0xFF161922))
                        .border(
                            width = 1.5.dp,
                            color = Color(0xFF2C3246),
                            shape = RoundedCornerShape(24.dp)
                        )
                        .padding(vertical = 36.dp, horizontal = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "SKIRK",
                            style = MaterialTheme.typography.displayMedium.copy(
                                letterSpacing = 8.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.SansSerif
                            ),
                            color = Color(0xFFE2E8F0)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "STANDBY",
                            style = MaterialTheme.typography.titleLarge.copy(
                                letterSpacing = 6.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Text(
                            text = if (chargingState.isCharging) "Charging: YES" else "Charging: NO (Manual Mode)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = if (chargingState.isCharging) Color(0xFF4ADE80) else Color(0xFFFACC15)
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Source: ${chargingState.source.displayName}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF94A3B8)
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Screen Awake: ACTIVE • Landscape: ON",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF64748B)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onExitStandBy,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF252B3B),
                        contentColor = Color(0xFFE2E8F0)
                    )
                ) {
                    Text("Exit StandBy")
                }
            }
        }
    }
}
