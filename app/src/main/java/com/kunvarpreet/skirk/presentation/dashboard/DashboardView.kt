package com.kunvarpreet.skirk.presentation.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kunvarpreet.skirk.domain.model.Dashboard
import com.kunvarpreet.skirk.presentation.dashboard.layout.PanelLayoutContainer
import com.kunvarpreet.skirk.widget.core.WidgetRegistry

/**
 * Reusable model-driven dashboard rendering engine.
 * Renders the active panel with its layout geometry, widget slots, and navigation controls.
 * Used identically in normal application mode and inside the StandBy runtime.
 */
@Composable
fun DashboardView(
    dashboard: Dashboard?,
    widgetRegistry: WidgetRegistry,
    onNextPanel: () -> Unit,
    onPreviousPanel: () -> Unit,
    onSelectPanel: (index: Int) -> Unit,
    onNextWidgetInSlot: (slotIndex: Int) -> Unit,
    onPreviousWidgetInSlot: (slotIndex: Int) -> Unit,
    onResetToDefault: () -> Unit,
    showPanelControls: Boolean = true,
    modifier: Modifier = Modifier
) {
    if (dashboard == null || dashboard.panels.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "No Panels Configured",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF94A3B8)
                )
                Text(
                    text = "The dashboard configuration is currently empty.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF64748B)
                )
                Button(onClick = onResetToDefault) {
                    Text("Restore Default Dashboard")
                }
            }
        }
        return
    }

    val activePanel = dashboard.activePanel ?: dashboard.panels[0]

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Main Panel Layout Area
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            PanelLayoutContainer(
                panel = activePanel,
                widgetRegistry = widgetRegistry,
                onNextWidgetInSlot = onNextWidgetInSlot,
                onPrevWidgetInSlot = onPreviousWidgetInSlot,
                modifier = Modifier.fillMaxSize()
            )
        }

        // Temporary Developer Panel Navigation Bar
        if (showPanelControls && dashboard.panels.size > 1) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                color = Color.Black.copy(alpha = 0.5f),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    FilledTonalButton(
                        onClick = onPreviousPanel,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("‹ Prev Panel", fontSize = 12.sp)
                    }

                    // Panel Title & Indicators
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = activePanel.name,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFF1F5F9)
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            dashboard.panels.forEachIndexed { index, _ ->
                                val isSelected = index == dashboard.activePanelIndex
                                Box(
                                    modifier = Modifier
                                        .size(if (isSelected) 8.dp else 6.dp)
                                        .clip(CircleShape)
                                        .background(if (isSelected) Color(0xFF38BDF8) else Color.White.copy(alpha = 0.3f))
                                        .clickable { onSelectPanel(index) }
                                )
                            }
                        }
                    }

                    FilledTonalButton(
                        onClick = onNextPanel,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Next Panel ›", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
