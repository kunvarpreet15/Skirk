package com.kunvarpreet.skirk.presentation.standby

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kunvarpreet.skirk.domain.standby.StandByController
import com.kunvarpreet.skirk.presentation.dashboard.DashboardView
import com.kunvarpreet.skirk.presentation.dashboard.DashboardViewModel
import com.kunvarpreet.skirk.widget.core.WidgetRegistry

/**
 * Full-screen StandBy display experience.
 * Combines real-time power status with the live model-driven DashboardView.
 */
@Composable
fun StandByScreen(
    dashboardViewModel: DashboardViewModel,
    standByController: StandByController,
    widgetRegistry: WidgetRegistry,
    onExitStandBy: () -> Unit,
    modifier: Modifier = Modifier
) {
    val chargingState by standByController.chargingState.collectAsState()
    val dashboardUiState by dashboardViewModel.uiState.collectAsState()

    Surface(
        modifier = modifier.fillMaxSize(),
        color = Color(0xFF07090E) // Deep AMOLED StandBy background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Sleek Top Status Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "SKIRK",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp,
                        fontFamily = FontFamily.SansSerif,
                        color = Color(0xFFE2E8F0)
                    )

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (chargingState.isCharging) Color(0xFF064E3B) else Color(0xFF422006)
                    ) {
                        Text(
                            text = if (chargingState.isCharging) "⚡ ${chargingState.source.displayName}" else "⚡ Manual Mode",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (chargingState.isCharging) Color(0xFF4ADE80) else Color(0xFFFACC15),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }

                    Text(
                        text = "• Awake",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )
                }

                FilledTonalButton(
                    onClick = onExitStandBy,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = Color(0xFF1E293B),
                        contentColor = Color(0xFFE2E8F0)
                    )
                ) {
                    Text("Exit StandBy", fontSize = 11.sp)
                }
            }

            // Live Model-Driven Dashboard Engine View
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                DashboardView(
                    dashboard = dashboardUiState.dashboard,
                    widgetRegistry = widgetRegistry,
                    onNextPanel = { dashboardViewModel.onNextPanel() },
                    onPreviousPanel = { dashboardViewModel.onPreviousPanel() },
                    onSelectPanel = { dashboardViewModel.onSelectPanel(it) },
                    onNextWidgetInSlot = { dashboardViewModel.onNextWidgetInSlot(it) },
                    onPreviousWidgetInSlot = { dashboardViewModel.onPreviousWidgetInSlot(it) },
                    onResetToDefault = { dashboardViewModel.onResetToDefault() },
                    showPanelControls = false,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
