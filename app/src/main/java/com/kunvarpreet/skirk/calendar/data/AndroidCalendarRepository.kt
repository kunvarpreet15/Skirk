package com.kunvarpreet.skirk.calendar.data

import android.Manifest
import android.content.BroadcastReceiver
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.CalendarContract
import android.provider.Settings
import androidx.core.content.ContextCompat
import com.kunvarpreet.skirk.calendar.domain.CalendarRepository
import com.kunvarpreet.skirk.calendar.model.CalendarAccessState
import com.kunvarpreet.skirk.calendar.model.CalendarDayEvents
import com.kunvarpreet.skirk.calendar.model.CalendarEvent
import com.kunvarpreet.skirk.calendar.model.CalendarItem
import com.kunvarpreet.skirk.calendar.model.MonthCalendarData
import com.kunvarpreet.skirk.calendar.model.ScheduleData
import com.kunvarpreet.skirk.calendar.util.CalendarDateUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId

/**
 * Android implementation of [CalendarRepository] integrating with Android's
 * official Calendar Provider via [CalendarContract.Instances].
 */
class AndroidCalendarRepository(
    private val context: Context
) : CalendarRepository {

    override fun hasCalendarPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_CALENDAR
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun openCalendarSettings(context: Context) {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", context.packageName, null)
        ).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            context.startActivity(intent)
        } catch (e: Exception) {}
    }

    override fun getCalendars(): List<CalendarItem> {
        if (!hasCalendarPermission()) return emptyList()

        val calendars = mutableListOf<CalendarItem>()
        val projection = arrayOf(
            CalendarContract.Calendars._ID,
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
            CalendarContract.Calendars.ACCOUNT_NAME,
            CalendarContract.Calendars.CALENDAR_COLOR,
            CalendarContract.Calendars.VISIBLE
        )

        try {
            context.contentResolver.query(
                CalendarContract.Calendars.CONTENT_URI,
                projection,
                null,
                null,
                "${CalendarContract.Calendars.CALENDAR_DISPLAY_NAME} ASC"
            )?.use { cursor ->
                val idIdx = cursor.getColumnIndexOrThrow(CalendarContract.Calendars._ID)
                val nameIdx = cursor.getColumnIndexOrThrow(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME)
                val accIdx = cursor.getColumnIndexOrThrow(CalendarContract.Calendars.ACCOUNT_NAME)
                val colorIdx = cursor.getColumnIndexOrThrow(CalendarContract.Calendars.CALENDAR_COLOR)
                val visIdx = cursor.getColumnIndexOrThrow(CalendarContract.Calendars.VISIBLE)

                while (cursor.moveToNext()) {
                    calendars.add(
                        CalendarItem(
                            id = cursor.getLong(idIdx),
                            displayName = cursor.getString(nameIdx) ?: "Calendar",
                            accountName = cursor.getString(accIdx),
                            color = cursor.getInt(colorIdx),
                            isVisible = cursor.getInt(visIdx) != 0
                        )
                    )
                }
            }
        } catch (e: Exception) {
            // Handle security exception or provider errors gracefully
        }

        return calendars
    }

    override fun observeMonthCalendar(
        yearMonth: YearMonth,
        zoneId: ZoneId
    ): Flow<MonthCalendarData> = callbackFlow {
        fun load() {
            if (!hasCalendarPermission()) {
                trySend(MonthCalendarData(accessState = CalendarAccessState.DENIED, yearMonth = yearMonth))
                return
            }

            val today = LocalDate.now(zoneId)
            val gridCells = CalendarDateUtils.getDaysInMonthGrid(yearMonth)
            if (gridCells.isEmpty()) {
                trySend(MonthCalendarData(accessState = CalendarAccessState.GRANTED, yearMonth = yearMonth))
                return
            }

            val firstDate = gridCells.first().date
            val lastDate = gridCells.last().date

            val startMillis = firstDate.atStartOfDay(zoneId).toInstant().toEpochMilli()
            val endMillis = lastDate.plusDays(1).atStartOfDay(zoneId).toInstant().toEpochMilli()

            val rawEvents = queryEventInstances(startMillis, endMillis)
            val sortedEvents = rawEvents.sorted()

            // Group events by local date
            val eventsByDate = sortedEvents.groupBy { event ->
                Instant.ofEpochMilli(event.startEpochMillis).atZone(zoneId).toLocalDate()
            }

            val dayEventsList = gridCells.map { cell ->
                CalendarDayEvents(
                    date = cell.date,
                    events = eventsByDate[cell.date] ?: emptyList(),
                    isToday = cell.date == today,
                    isCurrentMonth = cell.isCurrentMonth
                )
            }

            val selectedDate = if (yearMonth == YearMonth.from(today)) today else yearMonth.atDay(1)
            val selectedDayEvents = eventsByDate[selectedDate] ?: emptyList()

            trySend(
                MonthCalendarData(
                    accessState = CalendarAccessState.GRANTED,
                    yearMonth = yearMonth,
                    days = dayEventsList,
                    selectedDate = selectedDate,
                    selectedDayEvents = selectedDayEvents
                )
            )
        }

        load()

        val contentObserver = object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) {
                load()
            }
        }

        val broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(c: Context?, intent: Intent?) {
                load()
            }
        }

        try {
            context.contentResolver.registerContentObserver(
                CalendarContract.Events.CONTENT_URI,
                true,
                contentObserver
            )
        } catch (e: Exception) {}

        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_DATE_CHANGED)
            addAction(Intent.ACTION_TIMEZONE_CHANGED)
            addAction(Intent.ACTION_TIME_CHANGED)
        }
        context.registerReceiver(broadcastReceiver, filter)

        awaitClose {
            try {
                context.contentResolver.unregisterContentObserver(contentObserver)
            } catch (e: Exception) {}
            try {
                context.unregisterReceiver(broadcastReceiver)
            } catch (e: Exception) {}
        }
    }.distinctUntilChanged().flowOn(Dispatchers.IO)

    override fun observeSchedule(
        daysAhead: Int,
        zoneId: ZoneId
    ): Flow<ScheduleData> = callbackFlow {
        fun load() {
            if (!hasCalendarPermission()) {
                trySend(ScheduleData(accessState = CalendarAccessState.DENIED))
                return
            }

            val today = LocalDate.now(zoneId)
            val startMillis = today.atStartOfDay(zoneId).toInstant().toEpochMilli()
            val endMillis = today.plusDays(daysAhead.toLong() + 1).atStartOfDay(zoneId).toInstant().toEpochMilli()

            val rawEvents = queryEventInstances(startMillis, endMillis)
            val sortedEvents = rawEvents.sorted()

            val todayEvents = sortedEvents.filter { event ->
                val eventDate = Instant.ofEpochMilli(event.startEpochMillis).atZone(zoneId).toLocalDate()
                eventDate == today
            }

            val upcomingEvents = sortedEvents.filter { event ->
                val eventDate = Instant.ofEpochMilli(event.startEpochMillis).atZone(zoneId).toLocalDate()
                eventDate > today
            }

            val state = if (sortedEvents.isEmpty()) {
                CalendarAccessState.NO_CALENDARS
            } else {
                CalendarAccessState.GRANTED
            }

            trySend(
                ScheduleData(
                    accessState = state,
                    events = sortedEvents,
                    todayEvents = todayEvents,
                    upcomingEvents = upcomingEvents
                )
            )
        }

        load()

        val contentObserver = object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) {
                load()
            }
        }

        val broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(c: Context?, intent: Intent?) {
                load()
            }
        }

        try {
            context.contentResolver.registerContentObserver(
                CalendarContract.Events.CONTENT_URI,
                true,
                contentObserver
            )
        } catch (e: Exception) {}

        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_DATE_CHANGED)
            addAction(Intent.ACTION_TIMEZONE_CHANGED)
            addAction(Intent.ACTION_TIME_CHANGED)
        }
        context.registerReceiver(broadcastReceiver, filter)

        awaitClose {
            try {
                context.contentResolver.unregisterContentObserver(contentObserver)
            } catch (e: Exception) {}
            try {
                context.unregisterReceiver(broadcastReceiver)
            } catch (e: Exception) {}
        }
    }.distinctUntilChanged().flowOn(Dispatchers.IO)

    internal fun queryEventInstances(startMillis: Long, endMillis: Long): List<CalendarEvent> {
        val events = mutableListOf<CalendarEvent>()
        val uriBuilder = CalendarContract.Instances.CONTENT_URI.buildUpon()
        ContentUris.appendId(uriBuilder, startMillis)
        ContentUris.appendId(uriBuilder, endMillis)

        val projection = arrayOf(
            CalendarContract.Instances._ID,
            CalendarContract.Instances.EVENT_ID,
            CalendarContract.Instances.CALENDAR_ID,
            CalendarContract.Instances.TITLE,
            CalendarContract.Instances.BEGIN,
            CalendarContract.Instances.END,
            CalendarContract.Instances.ALL_DAY,
            CalendarContract.Instances.EVENT_LOCATION,
            CalendarContract.Instances.DESCRIPTION,
            CalendarContract.Instances.DISPLAY_COLOR
        )

        try {
            context.contentResolver.query(
                uriBuilder.build(),
                projection,
                null,
                null,
                "${CalendarContract.Instances.BEGIN} ASC"
            )?.use { cursor ->
                val idIdx = cursor.getColumnIndexOrThrow(CalendarContract.Instances._ID)
                val calIdIdx = cursor.getColumnIndexOrThrow(CalendarContract.Instances.CALENDAR_ID)
                val titleIdx = cursor.getColumnIndexOrThrow(CalendarContract.Instances.TITLE)
                val beginIdx = cursor.getColumnIndexOrThrow(CalendarContract.Instances.BEGIN)
                val endIdx = cursor.getColumnIndexOrThrow(CalendarContract.Instances.END)
                val allDayIdx = cursor.getColumnIndexOrThrow(CalendarContract.Instances.ALL_DAY)
                val locIdx = cursor.getColumnIndexOrThrow(CalendarContract.Instances.EVENT_LOCATION)
                val descIdx = cursor.getColumnIndexOrThrow(CalendarContract.Instances.DESCRIPTION)
                val colorIdx = cursor.getColumnIndexOrThrow(CalendarContract.Instances.DISPLAY_COLOR)

                while (cursor.moveToNext()) {
                    val eventId = cursor.getLong(idIdx)
                    val calId = cursor.getLong(calIdIdx)
                    val title = cursor.getString(titleIdx) ?: "(No title)"
                    val begin = cursor.getLong(beginIdx)
                    val end = cursor.getLong(endIdx)
                    val isAllDay = cursor.getInt(allDayIdx) != 0
                    val location = cursor.getString(locIdx)
                    val description = cursor.getString(descIdx)
                    val color = cursor.getInt(colorIdx)

                    events.add(
                        CalendarEvent(
                            id = eventId,
                            calendarId = calId,
                            title = title,
                            startEpochMillis = begin,
                            endEpochMillis = end,
                            isAllDay = isAllDay,
                            location = location,
                            description = description,
                            color = color
                        )
                    )
                }
            }
        } catch (e: Exception) {
            // Calendar permission or provider unavailable
        }

        return events
    }
}
