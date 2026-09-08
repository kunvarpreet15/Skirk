package com.kunvarpreet.skirk.presentation.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kunvarpreet.skirk.domain.model.WidgetSlot
import com.kunvarpreet.skirk.widget.core.WidgetRegistry

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    widgetRegistry: WidgetRegistry,
    onNavigateToSettings: () -> Unit,
    onNavigateToEditor: () -> Unit,
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
            val dashboard = uiState.dashboard
            val activePanel = dashboard?.activePanel

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    // Minimal Placeholder Header as requested in Phase 0 specification
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(20.dp)
                            )
                            .padding(vertical = 32.dp, horizontal = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "SKIRK",
                                style = MaterialTheme.typography.displaySmall.copy(
                                    letterSpacing = 6.sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.SansSerif
                                ),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Dashboard Placeholder",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Phase 0 Architecture Verification",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                    }
                }

                if (dashboard != null && activePanel != null) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Active Panel: ${activePanel.name}",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Panel ${dashboard.activePanelIndex + 1} of ${dashboard.panels.size}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Layout: ${activePanel.layout.displayName} (${activePanel.slots.size} slots)",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = { viewModel.onPreviousPanel() },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("‹ Prev Panel")
                                    }
                                    OutlinedButton(
                                        onClick = { viewModel.onNextPanel() },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Next Panel ›")
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Text(
                            text = "Configured Widget Slots",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Start
                        )
                    }

                    items(activePanel.slots) { slot ->
                        SlotCard(
                            slot = slot,
                            widgetRegistry = widgetRegistry,
                            onNextWidget = { viewModel.onNextWidgetInSlot(slot.slotIndex) },
                            onPrevWidget = { viewModel.onPreviousWidgetInSlot(slot.slotIndex) }
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = onNavigateToEditor,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Dashboard Editor")
                        }
                        OutlinedButton(
                            onClick = onNavigateToSettings,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Settings")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SlotCard(
    slot: WidgetSlot,
    widgetRegistry: WidgetRegistry,
    onNextWidget: () -> Unit,
    onPrevWidget: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Slot #${slot.slotIndex + 1}",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Stack: ${slot.widgets.size} widget(s)",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            val activeWidget = slot.activeWidget
            if (activeWidget != null) {
                val definition = widgetRegistry.getDefinition(activeWidget.widgetTypeId)
                val design = definition?.findDesign(activeWidget.selectedDesignId)
                val renderer = widgetRegistry.getRenderer(activeWidget.widgetTypeId, activeWidget.selectedDesignId)

                // Render via decoupled WidgetContentRenderer
                renderer.Render(
                    instance = activeWidget,
                    design = design ?: com.kunvarpreet.skirk.domain.model.WidgetDesign(
                        id = activeWidget.selectedDesignId,
                        displayName = activeWidget.selectedDesignId
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                if (slot.widgets.size > 1) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Stacked ${slot.activeWidgetIndex + 1}/${slot.widgets.size}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            OutlinedButton(onClick = onPrevWidget) {
                                Text("▲ Prev", fontSize = 11.sp)
                            }
                            OutlinedButton(onClick = onNextWidget) {
                                Text("▼ Next", fontSize = 11.sp)
                            }
                        }
                    }
                }
            } else {
                Text(
                    text = "Empty slot",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
            }
        }
    }
}
