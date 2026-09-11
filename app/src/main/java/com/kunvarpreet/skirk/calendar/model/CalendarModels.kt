package com.kunvarpreet.skirk.calendar.model

import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId

/**
 * Clean domain representation of an Android calendar account/container.
 */
data class CalendarItem(
    val id: Long,
    val displayName: String,
    val accountName: String? = null,
    val color: Int = 0xFF3B82F6.toInt(),
    val isVisible: Boolean = true
)

/**
 * Domain representation of a calendar event or recurring event instance occurrence.
 */
data class CalendarEvent(
    val id: Long,
    val calendarId: Long,
    val title: String,
    val startEpochMillis: Long,
    val endEpochMillis: Long,
    val isAllDay: Boolean = false,
    val location: String? = null,
    val description: String? = null,
    val color: Int? = null
) : Comparable<CalendarEvent> {

    override fun compareTo(other: CalendarEvent): Int {
        // All day events on the same day come first
        if (this.isAllDay != other.isAllDay) {
            return if (this.isAllDay) -1 else 1
        }
        val startComparison = this.startEpochMillis.compareTo(other.startEpochMillis)
        if (startComparison != 0) return startComparison

        val endComparison = this.endEpochMillis.compareTo(other.endEpochMillis)
        if (endComparison != 0) return endComparison

        return this.title.compareTo(other.title)
    }

    companion object {
        fun createSample(
            id: Long,
            title: String,
            startHour: Int,
            startMinute: Int,
            durationMinutes: Int,
            date: LocalDate = LocalDate.now(),
            isAllDay: Boolean = false,
            location: String? = null,
            color: Int = 0xFF3B82F6.toInt(),
            zoneId: ZoneId = ZoneId.systemDefault()
        ): CalendarEvent {
            val startZdt = date.atTime(startHour, startMinute).atZone(zoneId)
            val startMillis = startZdt.toInstant().toEpochMilli()
            val endMillis = startMillis + (durationMinutes * 60 * 1000L)

            return CalendarEvent(
                id = id,
                calendarId = 1L,
                title = title,
                startEpochMillis = startMillis,
                endEpochMillis = endMillis,
                isAllDay = isAllDay,
                location = location,
                color = color
            )
        }
    }
}

/**
 * Access state for the calendar subsystem.
 */
enum class CalendarAccessState {
    GRANTED,
    DENIED,
    NO_CALENDARS,
    ERROR
}

/**
 * Day cell representation for the monthly calendar grid.
 */
data class CalendarDayEvents(
    val date: LocalDate,
    val events: List<CalendarEvent> = emptyList(),
    val isToday: Boolean = false,
    val isCurrentMonth: Boolean = true
) {
    val hasEvents: Boolean get() = events.isNotEmpty()
}

/**
 * State object representing an entire monthly calendar view with event markings.
 */
data class MonthCalendarData(
    val accessState: CalendarAccessState = CalendarAccessState.GRANTED,
    val yearMonth: YearMonth = YearMonth.now(),
    val days: List<CalendarDayEvents> = emptyList(),
    val selectedDate: LocalDate = LocalDate.now(),
    val selectedDayEvents: List<CalendarEvent> = emptyList()
) {
    companion object {
        val Sample: MonthCalendarData by lazy {
            val now = LocalDate.now()
            val ym = YearMonth.from(now)
            val sampleEvents = listOf(
                CalendarEvent.createSample(1, "StandBy Review", 10, 0, 60, now),
                CalendarEvent.createSample(2, "Team Sync", 14, 30, 45, now, location = "Room 204"),
                CalendarEvent.createSample(3, "All-day Focus", 0, 0, 1440, now.plusDays(1), isAllDay = true)
            )

            val daysList = (1..ym.lengthOfMonth()).map { dayNum ->
                val d = ym.atDay(dayNum)
                val dayEvts = if (d == now) sampleEvents.take(2) else if (d == now.plusDays(1)) sampleEvents.takeLast(1) else emptyList()
                CalendarDayEvents(
                    date = d,
                    events = dayEvts,
                    isToday = d == now,
                    isCurrentMonth = true
                )
            }

            MonthCalendarData(
                accessState = CalendarAccessState.GRANTED,
                yearMonth = ym,
                days = daysList,
                selectedDate = now,
                selectedDayEvents = sampleEvents.take(2)
            )
        }
    }
}

/**
 * State object for the Schedule/Agenda widget.
 */
data class ScheduleData(
    val accessState: CalendarAccessState = CalendarAccessState.GRANTED,
    val events: List<CalendarEvent> = emptyList(),
    val todayEvents: List<CalendarEvent> = emptyList(),
    val upcomingEvents: List<CalendarEvent> = emptyList()
) {
    val isEmpty: Boolean get() = events.isEmpty()

    companion object {
        val Sample: ScheduleData by lazy {
            val now = LocalDate.now()
            val events = listOf(
                CalendarEvent.createSample(1, "Android StandBy Core", 10, 0, 60, now, location = "Room 204", color = 0xFF3B82F6.toInt()),
                CalendarEvent.createSample(2, "Widget Architecture Sync", 14, 30, 60, now, location = "Online", color = 0xFF10B981.toInt()),
                CalendarEvent.createSample(3, "Evening Gym", 18, 0, 90, now, location = "Fitness Center", color = 0xFFF59E0B.toInt()),
                CalendarEvent.createSample(4, "Product Roadmap 2026", 9, 30, 90, now.plusDays(1), location = "Main Hall", color = 0xFF8B5CF6.toInt()),
                CalendarEvent.createSample(5, "Design Review", 15, 0, 45, now.plusDays(2), location = "Studio A", color = 0xFFEC4899.toInt())
            )

            ScheduleData(
                accessState = CalendarAccessState.GRANTED,
                events = events,
                todayEvents = events.take(3),
                upcomingEvents = events.drop(3)
            )
        }
    }
}
