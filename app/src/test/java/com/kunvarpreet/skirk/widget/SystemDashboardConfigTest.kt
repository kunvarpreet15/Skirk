package com.kunvarpreet.skirk.widget

import com.kunvarpreet.skirk.widget.system.SystemDashboardConfig
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SystemDashboardConfigTest {

    @Test
    fun defaults_areCorrect() {
        val config = SystemDashboardConfig()
        assertEquals(SystemDashboardConfig.DESIGN_GRID, config.selectedDesign)
        assertTrue(config.showRam)
        assertTrue(config.showStorage)
        assertTrue(config.showBattery)
        assertTrue(config.showNetwork)
    }

    @Test
    fun serialization_roundTripPreservesValues() {
        val original = SystemDashboardConfig(
            selectedDesign = SystemDashboardConfig.DESIGN_RINGS,
            showRam = true,
            showStorage = false,
            showBattery = true,
            showNetwork = false
        )

        val widgetConfig = original.toWidgetConfig()
        val parsed = SystemDashboardConfig.fromWidgetConfig(widgetConfig)

        assertEquals(original.selectedDesign, parsed.selectedDesign)
        assertEquals(original.showRam, parsed.showRam)
        assertEquals(original.showStorage, parsed.showStorage)
        assertEquals(original.showBattery, parsed.showBattery)
        assertEquals(original.showNetwork, parsed.showNetwork)
    }

    @Test
    fun fromWidgetConfig_nullYieldsDefaults() {
        val config = SystemDashboardConfig.fromWidgetConfig(null)
        assertEquals(SystemDashboardConfig.DESIGN_GRID, config.selectedDesign)
        assertTrue(config.showRam)
        assertTrue(config.showStorage)
        assertTrue(config.showBattery)
        assertTrue(config.showNetwork)
    }
}
