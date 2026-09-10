package com.kunvarpreet.skirk.widget.battery

import com.kunvarpreet.skirk.domain.model.WidgetConfig

/**
 * Parsed configuration for the Battery widget.
 */
data class BatteryWidgetConfig(
    val showTemperature: Boolean = true,
    val showHealth: Boolean = true,
    val showPowerSource: Boolean = true
) {
    companion object {
        const val KEY_SHOW_TEMPERATURE = "show_temperature"
        const val KEY_SHOW_HEALTH = "show_health"
        const val KEY_SHOW_POWER_SOURCE = "show_power_source"

        fun from(config: WidgetConfig): BatteryWidgetConfig {
            return BatteryWidgetConfig(
                showTemperature = config.getBoolean(KEY_SHOW_TEMPERATURE, defaultValue = true),
                showHealth = config.getBoolean(KEY_SHOW_HEALTH, defaultValue = true),
                showPowerSource = config.getBoolean(KEY_SHOW_POWER_SOURCE, defaultValue = true)
            )
        }
    }

    fun toWidgetConfig(): WidgetConfig {
        return WidgetConfig(
            settings = mapOf(
                KEY_SHOW_TEMPERATURE to showTemperature.toString(),
                KEY_SHOW_HEALTH to showHealth.toString(),
                KEY_SHOW_POWER_SOURCE to showPowerSource.toString()
            )
        )
    }
}
