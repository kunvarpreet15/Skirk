package com.kunvarpreet.skirk.widget.notification

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.kunvarpreet.skirk.domain.model.WidgetDesign
import com.kunvarpreet.skirk.domain.model.WidgetInstance
import com.kunvarpreet.skirk.notification.domain.NotificationRepository
import com.kunvarpreet.skirk.notification.model.NotificationState
import com.kunvarpreet.skirk.widget.core.WidgetContentRenderer

/**
 * Production renderer for the Notification widget.
 */
class NotificationWidgetRenderer(
    private val notificationRepository: NotificationRepository
) : WidgetContentRenderer {

    @Composable
    override fun Render(
        instance: WidgetInstance,
        design: WidgetDesign,
        modifier: Modifier
    ) {
        val context = LocalContext.current
        val config = NotificationWidgetConfig.fromWidgetConfig(instance.config)
        val state by notificationRepository.observeNotificationState()
            .collectAsState(initial = NotificationState())

        val onRequestAccess = {
            notificationRepository.openNotificationAccessSettings(context)
        }

        when (design.id) {
            NotificationWidgetConfig.DESIGN_COMPACT -> {
                CompactNotificationDesign(
                    state = state,
                    config = config,
                    onRequestAccess = onRequestAccess,
                    modifier = modifier
                )
            }
            NotificationWidgetConfig.DESIGN_FOCUS -> {
                FocusNotificationDesign(
                    state = state,
                    config = config,
                    onRequestAccess = onRequestAccess,
                    modifier = modifier
                )
            }
            else -> {
                ListNotificationDesign(
                    state = state,
                    config = config,
                    onRequestAccess = onRequestAccess,
                    modifier = modifier
                )
            }
        }
    }
}
