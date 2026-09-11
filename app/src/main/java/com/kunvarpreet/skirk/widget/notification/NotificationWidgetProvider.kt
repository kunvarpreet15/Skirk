package com.kunvarpreet.skirk.widget.notification

import com.kunvarpreet.skirk.domain.model.WidgetCategory
import com.kunvarpreet.skirk.domain.model.WidgetDefinition
import com.kunvarpreet.skirk.domain.model.WidgetDesign
import com.kunvarpreet.skirk.notification.domain.MockNotificationRepository
import com.kunvarpreet.skirk.notification.domain.NotificationRepository
import com.kunvarpreet.skirk.widget.core.WidgetContentRenderer
import com.kunvarpreet.skirk.widget.core.WidgetProvider
import com.kunvarpreet.skirk.widget.model.WidgetTypeIds

/**
 * Production provider for the built-in Notification widget.
 */
class NotificationWidgetProvider(
    private val notificationRepository: NotificationRepository = MockNotificationRepository()
) : WidgetProvider {

    private val renderer = NotificationWidgetRenderer(notificationRepository)

    override val definition: WidgetDefinition = WidgetDefinition(
        id = WidgetTypeIds.NOTIFICATIONS,
        displayName = "Notifications",
        description = "Glanceable view of recent Android notifications",
        category = WidgetCategory.SYSTEM,
        availableDesigns = listOf(
            WidgetDesign(
                id = NotificationWidgetConfig.DESIGN_LIST,
                displayName = "Notification List"
            ),
            WidgetDesign(
                id = NotificationWidgetConfig.DESIGN_COMPACT,
                displayName = "Compact Rows"
            ),
            WidgetDesign(
                id = NotificationWidgetConfig.DESIGN_FOCUS,
                displayName = "Focus Spotlight"
            )
        ),
        defaultDesignId = NotificationWidgetConfig.DESIGN_LIST
    )

    override fun getRenderer(designId: String): WidgetContentRenderer = renderer
}
