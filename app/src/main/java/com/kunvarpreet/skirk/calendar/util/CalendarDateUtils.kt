package com.kunvarpreet.skirk.calendar.util

import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

/**
 * Grid cell representing a date in a month view calendar.
 */
data class CalendarGridCell(
    val date: LocalDate,
    val isCurrentMonth: Boolean
)

/**
 * Reusable date/time utilities for Calendar and Schedule widgets.
 */
object CalendarDateUtils {

    /**
     * Computes the 35 or 42 grid cells for displaying a full monthly calendar,
     * including leading days from the previous month and trailing days from the next month.
     */
    fun getDaysInMonthGrid(
        yearMonth: YearMonth,
        firstDayOfWeek: DayOfWeek = DayOfWeek.MONDAY
    ): List<CalendarGridCell> {
        val cells = mutableListOf<CalendarGridCell>()

        val firstOfMonth = yearMonth.atDay(1)
        val daysInMonth = yearMonth.lengthOfMonth()

        // Calculate leading days from previous month
        val leadingDaysCount = (firstOfMonth.dayOfWeek.value - firstDayOfWeek.value + 7) % 7
        val prevMonth = yearMonth.minusMonths(1)
        val prevMonthLength = prevMonth.lengthOfMonth()

        for (i in (prevMonthLength - leadingDaysCount + 1)..prevMonthLength) {
            cells.add(CalendarGridCell(date = prevMonth.atDay(i), isCurrentMonth = false))
        }

        // Add current month days
        for (day in 1..daysInMonth) {
            cells.add(CalendarGridCell(date = yearMonth.atDay(day), isCurrentMonth = true))
        }

        // Calculate trailing days from next month to complete the 7-day grid rows
        val totalSoFar = cells.size
        val remainder = totalSoFar % 7
        val trailingDaysCount = if (remainder == 0) 0 else 7 - remainder
        val nextMonth = yearMonth.plusMonths(1)

        for (day in 1..trailingDaysCount) {
            cells.add(CalendarGridCell(date = nextMonth.atDay(day), isCurrentMonth = false))
        }

        return cells
    }

    /**
     * Formats start and end times for an event based on duration and 12/24h preference.
     */
    fun formatEventTime(
        startMillis: Long,
        endMillis: Long,
        isAllDay: Boolean,
        is24Hour: Boolean = true,
        zoneId: ZoneId = ZoneId.systemDefault()
    ): String {
        if (isAllDay) return "All day"

        val pattern = if (is24Hour) "HH:mm" else "h:mm a"
        val formatter = DateTimeFormatter.ofPattern(pattern, Locale.getDefault())

        val startZdt = Instant.ofEpochMilli(startMillis).atZone(zoneId)
        val startTimeStr = startZdt.format(formatter)

        if (endMillis > startMillis) {
            val endZdt = Instant.ofEpochMilli(endMillis).atZone(zoneId)
            val endTimeStr = endZdt.format(formatter)
            return "$startTimeStr - $endTimeStr"
        }

        return startTimeStr
    }

    /**
     * Formats date header for schedule grouping: "Today", "Tomorrow", or "Wednesday, Sep 16".
     */
    fun formatDateHeader(
        date: LocalDate,
        today: LocalDate = LocalDate.now(),
        locale: Locale = Locale.getDefault()
    ): String {
        return when {
            date == today -> "Today"
            date == today.plusDays(1) -> "Tomorrow"
            date == today.minusDays(1) -> "Yesterday"
            else -> {
                val formatter = DateTimeFormatter.ofPattern("EEEE, MMM d", locale)
                date.format(formatter)
            }
        }
    }

    /**
     * Formats the month header, e.g. "SEPTEMBER 2026".
     */
    fun formatMonthHeader(
        yearMonth: YearMonth,
        locale: Locale = Locale.getDefault()
    ): String {
        val monthStr = yearMonth.month.getDisplayName(TextStyle.FULL, locale).uppercase(locale)
        return "$monthStr ${yearMonth.year}"
    }

    /**
     * Returns the 7 day-of-week abbreviation labels starting from [firstDayOfWeek].
     */
    fun getWeekdayLabels(
        firstDayOfWeek: DayOfWeek = DayOfWeek.MONDAY,
        locale: Locale = Locale.getDefault()
    ): List<String> {
        return (0..6).map { offset ->
            val dow = DayOfWeek.of(((firstDayOfWeek.value - 1 + offset) % 7) + 1)
            // Use 1-letter or narrow abbreviation, e.g. "M", "T", "W"
            dow.getDisplayName(TextStyle.NARROW, locale).uppercase(locale)
        }
    }
}
