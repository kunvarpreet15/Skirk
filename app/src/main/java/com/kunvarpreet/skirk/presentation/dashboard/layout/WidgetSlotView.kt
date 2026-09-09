package com.kunvarpreet.skirk.presentation.dashboard.layout

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kunvarpreet.skirk.domain.model.WidgetDesign
import com.kunvarpreet.skirk.domain.model.WidgetSlot
import com.kunvarpreet.skirk.widget.core.WidgetRegistry

/**
 * Composable rendering an individual WidgetSlot.
 * Displays the currently active widget in the slot's stack, along with
 * temporary developer controls to cycle widgets forward and backward.
 */
@Composable
fun WidgetSlotView(
    slot: WidgetSlot,
    widgetRegistry: WidgetRegistry,
    onNextWidget: () -> Unit,
    onPrevWidget: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activeWidget = slot.activeWidget

    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(20.dp)),
        contentAlignment = Alignment.Center
    ) {
        if (activeWidget == null) {
            // Empty Slot State
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .border(
                        width = 1.5.dp,
                        color = Color.White.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .background(Color.White.copy(alpha = 0.03f))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Empty Slot",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF64748B)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Slot #${slot.slotIndex + 1} (${slot.id})",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF475569)
                    )
                }
            }
        } else {
            // Active Widget Content
            val definition = widgetRegistry.getDefinition(activeWidget.widgetTypeId)
            val design = definition?.findDesign(activeWidget.selectedDesignId)
                ?: WidgetDesign(id = activeWidget.selectedDesignId, displayName = activeWidget.selectedDesignId)
            val renderer = widgetRegistry.getRenderer(activeWidget.widgetTypeId, activeWidget.selectedDesignId)

            renderer.Render(
                instance = activeWidget,
                design = design,
                modifier = Modifier.fillMaxSize()
            )

            // Temporary developer controls for multi-widget stack cycling
            if (slot.widgets.size > 1) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = Color.Black.copy(alpha = 0.75f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "${slot.activeWidgetIndex + 1}/${slot.widgets.size}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE2E8F0)
                        )

                        FilledTonalButton(
                            onClick = onPrevWidget,
                            modifier = Modifier.size(24.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
                            shape = RoundedCornerShape(6.dp),
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = Color.White.copy(alpha = 0.15f),
                                contentColor = Color.White
                            )
                        ) {
                            Text("▲", fontSize = 10.sp)
                        }

                        FilledTonalButton(
                            onClick = onNextWidget,
                            modifier = Modifier.size(24.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
                            shape = RoundedCornerShape(6.dp),
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = Color.White.copy(alpha = 0.15f),
                                contentColor = Color.White
                            )
                        ) {
                            Text("▼", fontSize = 10.sp)
                        }
                    }
                }
            }
        }
    }
}
