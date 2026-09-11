package com.kunvarpreet.skirk.widget.schedule

import com.kunvarpreet.skirk.domain.model.WidgetConfig

/**
 * Parsed configuration for Schedule widget instances.
 */
data class ScheduleWidgetConfig(
    val selectedDesign: String = "timeline",
    val maxEvents: Int = 8,
    val showPastEvents: Boolean = false,
    val showAllDayEvents: Boolean = true,
    val showLocation: Boolean = true
) {
    companion object {
        const val KEY_SELECTED_DESIGN = "selected_design"
        const val KEY_MAX_EVENTS = "max_events"
        const val KEY_SHOW_PAST_EVENTS = "show_past_events"
        const val KEY_SHOW_ALL_DAY_EVENTS = "show_all_day_events"
        const val KEY_SHOW_LOCATION = "show_location"

        fun from(config: WidgetConfig): ScheduleWidgetConfig {
            return ScheduleWidgetConfig(
                selectedDesign = config.getString(KEY_SELECTED_DESIGN, defaultValue = "timeline"),
                maxEvents = config.getInt(KEY_MAX_EVENTS, defaultValue = 8),
                showPastEvents = config.getBoolean(KEY_SHOW_PAST_EVENTS, defaultValue = false),
                showAllDayEvents = config.getBoolean(KEY_SHOW_ALL_DAY_EVENTS, defaultValue = true),
                showLocation = config.getBoolean(KEY_SHOW_LOCATION, defaultValue = true)
            )
        }

        fun fromConfig(config: WidgetConfig): ScheduleWidgetConfig = from(config)
    }

    fun toWidgetConfig(): WidgetConfig {
        return WidgetConfig(
            settings = mapOf(
                KEY_SELECTED_DESIGN to selectedDesign,
                KEY_MAX_EVENTS to maxEvents.toString(),
                KEY_SHOW_PAST_EVENTS to showPastEvents.toString(),
                KEY_SHOW_ALL_DAY_EVENTS to showAllDayEvents.toString(),
                KEY_SHOW_LOCATION to showLocation.toString()
            )
        )
    }
}
