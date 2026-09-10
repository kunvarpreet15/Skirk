package com.kunvarpreet.skirk.widget.common

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Snapshot of current time formatted for widget consumption.
 */
data class TimeSnapshot(
    val time: LocalDateTime = LocalDateTime.now(),
    val hours12: Int = if (time.hour % 12 == 0) 12 else time.hour % 12,
    val hours24: Int = time.hour,
    val minutes: Int = time.minute,
    val seconds: Int = time.second,
    val isAm: Boolean = time.hour < 12,
    val dayOfWeek: String = time.dayOfWeek.name,
    val dayOfMonth: Int = time.dayOfMonth,
    val monthName: String = time.month.name
) {
    fun formatTime(is24Hour: Boolean, showSeconds: Boolean): String {
        val pattern = when {
            is24Hour && showSeconds -> "HH:mm:ss"
            is24Hour -> "HH:mm"
            showSeconds -> "h:mm:ss"
            else -> "h:mm"
        }
        return time.format(DateTimeFormatter.ofPattern(pattern, Locale.getDefault()))
    }

    fun formatDate(pattern: String = "EEEE, d MMMM"): String {
        return time.format(DateTimeFormatter.ofPattern(pattern, Locale.getDefault()))
    }
}

/**
 * Lifecycle-aware Compose state for observing the current time with optimal energy efficiency.
 *
 * Efficiency Guarantees:
 * - If [includeSeconds] is FALSE: Sleeps until the top of the next minute (`delay(60000 - (now % 60000))`).
 *   Avoids 60 unnecessary recompositions every minute during long StandBy charging sessions.
 * - If [includeSeconds] is TRUE: Updates once per second aligned to second boundaries.
 * - Broadcast-Aware: Listens to system time, time-zone, and date change intents (`ACTION_TIME_CHANGED`,
 *   `ACTION_TIMEZONE_CHANGED`, `ACTION_DATE_CHANGED`).
 * - Lifecycle-Aware: Cancels automatically when Composable leaves the active composition (e.g. on panel or slot swipe).
 */
@Composable
fun rememberCurrentTime(
    includeSeconds: Boolean = false,
    zoneId: ZoneId = ZoneId.systemDefault()
): State<TimeSnapshot> {
    val context = LocalContext.current
    val timeState = remember { mutableStateOf(TimeSnapshot(LocalDateTime.now(zoneId))) }

    // Coroutine ticker that adapts to includeSeconds
    androidx.compose.runtime.LaunchedEffect(includeSeconds, zoneId) {
        while (true) {
            val now = LocalDateTime.now(zoneId)
            timeState.value = TimeSnapshot(now)

            if (includeSeconds) {
                // Sleep until the top of the next second
                val millisInCurrentSecond = System.currentTimeMillis() % 1000L
                val sleepMillis = (1000L - millisInCurrentSecond).coerceAtLeast(50L)
                delay(sleepMillis)
            } else {
                // Sleep until the top of the next minute
                val millisInCurrentMinute = System.currentTimeMillis() % 60000L
                val sleepMillis = (60000L - millisInCurrentMinute).coerceAtLeast(100L)
                delay(sleepMillis)
            }
        }
    }

    // System Broadcast Receiver for external time changes (e.g. user changes time, timezone, or daylight saving)
    DisposableEffect(context, zoneId) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(c: Context?, intent: Intent?) {
                timeState.value = TimeSnapshot(LocalDateTime.now(zoneId))
            }
        }

        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_TIME_CHANGED)
            addAction(Intent.ACTION_TIMEZONE_CHANGED)
            addAction(Intent.ACTION_DATE_CHANGED)
            addAction(Intent.ACTION_LOCALE_CHANGED)
        }

        context.registerReceiver(receiver, filter)

        onDispose {
            try {
                context.unregisterReceiver(receiver)
            } catch (e: Exception) {
                // Ignore if already unregistered
            }
        }
    }

    return timeState
}
