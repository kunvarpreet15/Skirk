package com.kunvarpreet.skirk.gesture

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.abs

/**
 * Centralized gesture thresholds and constants for the Skirk dashboard interaction engine.
 * Eliminates magic values and provides a single point of calibration for swipe responsiveness.
 */
data class GestureConfig(
    /**
     * Minimum displacement (in dp) required for a drag movement to be registered as an intentional swipe.
     */
    val swipeDisplacementThresholdDp: Dp = 40.dp,

    /**
     * Minimum velocity (in pixels/sec) to register a quick flick/fling gesture even if displacement is small.
     */
    val swipeVelocityThresholdPx: Float = 500f,

    /**
     * Ratio of primary-axis displacement to cross-axis displacement required to lock gesture direction.
     * E.g., for a vertical swipe, |deltaY| must exceed |deltaX| * directionDominanceRatio.
     */
    val directionDominanceRatio: Float = 1.25f
) {
    /**
     * Evaluates whether a movement vector qualifies as a dominant vertical swipe.
     */
    fun isDominantVertical(deltaX: Float, deltaY: Float): Boolean {
        val absX = abs(deltaX)
        val absY = abs(deltaY)
        return absY > 0f && absY >= absX * directionDominanceRatio
    }

    /**
     * Evaluates whether a movement vector qualifies as a dominant horizontal swipe.
     */
    fun isDominantHorizontal(deltaX: Float, deltaY: Float): Boolean {
        val absX = abs(deltaX)
        val absY = abs(deltaY)
        return absX > 0f && absX >= absY * directionDominanceRatio
    }

    companion object {
        val Default = GestureConfig()
    }
}
