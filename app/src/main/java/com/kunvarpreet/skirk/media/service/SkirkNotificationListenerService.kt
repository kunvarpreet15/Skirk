package com.kunvarpreet.skirk.media.service

import android.content.ComponentName
import android.content.Context
import android.service.notification.NotificationListenerService

/**
 * Required system service stub enabling Skirk to discover external media sessions
 * via Android's [android.media.session.MediaSessionManager].
 */
class SkirkNotificationListenerService : NotificationListenerService() {

    override fun onListenerConnected() {
        super.onListenerConnected()
        isConnected = true
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        isConnected = false
    }

    companion object {
        var isConnected: Boolean = false
            private set

        fun getComponentName(context: Context): ComponentName {
            return ComponentName(context, SkirkNotificationListenerService::class.java)
        }
    }
}
