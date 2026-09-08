package com.kunvarpreet.skirk.domain.model

import kotlinx.serialization.Serializable

/**
 * Arbitrary user configuration parameters specific to a widget instance.
 * Allows storing custom settings (e.g. 24h format, location ID, photo album path)
 * without polluting the core dashboard models.
 */
@Serializable
data class WidgetConfig(
    val settings: Map<String, String> = emptyMap()
) {
    fun getString(key: String, defaultValue: String = ""): String =
        settings[key] ?: defaultValue

    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean =
        settings[key]?.toBooleanStrictOrNull() ?: defaultValue

    fun getInt(key: String, defaultValue: Int = 0): Int =
        settings[key]?.toIntOrNull() ?: defaultValue

    fun withSetting(key: String, value: String): WidgetConfig =
        copy(settings = settings + (key to value))
}
