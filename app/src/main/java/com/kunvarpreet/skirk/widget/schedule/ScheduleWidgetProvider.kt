package com.kunvarpreet.skirk.widget.schedule

import com.kunvarpreet.skirk.calendar.domain.CalendarRepository
import com.kunvarpreet.skirk.calendar.domain.MockCalendarRepository
import com.kunvarpreet.skirk.domain.model.WidgetCategory
import com.kunvarpreet.skirk.domain.model.WidgetDefinition
import com.kunvarpreet.skirk.domain.model.WidgetDesign
import com.kunvarpreet.skirk.widget.core.WidgetContentRenderer
import com.kunvarpreet.skirk.widget.core.WidgetProvider
import com.kunvarpreet.skirk.widget.model.WidgetTypeIds

/**
 * Production provider for the built-in Schedule widget.
 */
class ScheduleWidgetProvider(
    private val calendarRepository: CalendarRepository = MockCalendarRepository()
) : WidgetProvider {

    private val renderer = ScheduleWidgetRenderer(calendarRepository)

    override val definition: WidgetDefinition = WidgetDefinition(
        id = WidgetTypeIds.SCHEDULE,
        displayName = "Schedule & Agenda",
        description = "Upcoming calendar events and timeline",
        category = WidgetCategory.PRODUCTIVITY,
        availableDesigns = listOf(
            WidgetDesign(id = "timeline", displayName = "Timeline"),
            WidgetDesign(id = "agenda_list", displayName = "Agenda List"),
            WidgetDesign(id = "large_event", displayName = "Large Event")
        ),
        defaultDesignId = "timeline"
    )

    override fun getRenderer(designId: String): WidgetContentRenderer = renderer
}
