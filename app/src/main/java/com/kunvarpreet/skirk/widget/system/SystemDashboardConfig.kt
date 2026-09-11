package com.kunvarpreet.skirk.widget.system

import com.kunvarpreet.skirk.domain.model.WidgetConfig

/**
 * Configuration options for the System Dashboard widget.
 */
data class SystemDashboardConfig(
    val selectedDesign: String = DESIGN_GRID,
    val showRam: Boolean = true,
    val showStorage: Boolean = true,
    val showBattery: Boolean = true,
    val showNetwork: Boolean = true
) {
    fun toWidgetConfig(): WidgetConfig {
        return WidgetConfig(
            settings = mapOf(
                KEY_SELECTED_DESIGN to selectedDesign,
                KEY_SHOW_RAM to showRam.toString(),
                KEY_SHOW_STORAGE to showStorage.toString(),
                KEY_SHOW_BATTERY to showBattery.toString(),
                KEY_SHOW_NETWORK to showNetwork.toString()
            )
        )
    }

    companion object {
        const val DESIGN_GRID = "gauges"
        const val DESIGN_RINGS = "rings"
        const val DESIGN_MINIMAL = "matrix"

        const val KEY_SELECTED_DESIGN = "selected_design"
        const val KEY_SHOW_RAM = "show_ram"
        const val KEY_SHOW_STORAGE = "show_storage"
        const val KEY_SHOW_BATTERY = "show_battery"
        const val KEY_SHOW_NETWORK = "show_network"

        fun fromWidgetConfig(config: WidgetConfig?): SystemDashboardConfig {
            if (config == null) return SystemDashboardConfig()
            val design = config.getString(KEY_SELECTED_DESIGN, DESIGN_GRID)
            val ram = config.getBoolean(KEY_SHOW_RAM, true)
            val storage = config.getBoolean(KEY_SHOW_STORAGE, true)
            val battery = config.getBoolean(KEY_SHOW_BATTERY, true)
            val network = config.getBoolean(KEY_SHOW_NETWORK, true)

            return SystemDashboardConfig(
                selectedDesign = design,
                showRam = ram,
                showStorage = storage,
                showBattery = battery,
                showNetwork = network
            )
        }
    }
}
