package com.kunvarpreet.skirk.notification.model

/**
 * Clean domain representation of an Android system notification.
 * Decoupled from raw [android.service.notification.StatusBarNotification] to prevent leaking
 * Android framework types into Compose UI.
 */
data class NotificationItem(
    val key: String,
    val id: Int,
    val packageName: String,
    val appName: String,
    val title: String,
    val text: String,
    val subText: String? = null,
    val postTime: Long = System.currentTimeMillis(),
    val isOngoing: Boolean = false,
    val groupKey: String? = null,
    val openNotification: (() -> Unit)? = null
)

/**
 * Access permission state for Android notification listener service.
 */
enum class NotificationAccessState {
    GRANTED,
    PERMISSION_REQUIRED
}

/**
 * Current UI state for the notification widget.
 */
data class NotificationState(
    val accessState: NotificationAccessState = NotificationAccessState.PERMISSION_REQUIRED,
    val notifications: List<NotificationItem> = emptyList()
) {
    val isEmpty: Boolean get() = accessState == NotificationAccessState.GRANTED && notifications.isEmpty()

    companion object {
        fun sample(currentTime: Long = System.currentTimeMillis()): NotificationState {
            return NotificationState(
                accessState = NotificationAccessState.GRANTED,
                notifications = listOf(
                    NotificationItem(
                        key = "sample_1",
                        id = 101,
                        packageName = "com.whatsapp",
                        appName = "WhatsApp",
                        title = "Alice Smith",
                        text = "Hey, are you free for the meeting this afternoon?",
                        postTime = currentTime - 2 * 60 * 1000L,
                        isOngoing = false
                    ),
                    NotificationItem(
                        key = "sample_2",
                        id = 102,
                        packageName = "com.google.android.gm",
                        appName = "Gmail",
                        title = "GitHub",
                        text = "[Skirk] Phase 7 pull request successfully merged",
                        postTime = currentTime - 15 * 60 * 1000L,
                        isOngoing = false
                    ),
                    NotificationItem(
                        key = "sample_3",
                        id = 103,
                        packageName = "com.slack",
                        appName = "Slack",
                        title = "Engineering Team",
                        text = "Deployment completed to production servers.",
                        postTime = currentTime - 45 * 60 * 1000L,
                        isOngoing = false
                    ),
                    NotificationItem(
                        key = "sample_4",
                        id = 104,
                        packageName = "com.google.android.calendar",
                        appName = "Calendar",
                        title = "Doctor's Appointment",
                        text = "Appointment scheduled at 4:30 PM",
                        postTime = currentTime - 2 * 3600 * 1000L,
                        isOngoing = false
                    )
                )
            )
        }
    }
}
