package com.kunvarpreet.skirk.presentation.dashboard.animation

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith

/**
 * Direction of widget transition within a slot stack.
 */
enum class WidgetTransitionDirection {
    /**
     * Swiped UP: Next widget slides in from bottom, current slides out to top.
     */
    FORWARD,

    /**
     * Swiped DOWN: Previous widget slides in from top, current slides out to bottom.
     */
    BACKWARD,

    /**
     * Neutral / Initial state: Crossfade without directional displacement.
     */
    NONE
}

/**
 * Generates an animated ContentTransform for widget transitions inside a slot.
 * Ensures smooth, non-intrusive StandBy-appropriate animations on mid-range devices.
 */
fun widgetSlotTransition(
    direction: WidgetTransitionDirection,
    durationMillis: Int = 280
): ContentTransform {
    val animationSpec = tween<Float>(durationMillis = durationMillis, easing = FastOutSlowInEasing)
    val intAnimationSpec = tween<androidx.compose.ui.unit.IntOffset>(durationMillis = durationMillis, easing = FastOutSlowInEasing)

    return when (direction) {
        WidgetTransitionDirection.FORWARD -> {
            // Next widget enters from bottom, current exits to top
            (slideInVertically(animationSpec = intAnimationSpec) { height -> height } +
                    fadeIn(animationSpec = animationSpec)) togetherWith
                    (slideOutVertically(animationSpec = intAnimationSpec) { height -> -height } +
                            fadeOut(animationSpec = animationSpec))
        }
        WidgetTransitionDirection.BACKWARD -> {
            // Previous widget enters from top, current exits to bottom
            (slideInVertically(animationSpec = intAnimationSpec) { height -> -height } +
                    fadeIn(animationSpec = animationSpec)) togetherWith
                    (slideOutVertically(animationSpec = intAnimationSpec) { height -> height } +
                            fadeOut(animationSpec = animationSpec))
        }
        WidgetTransitionDirection.NONE -> {
            fadeIn(animationSpec = animationSpec) togetherWith fadeOut(animationSpec = animationSpec)
        }
    }
}
