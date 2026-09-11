package com.kunvarpreet.skirk.calendar

import com.kunvarpreet.skirk.calendar.model.CalendarAccessState
import com.kunvarpreet.skirk.calendar.model.CalendarDayEvents
import com.kunvarpreet.skirk.calendar.model.CalendarEvent
import com.kunvarpreet.skirk.calendar.model.MonthCalendarData
import com.kunvarpreet.skirk.calendar.model.ScheduleData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class CalendarModelsTest {

    @Test
    fun calendarEvent_sortsAllDayFirstThenChronological() {
        val today = LocalDate.of(2026, 9, 11)
        val eventTimedEarly = CalendarEvent.createSample(1, "Morning Sync", 9, 0, 30, today)
        val eventTimedLate = CalendarEvent.createSample(2, "Evening Gym", 18, 0, 60, today)
        val eventAllDay = CalendarEvent.createSample(3, "Holiday", 0, 0, 1440, today, isAllDay = true)

        val list = listOf(eventTimedLate, eventTimedEarly, eventAllDay)
        val sorted = list.sorted()

        assertEquals("Holiday", sorted[0].title)
        assertEquals("Morning Sync", sorted[1].title)
        assertEquals("Evening Gym", sorted[2].title)
    }

    @Test
    fun calendarDayEvents_hasEvents_reflectsEventsList() {
        val date = LocalDate.of(2026, 9, 11)
        val emptyDay = CalendarDayEvents(date = date, events = emptyList())
        assertFalse(emptyDay.hasEvents)

        val withEvent = CalendarDayEvents(
            date = date,
            events = listOf(CalendarEvent.createSample(1, "Test", 10, 0, 30, date))
        )
        assertTrue(withEvent.hasEvents)
    }

    @Test
    fun monthCalendarData_sample_isValid() {
        val sample = MonthCalendarData.Sample
        assertEquals(CalendarAccessState.GRANTED, sample.accessState)
        assertTrue(sample.days.isNotEmpty())
        assertTrue(sample.selectedDayEvents.isNotEmpty())
    }

    @Test
    fun scheduleData_sample_isValid() {
        val sample = ScheduleData.Sample
        assertEquals(CalendarAccessState.GRANTED, sample.accessState)
        assertFalse(sample.isEmpty)
        assertTrue(sample.todayEvents.isNotEmpty())
        assertTrue(sample.upcomingEvents.isNotEmpty())
    }
}
