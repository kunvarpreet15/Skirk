package com.kunvarpreet.skirk.widget

import com.kunvarpreet.skirk.domain.model.WidgetConfig
import com.kunvarpreet.skirk.widget.analogclock.AnalogClockConfig
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AnalogClockConfigTest {

    @Test
    fun fromConfig_withDefaults_returnsDefaultValues() {
        val widgetConfig = WidgetConfig()
        val parsed = AnalogClockConfig.fromConfig(widgetConfig)

        assertTrue(parsed.showSeconds)
        assertTrue(parsed.showHourNumbers)
    }

    @Test
    fun fromConfig_withCustomProperties_parsesCorrectly() {
        val widgetConfig = WidgetConfig(
            settings = mapOf(
                AnalogClockConfig.KEY_SHOW_SECONDS to "false",
                AnalogClockConfig.KEY_SHOW_HOUR_NUMBERS to "false"
            )
        )
        val parsed = AnalogClockConfig.fromConfig(widgetConfig)

        assertFalse(parsed.showSeconds)
        assertFalse(parsed.showHourNumbers)
    }

    @Test
    fun toWidgetConfig_serializesPropertiesCorrectly() {
        val clockConfig = AnalogClockConfig(
            showSeconds = false,
            showHourNumbers = false
        )
        val widgetConfig = clockConfig.toWidgetConfig()

        assertEquals("false", widgetConfig.settings[AnalogClockConfig.KEY_SHOW_SECONDS])
        assertEquals("false", widgetConfig.settings[AnalogClockConfig.KEY_SHOW_HOUR_NUMBERS])
    }
}
