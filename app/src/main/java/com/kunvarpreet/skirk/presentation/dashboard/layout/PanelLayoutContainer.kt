package com.kunvarpreet.skirk.presentation.dashboard.layout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.kunvarpreet.skirk.domain.model.Panel
import com.kunvarpreet.skirk.domain.model.PanelLayout
import com.kunvarpreet.skirk.domain.model.WidgetSlot
import com.kunvarpreet.skirk.widget.core.WidgetRegistry

/**
 * Renders the layout geometry of a Panel according to its [PanelLayout].
 * Supports Single, Two-Split Horizontal (columns), Two-Split Vertical (rows),
 * Grid 4 (2x2), and Grid 6 (3x2).
 */
@Composable
fun PanelLayoutContainer(
    panel: Panel,
    widgetRegistry: WidgetRegistry,
    onNextWidgetInSlot: (slotIndex: Int) -> Unit,
    onPrevWidgetInSlot: (slotIndex: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val slots = panel.slots

    if (slots.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No slots configured for ${panel.name}",
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFF64748B)
            )
        }
        return
    }

    // Helper to get slot safely or empty placeholder slot
    fun slotAt(index: Int): WidgetSlot {
        return slots.find { it.slotIndex == index }
            ?: WidgetSlot(id = "missing_$index", slotIndex = index, widgets = emptyList())
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        when (panel.layout) {
            is PanelLayout.Single -> {
                WidgetSlotView(
                    slot = slotAt(0),
                    widgetRegistry = widgetRegistry,
                    onNextWidget = { onNextWidgetInSlot(0) },
                    onPrevWidget = { onPrevWidgetInSlot(0) },
                    modifier = Modifier.fillMaxSize()
                )
            }

            is PanelLayout.TwoSplitHorizontal -> {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    WidgetSlotView(
                        slot = slotAt(0),
                        widgetRegistry = widgetRegistry,
                        onNextWidget = { onNextWidgetInSlot(0) },
                        onPrevWidget = { onPrevWidgetInSlot(0) },
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    )
                    WidgetSlotView(
                        slot = slotAt(1),
                        widgetRegistry = widgetRegistry,
                        onNextWidget = { onNextWidgetInSlot(1) },
                        onPrevWidget = { onPrevWidgetInSlot(1) },
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    )
                }
            }

            is PanelLayout.TwoSplitVertical -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    WidgetSlotView(
                        slot = slotAt(0),
                        widgetRegistry = widgetRegistry,
                        onNextWidget = { onNextWidgetInSlot(0) },
                        onPrevWidget = { onPrevWidgetInSlot(0) },
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    )
                    WidgetSlotView(
                        slot = slotAt(1),
                        widgetRegistry = widgetRegistry,
                        onNextWidget = { onNextWidgetInSlot(1) },
                        onPrevWidget = { onPrevWidgetInSlot(1) },
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    )
                }
            }

            is PanelLayout.Grid4 -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        WidgetSlotView(
                            slot = slotAt(0),
                            widgetRegistry = widgetRegistry,
                            onNextWidget = { onNextWidgetInSlot(0) },
                            onPrevWidget = { onPrevWidgetInSlot(0) },
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        )
                        WidgetSlotView(
                            slot = slotAt(1),
                            widgetRegistry = widgetRegistry,
                            onNextWidget = { onNextWidgetInSlot(1) },
                            onPrevWidget = { onPrevWidgetInSlot(1) },
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        )
                    }

                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        WidgetSlotView(
                            slot = slotAt(2),
                            widgetRegistry = widgetRegistry,
                            onNextWidget = { onNextWidgetInSlot(2) },
                            onPrevWidget = { onPrevWidgetInSlot(2) },
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        )
                        WidgetSlotView(
                            slot = slotAt(3),
                            widgetRegistry = widgetRegistry,
                            onNextWidget = { onNextWidgetInSlot(3) },
                            onPrevWidget = { onPrevWidgetInSlot(3) },
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        )
                    }
                }
            }

            is PanelLayout.Grid6, is PanelLayout.Custom -> {
                // 3 columns x 2 rows
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        WidgetSlotView(
                            slot = slotAt(0),
                            widgetRegistry = widgetRegistry,
                            onNextWidget = { onNextWidgetInSlot(0) },
                            onPrevWidget = { onPrevWidgetInSlot(0) },
                            modifier = Modifier.weight(1f).fillMaxHeight()
                        )
                        WidgetSlotView(
                            slot = slotAt(1),
                            widgetRegistry = widgetRegistry,
                            onNextWidget = { onNextWidgetInSlot(1) },
                            onPrevWidget = { onPrevWidgetInSlot(1) },
                            modifier = Modifier.weight(1f).fillMaxHeight()
                        )
                        WidgetSlotView(
                            slot = slotAt(2),
                            widgetRegistry = widgetRegistry,
                            onNextWidget = { onNextWidgetInSlot(2) },
                            onPrevWidget = { onPrevWidgetInSlot(2) },
                            modifier = Modifier.weight(1f).fillMaxHeight()
                        )
                    }

                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        WidgetSlotView(
                            slot = slotAt(3),
                            widgetRegistry = widgetRegistry,
                            onNextWidget = { onNextWidgetInSlot(3) },
                            onPrevWidget = { onPrevWidgetInSlot(3) },
                            modifier = Modifier.weight(1f).fillMaxHeight()
                        )
                        WidgetSlotView(
                            slot = slotAt(4),
                            widgetRegistry = widgetRegistry,
                            onNextWidget = { onNextWidgetInSlot(4) },
                            onPrevWidget = { onPrevWidgetInSlot(4) },
                            modifier = Modifier.weight(1f).fillMaxHeight()
                        )
                        WidgetSlotView(
                            slot = slotAt(5),
                            widgetRegistry = widgetRegistry,
                            onNextWidget = { onNextWidgetInSlot(5) },
                            onPrevWidget = { onPrevWidgetInSlot(5) },
                            modifier = Modifier.weight(1f).fillMaxHeight()
                        )
                    }
                }
            }
        }
    }
}
