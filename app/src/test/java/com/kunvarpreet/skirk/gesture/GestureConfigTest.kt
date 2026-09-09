package com.kunvarpreet.skirk.gesture

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GestureConfigTest {

    private val config = GestureConfig.Default

    @Test
    fun isDominantVertical_pureVertical_returnsTrue() {
        assertTrue(config.isDominantVertical(deltaX = 0f, deltaY = 100f))
        assertTrue(config.isDominantVertical(deltaX = 0f, deltaY = -100f))
    }

    @Test
    fun isDominantVertical_diagonalWithDominantVertical_returnsTrue() {
        // dy = 100, dx = 50 -> dy > dx * 1.25 (100 > 62.5) -> true
        assertTrue(config.isDominantVertical(deltaX = 50f, deltaY = 100f))
        assertTrue(config.isDominantVertical(deltaX = -50f, deltaY = -100f))
    }

    @Test
    fun isDominantVertical_diagonalEqualOrHorizontalDominant_returnsFalse() {
        // dy = 100, dx = 90 -> 100 is not >= 90 * 1.25 (112.5) -> false
        assertFalse(config.isDominantVertical(deltaX = 90f, deltaY = 100f))

        // Pure horizontal -> false
        assertFalse(config.isDominantVertical(deltaX = 100f, deltaY = 0f))

        // Zero displacement -> false
        assertFalse(config.isDominantVertical(deltaX = 0f, deltaY = 0f))
    }

    @Test
    fun isDominantHorizontal_pureHorizontal_returnsTrue() {
        assertTrue(config.isDominantHorizontal(deltaX = 100f, deltaY = 0f))
        assertTrue(config.isDominantHorizontal(deltaX = -100f, deltaY = 0f))
    }

    @Test
    fun isDominantHorizontal_diagonalWithDominantHorizontal_returnsTrue() {
        // dx = 100, dy = 50 -> dx > dy * 1.25 (100 > 62.5) -> true
        assertTrue(config.isDominantHorizontal(deltaX = 100f, deltaY = 50f))
        assertTrue(config.isDominantHorizontal(deltaX = -100f, deltaY = -50f))
    }

    @Test
    fun isDominantHorizontal_diagonalEqualOrVerticalDominant_returnsFalse() {
        // dx = 100, dy = 90 -> 100 is not >= 90 * 1.25 (112.5) -> false
        assertFalse(config.isDominantHorizontal(deltaX = 100f, deltaY = 90f))

        // Pure vertical -> false
        assertFalse(config.isDominantHorizontal(deltaX = 0f, deltaY = 100f))

        // Zero displacement -> false
        assertFalse(config.isDominantHorizontal(deltaX = 0f, deltaY = 0f))
    }
}
