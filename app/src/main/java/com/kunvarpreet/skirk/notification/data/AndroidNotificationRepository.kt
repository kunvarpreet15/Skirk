package com.kunvarpreet.skirk.notification.data

import android.app.Notification
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.service.notification.StatusBarNotification
import androidx.core.app.NotificationManagerCompat
import com.kunvarpreet.skirk.media.service.SkirkNotificationListenerService
import com.kunvarpreet.skirk.notification.domain.NotificationRepository
import com.kunvarpreet.skirk.notification.model.NotificationAccessState
import com.kunvarpreet.skirk.notification.model.NotificationItem
import com.kunvarpreet.skirk.notification.model.NotificationState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap

/**
 * Android implementation of [NotificationRepository] that observes system notifications
 * via [SkirkNotificationListenerService].
 *
 * Privacy Guarantees:
 * - Notification contents are strictly held in-memory and NEVER persisted to disk or database.
 * - Titles and text are NEVER logged to Logcat during normal operation.
 * - Notifications are not transmitted over any network.
 */
class AndroidNotificationRepository(
    private val context: Context,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
) : NotificationRepository, SkirkNotificationListenerService.NotificationEventListener {

    private val notificationMap = ConcurrentHashMap<String, NotificationItem>()
    private val _state = MutableStateFlow(calculateCurrentState())

    init {
        SkirkNotificationListenerService.addListener(this)
        refreshNotifications()
    }

    override fun observeNotificationState(): Flow<NotificationState> = _state.asStateFlow()

    override fun isNotificationAccessGranted(): Boolean {
        return try {
            val enabledPackages = NotificationManagerCompat.getEnabledListenerPackages(context)
            enabledPackages.contains(context.packageName)
        } catch (e: Exception) {
            false
        }
    }

    override fun openNotificationAccessSettings(context: Context) {
        val componentName = ComponentName(context, SkirkNotificationListenerService::class.java)
        val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Intent(Settings.ACTION_NOTIFICATION_LISTENER_DETAIL_SETTINGS).apply {
                putExtra(Settings.EXTRA_NOTIFICATION_LISTENER_COMPONENT_NAME, componentName.flattenToString())
            }
        } else {
            Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            val fallbackIntent = Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            try {
                context.startActivity(fallbackIntent)
            } catch (ignored: Exception) {
            }
        }
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val item = mapToItem(sbn)
        if (item != null) {
            notificationMap[item.key] = item
            publishState()
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        val key = sbn.key ?: "${sbn.packageName}_${sbn.id}"
        if (notificationMap.remove(key) != null) {
            publishState()
        }
    }

    override fun onListenerConnected() {
        refreshNotifications()
    }

    override fun onListenerDisconnected() {
        notificationMap.clear()
        publishState()
    }

    fun refreshNotifications() {
        scope.launch {
            if (!isNotificationAccessGranted()) {
                notificationMap.clear()
                _state.value = NotificationState(
                    accessState = NotificationAccessState.PERMISSION_REQUIRED,
                    notifications = emptyList()
                )
                return@launch
            }

            val active = SkirkNotificationListenerService.getActiveNotificationsSafe()
            if (active != null) {
                notificationMap.clear()
                for (sbn in active) {
                    val item = mapToItem(sbn)
                    if (item != null) {
                        notificationMap[item.key] = item
                    }
                }
            }
            publishState()
        }
    }

    private fun publishState() {
        _state.value = calculateCurrentState()
    }

    private fun calculateCurrentState(): NotificationState {
        val hasAccess = isNotificationAccessGranted()
        if (!hasAccess) {
            return NotificationState(
                accessState = NotificationAccessState.PERMISSION_REQUIRED,
                notifications = emptyList()
            )
        }

        val sortedList = notificationMap.values
            .sortedByDescending { it.postTime }
            .toList()

        return NotificationState(
            accessState = NotificationAccessState.GRANTED,
            notifications = sortedList
        )
    }

    private fun mapToItem(sbn: StatusBarNotification): NotificationItem? {
        val notification = sbn.notification ?: return null
        val extras = notification.extras

        val title = extras?.getCharSequence(Notification.EXTRA_TITLE)?.toString()?.trim()
            ?: extras?.getCharSequence(Notification.EXTRA_TITLE_BIG)?.toString()?.trim()
            ?: ""

        val text = extras?.getCharSequence(Notification.EXTRA_TEXT)?.toString()?.trim()
            ?: extras?.getCharSequence(Notification.EXTRA_BIG_TEXT)?.toString()?.trim()
            ?: extras?.getCharSequence(Notification.EXTRA_SUMMARY_TEXT)?.toString()?.trim()
            ?: ""

        // Ignore completely blank system notifications
        if (title.isBlank() && text.isBlank()) {
            return null
        }

        val subText = extras?.getCharSequence(Notification.EXTRA_SUB_TEXT)?.toString()?.trim()

        val appName = try {
            val pm = context.packageManager
            val appInfo = pm.getApplicationInfo(sbn.packageName, 0)
            pm.getApplicationLabel(appInfo).toString()
        } catch (e: Exception) {
            sbn.packageName
        }

        val isOngoing = (notification.flags and Notification.FLAG_ONGOING_EVENT) != 0
        val contentIntent = notification.contentIntent
        val openAction: (() -> Unit)? = contentIntent?.let { pendingIntent ->
            {
                try {
                    pendingIntent.send()
                } catch (e: Exception) {
                    // Safe swallow if canceled or intent resolution fails
                }
            }
        }

        return NotificationItem(
            key = sbn.key ?: "${sbn.packageName}_${sbn.id}",
            id = sbn.id,
            packageName = sbn.packageName,
            appName = appName,
            title = title,
            text = text,
            subText = subText,
            postTime = sbn.postTime,
            isOngoing = isOngoing,
            groupKey = sbn.groupKey,
            openNotification = openAction
        )
    }
}
