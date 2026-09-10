package com.kunvarpreet.skirk.widget.analogclock

import com.kunvarpreet.skirk.domain.model.WidgetConfig

/**
 * Parsed configuration for the Analog Clock widget.
 */
data class AnalogClockConfig(
    val showSeconds: Boolean = true,
    val showHourNumbers: Boolean = true
) {
    companion object {
        const val KEY_SHOW_SECONDS = "show_seconds"
        const val KEY_SHOW_NUMBERS = "show_hour_numbers"
        const val KEY_SHOW_HOUR_NUMBERS = KEY_SHOW_NUMBERS

        fun from(config: WidgetConfig): AnalogClockConfig {
            val showSeconds = config.getBoolean(KEY_SHOW_SECONDS, defaultValue = true)
            val showNumbers = config.getBoolean(KEY_SHOW_NUMBERS, defaultValue = true)
            return AnalogClockConfig(
                showSeconds = showSeconds,
                showHourNumbers = showNumbers
            )
        }

        fun fromConfig(config: WidgetConfig): AnalogClockConfig = from(config)
    }

    fun toWidgetConfig(): WidgetConfig {
        return WidgetConfig(
            settings = mapOf(
                KEY_SHOW_SECONDS to showSeconds.toString(),
                KEY_SHOW_HOUR_NUMBERS to showHourNumbers.toString()
            )
        )
    }
}
