package com.kunvarpreet.skirk.data.repository

import com.kunvarpreet.skirk.domain.model.Dashboard
import com.kunvarpreet.skirk.domain.model.Panel
import com.kunvarpreet.skirk.domain.model.PanelLayout
import com.kunvarpreet.skirk.domain.model.WidgetInstance
import com.kunvarpreet.skirk.domain.model.WidgetSlot
import com.kunvarpreet.skirk.widget.model.WidgetTypeIds

/**
 * Factory providing the default out-of-the-box dashboard layout and widget stack.
 * Ensures the first-run experience is pre-populated with an intuitive configuration.
 */
object DefaultDashboardFactory {

    fun createDefaultDashboard(): Dashboard {
        val mainPanel = Panel(
            id = "panel_standby_main",
            name = "StandBy Glance",
            layout = PanelLayout.TwoSplitHorizontal,
            slots = listOf(
                WidgetSlot(
                    slotIndex = 0,
                    widgets = listOf(
                        WidgetInstance(
                            id = "inst_clock_digital",
                            widgetTypeId = WidgetTypeIds.DIGITAL_CLOCK,
                            selectedDesignId = "large"
                        ),
                        WidgetInstance(
                            id = "inst_clock_analog",
                            widgetTypeId = WidgetTypeIds.ANALOG_CLOCK,
                            selectedDesignId = "classic"
                        ),
                        WidgetInstance(
                            id = "inst_quotes",
                            widgetTypeId = WidgetTypeIds.QUOTES,
                            selectedDesignId = "typographic"
                        )
                    ),
                    activeWidgetIndex = 0
                ),
                WidgetSlot(
                    slotIndex = 1,
                    widgets = listOf(
                        WidgetInstance(
                            id = "inst_battery",
                            widgetTypeId = WidgetTypeIds.BATTERY,
                            selectedDesignId = "ring"
                        ),
                        WidgetInstance(
                            id = "inst_calendar",
                            widgetTypeId = WidgetTypeIds.CALENDAR,
                            selectedDesignId = "month_view"
                        ),
                        WidgetInstance(
                            id = "inst_media",
                            widgetTypeId = WidgetTypeIds.MEDIA_PLAYER,
                            selectedDesignId = "compact"
                        )
                    ),
                    activeWidgetIndex = 0
                )
            )
        )

        val focusPanel = Panel(
            id = "panel_standby_focus",
            name = "Focus & Media",
            layout = PanelLayout.Single,
            slots = listOf(
                WidgetSlot(
                    slotIndex = 0,
                    widgets = listOf(
                        WidgetInstance(
                            id = "inst_system",
                            widgetTypeId = WidgetTypeIds.SYSTEM_DASHBOARD,
                            selectedDesignId = "gauges"
                        ),
                        WidgetInstance(
                            id = "inst_photo",
                            widgetTypeId = WidgetTypeIds.PHOTO_SLIDESHOW,
                            selectedDesignId = "full_bleed"
                        )
                    ),
                    activeWidgetIndex = 0
                )
            )
        )

        return Dashboard(
            id = "dashboard_default",
            name = "Primary StandBy",
            panels = listOf(mainPanel, focusPanel),
            activePanelIndex = 0
        )
    }
}
