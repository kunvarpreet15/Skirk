package com.kunvarpreet.skirk.widget

import com.kunvarpreet.skirk.domain.model.WidgetConfig
import com.kunvarpreet.skirk.widget.digitalclock.DigitalClockConfig
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DigitalClockConfigTest {

    @Test
    fun fromConfig_withDefaults_returnsDefaultValues() {
        val widgetConfig = WidgetConfig()
        val parsed = DigitalClockConfig.fromConfig(widgetConfig)

        assertTrue(parsed.is24Hour)
        assertFalse(parsed.showSeconds)
        assertTrue(parsed.showDate)
    }

    @Test
    fun fromConfig_withCustomProperties_parsesCorrectly() {
        val widgetConfig = WidgetConfig(
            settings = mapOf(
                DigitalClockConfig.KEY_IS_24_HOUR to "false",
                DigitalClockConfig.KEY_SHOW_SECONDS to "true",
                DigitalClockConfig.KEY_SHOW_DATE to "false"
            )
        )
        val parsed = DigitalClockConfig.fromConfig(widgetConfig)

        assertFalse(parsed.is24Hour)
        assertTrue(parsed.showSeconds)
        assertFalse(parsed.showDate)
    }

    @Test
    fun toWidgetConfig_serializesPropertiesCorrectly() {
        val clockConfig = DigitalClockConfig(
            is24Hour = true,
            showSeconds = true,
            showDate = false
        )
        val widgetConfig = clockConfig.toWidgetConfig()

        assertEquals("true", widgetConfig.settings[DigitalClockConfig.KEY_IS_24_HOUR])
        assertEquals("true", widgetConfig.settings[DigitalClockConfig.KEY_SHOW_SECONDS])
        assertEquals("false", widgetConfig.settings[DigitalClockConfig.KEY_SHOW_DATE])
    }
}
