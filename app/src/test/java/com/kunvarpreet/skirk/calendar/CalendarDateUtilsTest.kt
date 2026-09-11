package com.kunvarpreet.skirk.calendar

import com.kunvarpreet.skirk.calendar.util.CalendarDateUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.util.Locale

class CalendarDateUtilsTest {

    @Test
    fun getDaysInMonthGrid_september2026_generates35Cells() {
        val ym = YearMonth.of(2026, 9)
        val cells = CalendarDateUtils.getDaysInMonthGrid(ym, DayOfWeek.MONDAY)

        assertEquals(35, cells.size)

        // First cell should be Aug 31, 2026 (previous month)
        assertEquals(LocalDate.of(2026, 8, 31), cells.first().date)
        assertFalse(cells.first().isCurrentMonth)

        // September has 30 days
        val currentMonthCells = cells.filter { it.isCurrentMonth }
        assertEquals(30, currentMonthCells.size)
        assertEquals(LocalDate.of(2026, 9, 1), currentMonthCells.first().date)
        assertEquals(LocalDate.of(2026, 9, 30), currentMonthCells.last().date)

        // Last cell should be Oct 4, 2026
        assertEquals(LocalDate.of(2026, 10, 4), cells.last().date)
        assertFalse(cells.last().isCurrentMonth)
    }

    @Test
    fun getDaysInMonthGrid_cellsAreStrictlyConsecutive() {
        val ym = YearMonth.of(2026, 2) // Non-leap year Feb
        val cells = CalendarDateUtils.getDaysInMonthGrid(ym, DayOfWeek.SUNDAY)

        assertTrue(cells.size % 7 == 0)
        for (i in 0 until cells.size - 1) {
            assertEquals(cells[i].date.plusDays(1), cells[i + 1].date)
        }
    }

    @Test
    fun formatEventTime_allDay_returnsAllDay() {
        val result = CalendarDateUtils.formatEventTime(
            startMillis = 1000L,
            endMillis = 5000L,
            isAllDay = true
        )
        assertEquals("All day", result)
    }

    @Test
    fun formatEventTime_timedEvent_formatsCorrectly() {
        val zoneId = ZoneId.of("UTC")
        val date = LocalDate.of(2026, 9, 11)
        val startMillis = date.atTime(10, 0).atZone(zoneId).toInstant().toEpochMilli()
        val endMillis = date.atTime(11, 30).atZone(zoneId).toInstant().toEpochMilli()

        val result24 = CalendarDateUtils.formatEventTime(
            startMillis = startMillis,
            endMillis = endMillis,
            isAllDay = false,
            is24Hour = true,
            zoneId = zoneId
        )
        assertEquals("10:00 - 11:30", result24)
    }

    @Test
    fun formatDateHeader_relativeDates() {
        val today = LocalDate.of(2026, 9, 11)
        val tomorrow = today.plusDays(1)
        val other = today.plusDays(5)

        assertEquals("Today", CalendarDateUtils.formatDateHeader(today, today))
        assertEquals("Tomorrow", CalendarDateUtils.formatDateHeader(tomorrow, today))
        assertTrue(CalendarDateUtils.formatDateHeader(other, today, Locale.US).contains("Sep 16"))
    }

    @Test
    fun formatMonthHeader_returnsUppercaseMonthAndYear() {
        val ym = YearMonth.of(2026, 9)
        assertEquals("SEPTEMBER 2026", CalendarDateUtils.formatMonthHeader(ym, Locale.US))
    }

    @Test
    fun getWeekdayLabels_returnsSevenLabels() {
        val labels = CalendarDateUtils.getWeekdayLabels(DayOfWeek.MONDAY, Locale.US)
        assertEquals(7, labels.size)
        assertEquals("M", labels[0])
    }
}
