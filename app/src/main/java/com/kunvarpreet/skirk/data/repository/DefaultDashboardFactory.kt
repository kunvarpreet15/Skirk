package com.kunvarpreet.skirk.data.repository

import com.kunvarpreet.skirk.domain.model.Dashboard
import com.kunvarpreet.skirk.domain.model.Panel
import com.kunvarpreet.skirk.domain.model.PanelLayout
import com.kunvarpreet.skirk.domain.model.WidgetInstance
import com.kunvarpreet.skirk.domain.model.WidgetSlot
import com.kunvarpreet.skirk.widget.model.WidgetTypeIds

/**
 * Factory providing the default out-of-the-box dashboard layout and widget stacks.
 * Pre-configures three diverse panels showcasing different layouts and multi-widget stacks.
 */
object DefaultDashboardFactory {

    fun createDefaultDashboard(): Dashboard {
        // Panel 1: Two Columns (Clock & Battery)
        val panel1 = Panel(
            id = "panel_clock_battery",
            name = "Clock & Battery",
            layout = PanelLayout.TwoSplitHorizontal,
            slots = listOf(
                WidgetSlot(
                    id = "slot_p1_0",
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
                        )
                    ),
                    activeWidgetIndex = 0
                ),
                WidgetSlot(
                    id = "slot_p1_1",
                    slotIndex = 1,
                    widgets = listOf(
                        WidgetInstance(
                            id = "inst_battery",
                            widgetTypeId = WidgetTypeIds.BATTERY,
                            selectedDesignId = "ring"
                        ),
                        WidgetInstance(
                            id = "inst_quotes_1",
                            widgetTypeId = WidgetTypeIds.QUOTES,
                            selectedDesignId = "typographic"
                        )
                    ),
                    activeWidgetIndex = 0
                )
            )
        )

        // Panel 2: Single Full Focus (Media & System)
        val panel2 = Panel(
            id = "panel_media_focus",
            name = "Media & Focus",
            layout = PanelLayout.Single,
            slots = listOf(
                WidgetSlot(
                    id = "slot_p2_0",
                    slotIndex = 0,
                    widgets = listOf(
                        WidgetInstance(
                            id = "inst_media",
                            widgetTypeId = WidgetTypeIds.MEDIA_PLAYER,
                            selectedDesignId = "compact"
                        ),
                        WidgetInstance(
                            id = "inst_system",
                            widgetTypeId = WidgetTypeIds.SYSTEM_DASHBOARD,
                            selectedDesignId = "gauges"
                        )
                    ),
                    activeWidgetIndex = 0
                )
            )
        )

        // Panel 3: Four Widgets Grid (Glance Grid)
        val panel3 = Panel(
            id = "panel_glance_grid",
            name = "Glance Grid",
            layout = PanelLayout.Grid4,
            slots = listOf(
                WidgetSlot(
                    id = "slot_p3_0",
                    slotIndex = 0,
                    widgets = listOf(
                        WidgetInstance(
                            id = "inst_calendar",
                            widgetTypeId = WidgetTypeIds.CALENDAR,
                            selectedDesignId = "month_view"
                        )
                    ),
                    activeWidgetIndex = 0
                ),
                WidgetSlot(
                    id = "slot_p3_1",
                    slotIndex = 1,
                    widgets = listOf(
                        WidgetInstance(
                            id = "inst_schedule",
                            widgetTypeId = WidgetTypeIds.SCHEDULE,
                            selectedDesignId = "timeline"
                        )
                    ),
                    activeWidgetIndex = 0
                ),
                WidgetSlot(
                    id = "slot_p3_2",
                    slotIndex = 2,
                    widgets = listOf(
                        WidgetInstance(
                            id = "inst_quotes_2",
                            widgetTypeId = WidgetTypeIds.QUOTES,
                            selectedDesignId = "minimal_italic"
                        )
                    ),
                    activeWidgetIndex = 0
                ),
                WidgetSlot(
                    id = "slot_p3_3",
                    slotIndex = 3,
                    widgets = listOf(
                        WidgetInstance(
                            id = "inst_countdown",
                            widgetTypeId = WidgetTypeIds.COUNTDOWN,
                            selectedDesignId = "days_remaining"
                        )
                    ),
                    activeWidgetIndex = 0
                )
            )
        )

        return Dashboard(
            id = "dashboard_default",
            name = "Primary StandBy",
            schemaVersion = 1,
            panels = listOf(panel1, panel2, panel3),
            activePanelIndex = 0
        )
    }
}
