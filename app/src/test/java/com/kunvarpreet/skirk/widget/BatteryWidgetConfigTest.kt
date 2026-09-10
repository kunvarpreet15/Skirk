package com.kunvarpreet.skirk.widget

import com.kunvarpreet.skirk.domain.model.WidgetConfig
import com.kunvarpreet.skirk.widget.battery.BatteryWidgetConfig
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BatteryWidgetConfigTest {

    @Test
    fun from_withDefaults_returnsDefaultValues() {
        val widgetConfig = WidgetConfig()
        val parsed = BatteryWidgetConfig.from(widgetConfig)

        assertTrue(parsed.showTemperature)
        assertTrue(parsed.showHealth)
        assertTrue(parsed.showPowerSource)
    }

    @Test
    fun from_withCustomProperties_parsesCorrectly() {
        val widgetConfig = WidgetConfig(
            settings = mapOf(
                BatteryWidgetConfig.KEY_SHOW_TEMPERATURE to "false",
                BatteryWidgetConfig.KEY_SHOW_HEALTH to "false",
                BatteryWidgetConfig.KEY_SHOW_POWER_SOURCE to "false"
            )
        )
        val parsed = BatteryWidgetConfig.from(widgetConfig)

        assertFalse(parsed.showTemperature)
        assertFalse(parsed.showHealth)
        assertFalse(parsed.showPowerSource)
    }

    @Test
    fun toWidgetConfig_serializesPropertiesCorrectly() {
        val config = BatteryWidgetConfig(
            showTemperature = false,
            showHealth = true,
            showPowerSource = false
        )
        val widgetConfig = config.toWidgetConfig()

        assertEquals("false", widgetConfig.settings[BatteryWidgetConfig.KEY_SHOW_TEMPERATURE])
        assertEquals("true", widgetConfig.settings[BatteryWidgetConfig.KEY_SHOW_HEALTH])
        assertEquals("false", widgetConfig.settings[BatteryWidgetConfig.KEY_SHOW_POWER_SOURCE])
    }
}
