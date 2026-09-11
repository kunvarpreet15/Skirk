package com.kunvarpreet.skirk.widget.calendar

import com.kunvarpreet.skirk.calendar.domain.CalendarRepository
import com.kunvarpreet.skirk.calendar.domain.MockCalendarRepository
import com.kunvarpreet.skirk.domain.model.WidgetCategory
import com.kunvarpreet.skirk.domain.model.WidgetDefinition
import com.kunvarpreet.skirk.domain.model.WidgetDesign
import com.kunvarpreet.skirk.widget.core.WidgetContentRenderer
import com.kunvarpreet.skirk.widget.core.WidgetProvider
import com.kunvarpreet.skirk.widget.model.WidgetTypeIds

/**
 * Production provider for the built-in Calendar widget.
 */
class CalendarWidgetProvider(
    private val calendarRepository: CalendarRepository = MockCalendarRepository()
) : WidgetProvider {

    private val renderer = CalendarWidgetRenderer(calendarRepository)

    override val definition: WidgetDefinition = WidgetDefinition(
        id = WidgetTypeIds.CALENDAR,
        displayName = "Calendar",
        description = "Month glance and upcoming schedule markers",
        category = WidgetCategory.PRODUCTIVITY,
        availableDesigns = listOf(
            WidgetDesign(id = "classic", displayName = "Month Grid"),
            WidgetDesign(id = "minimal", displayName = "Minimal Grid"),
            WidgetDesign(id = "agenda_calendar", displayName = "Agenda Calendar")
        ),
        defaultDesignId = "classic"
    )

    override fun getRenderer(designId: String): WidgetContentRenderer = renderer
}
