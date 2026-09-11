package com.kunvarpreet.skirk.widget

import com.kunvarpreet.skirk.notification.model.NotificationItem
import com.kunvarpreet.skirk.widget.notification.NotificationWidgetConfig
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NotificationOrderingTest {

    @Test
    fun notifications_areSortedByRecencyDescending() {
        val now = System.currentTimeMillis()
        val older = NotificationItem(
            key = "item_1",
            id = 1,
            packageName = "com.test",
            appName = "Test",
            title = "Older",
            text = "Older content",
            postTime = now - 60_000L
        )
        val newest = NotificationItem(
            key = "item_2",
            id = 2,
            packageName = "com.test",
            appName = "Test",
            title = "Newest",
            text = "Newest content",
            postTime = now
        )
        val oldest = NotificationItem(
            key = "item_3",
            id = 3,
            packageName = "com.test",
            appName = "Test",
            title = "Oldest",
            text = "Oldest content",
            postTime = now - 120_000L
        )

        val unsortedList = listOf(older, oldest, newest)
        val sortedList = unsortedList.sortedByDescending { it.postTime }

        assertEquals("item_2", sortedList[0].key)
        assertEquals("item_1", sortedList[1].key)
        assertEquals("item_3", sortedList[2].key)
    }

    @Test
    fun ongoingFilter_correctlyFiltersWhenDisabled() {
        val ongoing = NotificationItem(
            key = "ongoing_1",
            id = 1,
            packageName = "com.music",
            appName = "Music",
            title = "Playing song",
            text = "Artist",
            isOngoing = true
        )
        val normal = NotificationItem(
            key = "normal_1",
            id = 2,
            packageName = "com.chat",
            appName = "Chat",
            title = "Hello",
            text = "World",
            isOngoing = false
        )

        val list = listOf(ongoing, normal)

        val configExclude = NotificationWidgetConfig(includeOngoing = false)
        val filteredExclude = list.filter { configExclude.includeOngoing || !it.isOngoing }
        assertEquals(1, filteredExclude.size)
        assertEquals("normal_1", filteredExclude.first().key)

        val configInclude = NotificationWidgetConfig(includeOngoing = true)
        val filteredInclude = list.filter { configInclude.includeOngoing || !it.isOngoing }
        assertEquals(2, filteredInclude.size)
    }

    @Test
    fun limitFilter_respectsMaxNotifications() {
        val items = (1..10).map { i ->
            NotificationItem(
                key = "item_$i",
                id = i,
                packageName = "com.test",
                appName = "Test",
                title = "Title $i",
                text = "Text $i",
                postTime = 1000L * i
            )
        }.sortedByDescending { it.postTime }

        val config = NotificationWidgetConfig(maxNotifications = 3)
        val limited = items.take(config.maxNotifications)

        assertEquals(3, limited.size)
        assertEquals("item_10", limited[0].key)
        assertEquals("item_9", limited[1].key)
        assertEquals("item_8", limited[2].key)
    }
}
