package com.kunvarpreet.skirk.presentation.dashboard

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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.kunvarpreet.skirk.widget.core.WidgetRegistry

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    widgetRegistry: WidgetRegistry,
    onNavigateToSettings: () -> Unit,
    onNavigateToEditor: () -> Unit,
    onLaunchStandBy: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Top Header Row with Title and Action Buttons
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "SKIRK",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    letterSpacing = 4.sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.SansSerif
                                ),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Dashboard Engine (Phase 2)",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = onNavigateToEditor,
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text("Editor", fontSize = 12.sp)
                            }
                            OutlinedButton(
                                onClick = onNavigateToSettings,
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text("Settings", fontSize = 12.sp)
                            }
                            Button(
                                onClick = onLaunchStandBy,
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                ),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text("StandBy", fontSize = 12.sp)
                            }
                        }
                    }
                }

                // Live Model-Driven Dashboard Area
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    DashboardView(
                        dashboard = uiState.dashboard,
                        widgetRegistry = widgetRegistry,
                        onNextPanel = { viewModel.onNextPanel() },
                        onPreviousPanel = { viewModel.onPreviousPanel() },
                        onSelectPanel = { viewModel.onSelectPanel(it) },
                        onNextWidgetInSlot = { viewModel.onNextWidgetInSlot(it) },
                        onPreviousWidgetInSlot = { viewModel.onPreviousWidgetInSlot(it) },
                        onResetToDefault = { viewModel.onResetToDefault() },
                        showPanelControls = true,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}
