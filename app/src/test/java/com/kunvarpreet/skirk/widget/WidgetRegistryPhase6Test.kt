package com.kunvarpreet.skirk.widget

import com.kunvarpreet.skirk.calendar.domain.MockCalendarRepository
import com.kunvarpreet.skirk.domain.model.WidgetCategory
import com.kunvarpreet.skirk.widget.calendar.CalendarWidgetProvider
import com.kunvarpreet.skirk.widget.calendar.CalendarWidgetRenderer
import com.kunvarpreet.skirk.widget.core.WidgetRegistry
import com.kunvarpreet.skirk.widget.definitions.BuiltInWidgetDefinitions
import com.kunvarpreet.skirk.widget.model.WidgetTypeIds
import com.kunvarpreet.skirk.widget.schedule.ScheduleWidgetProvider
import com.kunvarpreet.skirk.widget.schedule.ScheduleWidgetRenderer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class WidgetRegistryPhase6Test {

    private lateinit var registry: WidgetRegistry

    @Before
    fun setup() {
        registry = WidgetRegistry()
        BuiltInWidgetDefinitions.allBuiltInProviders.forEach { registry.register(it) }
    }

    @Test
    fun calendar_registeredWithProductionProviderAndDesigns() {
        val def = registry.getDefinition(WidgetTypeIds.CALENDAR)
        assertNotNull(def)
        assertEquals("Calendar", def?.displayName)
        assertEquals(WidgetCategory.PRODUCTIVITY, def?.category)

        val designIds = def!!.availableDesigns.map { it.id }
        assertTrue(designIds.contains("classic"))
        assertTrue(designIds.contains("minimal"))
        assertTrue(designIds.contains("agenda_calendar"))

        val renderer = registry.getRenderer(WidgetTypeIds.CALENDAR, "classic")
        assertTrue(renderer is CalendarWidgetRenderer)
    }

    @Test
    fun schedule_registeredWithProductionProviderAndDesigns() {
        val def = registry.getDefinition(WidgetTypeIds.SCHEDULE)
        assertNotNull(def)
        assertEquals("Schedule & Agenda", def?.displayName)
        assertEquals(WidgetCategory.PRODUCTIVITY, def?.category)

        val designIds = def!!.availableDesigns.map { it.id }
        assertTrue(designIds.contains("timeline"))
        assertTrue(designIds.contains("agenda_list"))
        assertTrue(designIds.contains("large_event"))

        val renderer = registry.getRenderer(WidgetTypeIds.SCHEDULE, "timeline")
        assertTrue(renderer is ScheduleWidgetRenderer)
    }

    @Test
    fun createBuiltInProviders_withCustomCalendarRepository_registersCorrectly() {
        val customRepo = MockCalendarRepository()
        val customRegistry = WidgetRegistry()

        BuiltInWidgetDefinitions.createBuiltInProviders(
            calendarRepository = customRepo
        ).forEach {
            customRegistry.register(it)
        }

        val calRenderer = customRegistry.getRenderer(WidgetTypeIds.CALENDAR, "minimal")
        assertTrue(calRenderer is CalendarWidgetRenderer)

        val schedRenderer = customRegistry.getRenderer(WidgetTypeIds.SCHEDULE, "large_event")
        assertTrue(schedRenderer is ScheduleWidgetRenderer)
    }
}
