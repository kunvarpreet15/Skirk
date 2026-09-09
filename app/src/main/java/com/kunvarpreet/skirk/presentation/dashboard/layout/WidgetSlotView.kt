package com.kunvarpreet.skirk.presentation.dashboard.layout

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kunvarpreet.skirk.domain.model.WidgetDesign
import com.kunvarpreet.skirk.domain.model.WidgetSlot
import com.kunvarpreet.skirk.gesture.GestureConfig
import com.kunvarpreet.skirk.gesture.slotVerticalSwipe
import com.kunvarpreet.skirk.presentation.dashboard.animation.WidgetTransitionDirection
import com.kunvarpreet.skirk.presentation.dashboard.animation.widgetSlotTransition
import com.kunvarpreet.skirk.widget.core.WidgetRegistry

/**
 * Composable rendering an individual WidgetSlot.
 *
 * Features:
 * - Natural vertical swipe gesture handling via [slotVerticalSwipe].
 * - Directional vertical slide and fade animations via [AnimatedContent] and [widgetSlotTransition].
 * - Accessibility semantic actions for screen readers.
 * - Tap passthrough for interactive widget elements.
 * - Clean StandBy slot framing without clutter.
 */
@Composable
fun WidgetSlotView(
    slot: WidgetSlot,
    widgetRegistry: WidgetRegistry,
    onNextWidget: () -> Unit,
    onPrevWidget: () -> Unit,
    modifier: Modifier = Modifier,
    gestureConfig: GestureConfig = GestureConfig.Default,
    showDebugControls: Boolean = false
) {
    val activeWidget = slot.activeWidget
    val canSwipe = slot.widgets.size > 1

    var transitionDirection by remember { mutableStateOf(WidgetTransitionDirection.NONE) }

    val handleSwipeUp: () -> Unit = {
        if (canSwipe) {
            transitionDirection = WidgetTransitionDirection.FORWARD
            onNextWidget()
        }
    }

    val handleSwipeDown: () -> Unit = {
        if (canSwipe) {
            transitionDirection = WidgetTransitionDirection.BACKWARD
            onPrevWidget()
        }
    }

    val activeDefinition = activeWidget?.let { widgetRegistry.getDefinition(it.widgetTypeId) }
    val widgetDisplayName = activeDefinition?.displayName ?: activeWidget?.widgetTypeId ?: "Empty"

    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(20.dp))
            .slotVerticalSwipe(
                enabled = canSwipe,
                config = gestureConfig,
                onSwipeUp = handleSwipeUp,
                onSwipeDown = handleSwipeDown
            )
            .semantics {
                contentDescription = "Slot ${slot.slotIndex + 1}, showing $widgetDisplayName, item ${slot.activeWidgetIndex + 1} of ${slot.widgets.size.coerceAtLeast(1)}"
                if (canSwipe) {
                    customActions = listOf(
                        CustomAccessibilityAction("Next Widget") {
                            handleSwipeUp()
                            true
                        },
                        CustomAccessibilityAction("Previous Widget") {
                            handleSwipeDown()
                            true
                        }
                    )
                }
            },
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
                        text = "Slot #${slot.slotIndex + 1}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF475569)
                    )
                }
            }
        } else {
            // Active Widget Content with Directional Animated Vertical Transition
            AnimatedContent(
                targetState = activeWidget,
                transitionSpec = {
                    widgetSlotTransition(transitionDirection)
                },
                label = "WidgetSlotContentAnimation"
            ) { targetWidget ->
                val definition = widgetRegistry.getDefinition(targetWidget.widgetTypeId)
                val design = definition?.findDesign(targetWidget.selectedDesignId)
                    ?: WidgetDesign(id = targetWidget.selectedDesignId, displayName = targetWidget.selectedDesignId)
                val renderer = widgetRegistry.getRenderer(targetWidget.widgetTypeId, targetWidget.selectedDesignId)

                renderer.Render(
                    instance = targetWidget,
                    design = design,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Optional developer controls for multi-widget stack cycling (disabled by default in StandBy)
            if (showDebugControls && canSwipe) {
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
                            onClick = handleSwipeDown,
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
                            onClick = handleSwipeUp,
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
