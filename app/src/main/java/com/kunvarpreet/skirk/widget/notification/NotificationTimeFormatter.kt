package com.kunvarpreet.skirk.widget.notification

import java.util.concurrent.TimeUnit

object NotificationTimeFormatter {
    fun formatRelativeTime(postTime: Long, currentTime: Long = System.currentTimeMillis()): String {
        val diffMs = (currentTime - postTime).coerceAtLeast(0L)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(diffMs)
        val hours = TimeUnit.MILLISECONDS.toHours(diffMs)
        val days = TimeUnit.MILLISECONDS.toDays(diffMs)

        return when {
            minutes < 1 -> "Just now"
            minutes < 60 -> "${minutes}m ago"
            hours < 24 -> "${hours}h ago"
            days == 1L -> "Yesterday"
            else -> "${days}d ago"
        }
    }
}
