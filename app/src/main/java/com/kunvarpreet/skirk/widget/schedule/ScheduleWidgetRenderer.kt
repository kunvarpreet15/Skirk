package com.kunvarpreet.skirk.widget.schedule

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.kunvarpreet.skirk.calendar.domain.CalendarRepository
import com.kunvarpreet.skirk.calendar.model.CalendarAccessState
import com.kunvarpreet.skirk.calendar.model.ScheduleData
import com.kunvarpreet.skirk.calendar.ui.CalendarEmptyView
import com.kunvarpreet.skirk.domain.model.WidgetDesign
import com.kunvarpreet.skirk.domain.model.WidgetInstance
import com.kunvarpreet.skirk.widget.core.WidgetContentRenderer

/**
 * Production renderer for the Schedule & Agenda widget.
 */
class ScheduleWidgetRenderer(
    private val calendarRepository: CalendarRepository
) : WidgetContentRenderer {

    @Composable
    override fun Render(
        instance: WidgetInstance,
        design: WidgetDesign,
        modifier: Modifier
    ) {
        val context = LocalContext.current
        val config = ScheduleWidgetConfig.from(instance.config)
        val scheduleData by calendarRepository.observeSchedule()
            .collectAsState(initial = ScheduleData(accessState = CalendarAccessState.GRANTED))

        if (scheduleData.accessState != CalendarAccessState.GRANTED) {
            CalendarEmptyView(
                state = scheduleData.accessState,
                onGrantPermission = {
                    calendarRepository.openCalendarSettings(context)
                },
                modifier = modifier
            )
            return
        }

        when (design.id) {
            "agenda_list", "compact_agenda" -> {
                CompactAgendaScheduleDesign(
                    data = scheduleData,
                    config = config,
                    modifier = modifier
                )
            }
            "large_event" -> {
                LargeEventScheduleDesign(
                    data = scheduleData,
                    config = config,
                    modifier = modifier
                )
            }
            else -> {
                TimelineScheduleDesign(
                    data = scheduleData,
                    config = config,
                    modifier = modifier
                )
            }
        }
    }
}
