package com.kunvarpreet.skirk.media.service

import android.content.ComponentName
import android.content.Context
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import java.util.concurrent.CopyOnWriteArrayList

/**
 * System notification listener service enabling Skirk to discover media sessions
 * and observe active notifications for the Notification Widget.
 */
class SkirkNotificationListenerService : NotificationListenerService() {

    interface NotificationEventListener {
        fun onNotificationPosted(sbn: StatusBarNotification)
        fun onNotificationRemoved(sbn: StatusBarNotification)
        fun onListenerConnected()
        fun onListenerDisconnected()
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        instance = this
        isConnected = true
        listeners.forEach { it.onListenerConnected() }
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        instance = null
        isConnected = false
        listeners.forEach { it.onListenerDisconnected() }
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        if (sbn != null) {
            listeners.forEach { it.onNotificationPosted(sbn) }
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
        if (sbn != null) {
            listeners.forEach { it.onNotificationRemoved(sbn) }
        }
    }

    companion object {
        var isConnected: Boolean = false
            private set

        private var instance: SkirkNotificationListenerService? = null
        private val listeners = CopyOnWriteArrayList<NotificationEventListener>()

        fun addListener(listener: NotificationEventListener) {
            listeners.add(listener)
            if (isConnected) {
                listener.onListenerConnected()
            }
        }

        fun removeListener(listener: NotificationEventListener) {
            listeners.remove(listener)
        }

        fun getActiveNotificationsSafe(): Array<StatusBarNotification>? {
            return try {
                instance?.activeNotifications
            } catch (e: Exception) {
                null
            }
        }

        fun getComponentName(context: Context): ComponentName {
            return ComponentName(context, SkirkNotificationListenerService::class.java)
        }
    }
}
