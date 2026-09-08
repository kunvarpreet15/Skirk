package com.kunvarpreet.skirk.navigation

/**
 * Navigation destination routes for Skirk.
 */
sealed class Screen(val route: String) {
    data object StandByDashboard : Screen("standby_dashboard")
    data object Settings : Screen("settings")
    data object DashboardEditor : Screen("dashboard_editor")

    data object WidgetPicker : Screen("widget_picker/{panelId}/{slotIndex}") {
        fun createRoute(panelId: String, slotIndex: Int): String =
            "widget_picker/$panelId/$slotIndex"
    }

    data object WidgetConfiguration : Screen("widget_configuration/{instanceId}") {
        fun createRoute(instanceId: String): String =
            "widget_configuration/$instanceId"
    }
}
