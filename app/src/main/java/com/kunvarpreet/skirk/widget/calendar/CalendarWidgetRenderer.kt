package com.kunvarpreet.skirk.widget.calendar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.kunvarpreet.skirk.calendar.domain.CalendarRepository
import com.kunvarpreet.skirk.calendar.model.CalendarAccessState
import com.kunvarpreet.skirk.calendar.model.MonthCalendarData
import com.kunvarpreet.skirk.calendar.ui.CalendarEmptyView
import com.kunvarpreet.skirk.domain.model.WidgetDesign
import com.kunvarpreet.skirk.domain.model.WidgetInstance
import com.kunvarpreet.skirk.widget.core.WidgetContentRenderer

/**
 * Production renderer for the Calendar widget.
 */
class CalendarWidgetRenderer(
    private val calendarRepository: CalendarRepository
) : WidgetContentRenderer {

    @Composable
    override fun Render(
        instance: WidgetInstance,
        design: WidgetDesign,
        modifier: Modifier
    ) {
        val context = LocalContext.current
        val config = CalendarWidgetConfig.from(instance.config)
        val monthData by calendarRepository.observeMonthCalendar()
            .collectAsState(initial = MonthCalendarData(accessState = CalendarAccessState.GRANTED))

        if (monthData.accessState != CalendarAccessState.GRANTED) {
            CalendarEmptyView(
                state = monthData.accessState,
                onGrantPermission = {
                    calendarRepository.openCalendarSettings(context)
                },
                modifier = modifier
            )
            return
        }

        when (design.id) {
            "minimal" -> {
                MinimalCalendarDesign(
                    data = monthData,
                    config = config,
                    modifier = modifier
                )
            }
            "agenda_calendar", "day_large" -> {
                AgendaCalendarDesign(
                    data = monthData,
                    config = config,
                    modifier = modifier
                )
            }
            else -> {
                ClassicCalendarDesign(
                    data = monthData,
                    config = config,
                    modifier = modifier
                )
            }
        }
    }
}
