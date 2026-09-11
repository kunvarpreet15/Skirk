package com.kunvarpreet.skirk.calendar.domain

import android.content.Context
import com.kunvarpreet.skirk.calendar.model.CalendarItem
import com.kunvarpreet.skirk.calendar.model.MonthCalendarData
import com.kunvarpreet.skirk.calendar.model.ScheduleData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.YearMonth
import java.time.ZoneId

/**
 * Domain repository contract abstracting access to calendar and schedule data.
 */
interface CalendarRepository {
    fun hasCalendarPermission(): Boolean
    fun observeMonthCalendar(yearMonth: YearMonth = YearMonth.now(), zoneId: ZoneId = ZoneId.systemDefault()): Flow<MonthCalendarData>
    fun observeSchedule(daysAhead: Int = 14, zoneId: ZoneId = ZoneId.systemDefault()): Flow<ScheduleData>
    fun getCalendars(): List<CalendarItem>
    fun openCalendarSettings(context: Context)
}

/**
 * Mock implementation of [CalendarRepository] for previews and headless unit tests.
 */
class MockCalendarRepository(
    initialMonthData: MonthCalendarData = MonthCalendarData.Sample,
    initialScheduleData: ScheduleData = ScheduleData.Sample
) : CalendarRepository {

    private val monthFlow = MutableStateFlow(initialMonthData)
    private val scheduleFlow = MutableStateFlow(initialScheduleData)

    override fun hasCalendarPermission(): Boolean = true

    override fun observeMonthCalendar(yearMonth: YearMonth, zoneId: ZoneId): Flow<MonthCalendarData> =
        monthFlow.asStateFlow()

    override fun observeSchedule(daysAhead: Int, zoneId: ZoneId): Flow<ScheduleData> =
        scheduleFlow.asStateFlow()

    override fun getCalendars(): List<CalendarItem> = listOf(
        CalendarItem(id = 1L, displayName = "Personal", color = 0xFF3B82F6.toInt()),
        CalendarItem(id = 2L, displayName = "Work", color = 0xFF10B981.toInt())
    )

    override fun openCalendarSettings(context: Context) {}
}
