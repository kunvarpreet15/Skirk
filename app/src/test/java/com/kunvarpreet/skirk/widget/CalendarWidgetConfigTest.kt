package com.kunvarpreet.skirk.widget

import com.kunvarpreet.skirk.domain.model.WidgetConfig
import com.kunvarpreet.skirk.widget.calendar.CalendarWidgetConfig
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CalendarWidgetConfigTest {

    @Test
    fun fromConfig_withDefaults_returnsDefaultValues() {
        val widgetConfig = WidgetConfig()
        val parsed = CalendarWidgetConfig.fromConfig(widgetConfig)

        assertEquals("classic", parsed.selectedDesign)
        assertTrue(parsed.showEventIndicators)
        assertTrue(parsed.showAdjacentMonthDays)
        assertTrue(parsed.highlightCurrentDay)
    }

    @Test
    fun fromConfig_withCustomProperties_parsesCorrectly() {
        val widgetConfig = WidgetConfig(
            settings = mapOf(
                CalendarWidgetConfig.KEY_SELECTED_DESIGN to "minimal",
                CalendarWidgetConfig.KEY_SHOW_EVENT_INDICATORS to "false",
                CalendarWidgetConfig.KEY_SHOW_ADJACENT_DAYS to "false",
                CalendarWidgetConfig.KEY_HIGHLIGHT_CURRENT_DAY to "false"
            )
        )
        val parsed = CalendarWidgetConfig.fromConfig(widgetConfig)

        assertEquals("minimal", parsed.selectedDesign)
        assertFalse(parsed.showEventIndicators)
        assertFalse(parsed.showAdjacentMonthDays)
        assertFalse(parsed.highlightCurrentDay)
    }

    @Test
    fun toWidgetConfig_serializesPropertiesCorrectly() {
        val config = CalendarWidgetConfig(
            selectedDesign = "agenda_calendar",
            showEventIndicators = false,
            showAdjacentMonthDays = true,
            highlightCurrentDay = false
        )
        val widgetConfig = config.toWidgetConfig()

        assertEquals("agenda_calendar", widgetConfig.settings[CalendarWidgetConfig.KEY_SELECTED_DESIGN])
        assertEquals("false", widgetConfig.settings[CalendarWidgetConfig.KEY_SHOW_EVENT_INDICATORS])
        assertEquals("true", widgetConfig.settings[CalendarWidgetConfig.KEY_SHOW_ADJACENT_DAYS])
        assertEquals("false", widgetConfig.settings[CalendarWidgetConfig.KEY_HIGHLIGHT_CURRENT_DAY])
    }
}
