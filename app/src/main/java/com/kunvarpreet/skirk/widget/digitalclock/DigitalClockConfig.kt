package com.kunvarpreet.skirk.widget.digitalclock

import android.content.Context
import android.text.format.DateFormat
import com.kunvarpreet.skirk.domain.model.WidgetConfig

/**
 * Parsed configuration for the Digital Clock widget.
 */
data class DigitalClockConfig(
    val is24Hour: Boolean = true,
    val showSeconds: Boolean = false,
    val showDate: Boolean = true
) {
    companion object {
        const val KEY_IS_24_HOUR = "is_24_hour"
        const val KEY_SHOW_SECONDS = "show_seconds"
        const val KEY_SHOW_DATE = "show_date"

        fun from(config: WidgetConfig, context: Context? = null): DigitalClockConfig {
            val system24Hour = context?.let { DateFormat.is24HourFormat(it) } ?: true
            val is24Hour = config.settings[KEY_IS_24_HOUR]?.toBooleanStrictOrNull() ?: system24Hour
            val showSeconds = config.getBoolean(KEY_SHOW_SECONDS, defaultValue = false)
            val showDate = config.getBoolean(KEY_SHOW_DATE, defaultValue = true)

            return DigitalClockConfig(
                is24Hour = is24Hour,
                showSeconds = showSeconds,
                showDate = showDate
            )
        }

        fun fromConfig(config: WidgetConfig, context: Context? = null): DigitalClockConfig = from(config, context)
    }

    fun toWidgetConfig(): WidgetConfig {
        return WidgetConfig(
            settings = mapOf(
                KEY_IS_24_HOUR to is24Hour.toString(),
                KEY_SHOW_SECONDS to showSeconds.toString(),
                KEY_SHOW_DATE to showDate.toString()
            )
        )
    }
}
