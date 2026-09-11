package com.kunvarpreet.skirk.widget

import com.kunvarpreet.skirk.domain.model.WidgetConfig
import com.kunvarpreet.skirk.widget.schedule.ScheduleWidgetConfig
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ScheduleWidgetConfigTest {

    @Test
    fun fromConfig_withDefaults_returnsDefaultValues() {
        val widgetConfig = WidgetConfig()
        val parsed = ScheduleWidgetConfig.fromConfig(widgetConfig)

        assertEquals("timeline", parsed.selectedDesign)
        assertEquals(8, parsed.maxEvents)
        assertFalse(parsed.showPastEvents)
        assertTrue(parsed.showAllDayEvents)
        assertTrue(parsed.showLocation)
    }

    @Test
    fun fromConfig_withCustomProperties_parsesCorrectly() {
        val widgetConfig = WidgetConfig(
            settings = mapOf(
                ScheduleWidgetConfig.KEY_SELECTED_DESIGN to "large_event",
                ScheduleWidgetConfig.KEY_MAX_EVENTS to "15",
                ScheduleWidgetConfig.KEY_SHOW_PAST_EVENTS to "true",
                ScheduleWidgetConfig.KEY_SHOW_ALL_DAY_EVENTS to "false",
                ScheduleWidgetConfig.KEY_SHOW_LOCATION to "false"
            )
        )
        val parsed = ScheduleWidgetConfig.fromConfig(widgetConfig)

        assertEquals("large_event", parsed.selectedDesign)
        assertEquals(15, parsed.maxEvents)
        assertTrue(parsed.showPastEvents)
        assertFalse(parsed.showAllDayEvents)
        assertFalse(parsed.showLocation)
    }

    @Test
    fun toWidgetConfig_serializesPropertiesCorrectly() {
        val config = ScheduleWidgetConfig(
            selectedDesign = "compact_agenda",
            maxEvents = 12,
            showPastEvents = false,
            showAllDayEvents = true,
            showLocation = false
        )
        val widgetConfig = config.toWidgetConfig()

        assertEquals("compact_agenda", widgetConfig.settings[ScheduleWidgetConfig.KEY_SELECTED_DESIGN])
        assertEquals("12", widgetConfig.settings[ScheduleWidgetConfig.KEY_MAX_EVENTS])
        assertEquals("false", widgetConfig.settings[ScheduleWidgetConfig.KEY_SHOW_PAST_EVENTS])
        assertEquals("true", widgetConfig.settings[ScheduleWidgetConfig.KEY_SHOW_ALL_DAY_EVENTS])
        assertEquals("false", widgetConfig.settings[ScheduleWidgetConfig.KEY_SHOW_LOCATION])
    }
}
