package com.kunvarpreet.skirk.widget.notification

import com.kunvarpreet.skirk.domain.model.WidgetConfig

/**
 * Configuration options for the Notification widget.
 */
data class NotificationWidgetConfig(
    val selectedDesign: String = DESIGN_LIST,
    val maxNotifications: Int = 5,
    val showAppNames: Boolean = true,
    val showTimestamps: Boolean = true,
    val includeOngoing: Boolean = false
) {
    fun toWidgetConfig(): WidgetConfig {
        return WidgetConfig(
            settings = mapOf(
                KEY_SELECTED_DESIGN to selectedDesign,
                KEY_MAX_NOTIFICATIONS to maxNotifications.toString(),
                KEY_SHOW_APP_NAMES to showAppNames.toString(),
                KEY_SHOW_TIMESTAMPS to showTimestamps.toString(),
                KEY_INCLUDE_ONGOING to includeOngoing.toString()
            )
        )
    }

    companion object {
        const val DESIGN_LIST = "notification_list"
        const val DESIGN_COMPACT = "compact_rows"
        const val DESIGN_FOCUS = "focus_single"

        const val KEY_SELECTED_DESIGN = "selected_design"
        const val KEY_MAX_NOTIFICATIONS = "max_notifications"
        const val KEY_SHOW_APP_NAMES = "show_app_names"
        const val KEY_SHOW_TIMESTAMPS = "show_timestamps"
        const val KEY_INCLUDE_ONGOING = "include_ongoing"

        fun fromWidgetConfig(config: WidgetConfig?): NotificationWidgetConfig {
            if (config == null) return NotificationWidgetConfig()
            val design = config.getString(KEY_SELECTED_DESIGN, DESIGN_LIST)
            val max = config.getInt(KEY_MAX_NOTIFICATIONS, 5).coerceIn(1, 20)
            val showApps = config.getBoolean(KEY_SHOW_APP_NAMES, true)
            val showTimes = config.getBoolean(KEY_SHOW_TIMESTAMPS, true)
            val ongoing = config.getBoolean(KEY_INCLUDE_ONGOING, false)

            return NotificationWidgetConfig(
                selectedDesign = design,
                maxNotifications = max,
                showAppNames = showApps,
                showTimestamps = showTimes,
                includeOngoing = ongoing
            )
        }
    }
}
