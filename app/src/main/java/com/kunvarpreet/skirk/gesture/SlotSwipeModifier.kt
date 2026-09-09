package com.kunvarpreet.skirk.gesture

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.LocalDensity

/**
 * Modifier that attaches vertical swipe detection to a widget slot.
 *
 * Characteristics:
 * - Operates strictly along [Orientation.Vertical], allowing horizontal drags to pass
 *   cleanly to parent containers like HorizontalPager.
 * - Does not intercept short taps, allowing interactive widgets (e.g., buttons, media toggles)
 *   to receive click events naturally.
 * - Uses [GestureConfig] for displacement and velocity thresholds to avoid accidental activations.
 * - Swipe UP triggers [onSwipeUp] (advances to next widget).
 * - Swipe DOWN triggers [onSwipeDown] (returns to previous widget).
 */
fun Modifier.slotVerticalSwipe(
    enabled: Boolean = true,
    config: GestureConfig = GestureConfig.Default,
    onSwipeUp: () -> Unit,
    onSwipeDown: () -> Unit
): Modifier = composed {
    if (!enabled) return@composed this

    val density = LocalDensity.current
    var accumulatedDeltaY by remember { mutableFloatStateOf(0f) }

    draggable(
        orientation = Orientation.Vertical,
        enabled = enabled,
        state = rememberDraggableState { delta ->
            accumulatedDeltaY += delta
        },
        onDragStarted = {
            accumulatedDeltaY = 0f
        },
        onDragStopped = { velocity ->
            val thresholdPx = with(density) { config.swipeDisplacementThresholdDp.toPx() }
            val velocityThreshold = config.swipeVelocityThresholdPx

            when {
                accumulatedDeltaY <= -thresholdPx || velocity <= -velocityThreshold -> {
                    // Swiped UP -> Advance to Next Widget
                    onSwipeUp()
                }
                accumulatedDeltaY >= thresholdPx || velocity >= velocityThreshold -> {
                    // Swiped DOWN -> Go to Previous Widget
                    onSwipeDown()
                }
            }
            accumulatedDeltaY = 0f
        }
    )
}
