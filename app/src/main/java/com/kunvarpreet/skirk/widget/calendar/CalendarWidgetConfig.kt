package com.kunvarpreet.skirk.widget.calendar

import com.kunvarpreet.skirk.domain.model.WidgetConfig

/**
 * Parsed configuration for Calendar widget instances.
 */
data class CalendarWidgetConfig(
    val selectedDesign: String = "classic",
    val showEventIndicators: Boolean = true,
    val showAdjacentMonthDays: Boolean = true,
    val highlightCurrentDay: Boolean = true
) {
    companion object {
        const val KEY_SELECTED_DESIGN = "selected_design"
        const val KEY_SHOW_EVENT_INDICATORS = "show_event_indicators"
        const val KEY_SHOW_ADJACENT_DAYS = "show_adjacent_days"
        const val KEY_HIGHLIGHT_CURRENT_DAY = "highlight_current_day"

        fun from(config: WidgetConfig): CalendarWidgetConfig {
            return CalendarWidgetConfig(
                selectedDesign = config.getString(KEY_SELECTED_DESIGN, defaultValue = "classic"),
                showEventIndicators = config.getBoolean(KEY_SHOW_EVENT_INDICATORS, defaultValue = true),
                showAdjacentMonthDays = config.getBoolean(KEY_SHOW_ADJACENT_DAYS, defaultValue = true),
                highlightCurrentDay = config.getBoolean(KEY_HIGHLIGHT_CURRENT_DAY, defaultValue = true)
            )
        }

        fun fromConfig(config: WidgetConfig): CalendarWidgetConfig = from(config)
    }

    fun toWidgetConfig(): WidgetConfig {
        return WidgetConfig(
            settings = mapOf(
                KEY_SELECTED_DESIGN to selectedDesign,
                KEY_SHOW_EVENT_INDICATORS to showEventIndicators.toString(),
                KEY_SHOW_ADJACENT_DAYS to showAdjacentMonthDays.toString(),
                KEY_HIGHLIGHT_CURRENT_DAY to highlightCurrentDay.toString()
            )
        )
    }
}
