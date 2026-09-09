package com.kunvarpreet.skirk.domain

import com.kunvarpreet.skirk.data.repository.DefaultDashboardFactory
import com.kunvarpreet.skirk.domain.model.Dashboard
import com.kunvarpreet.skirk.domain.model.Panel
import com.kunvarpreet.skirk.domain.model.PanelLayout
import com.kunvarpreet.skirk.domain.model.WidgetConfig
import com.kunvarpreet.skirk.domain.model.WidgetInstance
import com.kunvarpreet.skirk.domain.model.WidgetSlot
import com.kunvarpreet.skirk.widget.core.WidgetRegistry
import com.kunvarpreet.skirk.widget.definitions.BuiltInWidgetDefinitions
import com.kunvarpreet.skirk.widget.model.WidgetTypeIds
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DashboardEngineTest {

    @Test
    fun testMultiplePanelsNavigation() {
        val panel1 = Panel(id = "p1", name = "Panel 1", layout = PanelLayout.Single)
        val panel2 = Panel(id = "p2", name = "Panel 2", layout = PanelLayout.TwoSplitHorizontal)
        val panel3 = Panel(id = "p3", name = "Panel 3", layout = PanelLayout.Grid4)

        var dashboard = Dashboard(
            id = "test_dash",
            panels = listOf(panel1, panel2, panel3),
            activePanelIndex = 0
        )

        // Forward cycle: 0 -> 1 -> 2 -> 0
        assertEquals("p1", dashboard.activePanel?.id)
        dashboard = dashboard.nextPanel()
        assertEquals("p2", dashboard.activePanel?.id)
        dashboard = dashboard.nextPanel()
        assertEquals("p3", dashboard.activePanel?.id)
        dashboard = dashboard.nextPanel()
        assertEquals("p1", dashboard.activePanel?.id)

        // Backward cycle: 0 -> 2 -> 1 -> 0
        dashboard = dashboard.previousPanel()
        assertEquals("p3", dashboard.activePanel?.id)
        dashboard = dashboard.previousPanel()
        assertEquals("p2", dashboard.activePanel?.id)
        dashboard = dashboard.previousPanel()
        assertEquals("p1", dashboard.activePanel?.id)

        // Direct select
        dashboard = dashboard.selectPanel(2)
        assertEquals("p3", dashboard.activePanel?.id)
    }

    @Test
    fun testLayoutSlotCounts() {
        assertEquals(1, PanelLayout.Single.slotCount)
        assertEquals(2, PanelLayout.TwoSplitHorizontal.slotCount)
        assertEquals(2, PanelLayout.TwoSplitVertical.slotCount)
        assertEquals(4, PanelLayout.Grid4.slotCount)
        assertEquals(6, PanelLayout.Grid6.slotCount)
    }

    @Test
    fun testMultipleWidgetsInSlotStack() {
        val w1 = WidgetInstance(id = "w1", widgetTypeId = WidgetTypeIds.DIGITAL_CLOCK, selectedDesignId = "large")
        val w2 = WidgetInstance(id = "w2", widgetTypeId = WidgetTypeIds.CALENDAR, selectedDesignId = "month_view")
        val w3 = WidgetInstance(id = "w3", widgetTypeId = WidgetTypeIds.QUOTES, selectedDesignId = "typographic")

        var slot = WidgetSlot(
            id = "slot_1",
            slotIndex = 0,
            widgets = listOf(w1, w2, w3),
            activeWidgetIndex = 0
        )

        assertEquals("w1", slot.activeWidget?.id)

        // Cycle next
        slot = slot.nextWidget()
        assertEquals("w2", slot.activeWidget?.id)

        slot = slot.nextWidget()
        assertEquals("w3", slot.activeWidget?.id)

        // Wraps to beginning
        slot = slot.nextWidget()
        assertEquals("w1", slot.activeWidget?.id)

        // Cycle previous wraps to end
        slot = slot.previousWidget()
        assertEquals("w3", slot.activeWidget?.id)
    }

    @Test
    fun testPanelAndWidgetModifications() {
        var dashboard = Dashboard(id = "d1", name = "Test Dashboard")

        // Add Panel
        val panel = Panel(
            id = "p1",
            name = "Panel A",
            layout = PanelLayout.TwoSplitHorizontal,
            slots = listOf(
                WidgetSlot(id = "s1", slotIndex = 0),
                WidgetSlot(id = "s2", slotIndex = 1)
            )
        )
        dashboard = dashboard.addPanel(panel)
        assertEquals(1, dashboard.panels.size)
        assertEquals("p1", dashboard.panels[0].id)

        // Add Widget to slot s1
        val widget1 = WidgetInstance(id = "w1", widgetTypeId = WidgetTypeIds.BATTERY, selectedDesignId = "ring")
        dashboard = dashboard.updatePanel("p1") { p ->
            p.updateSlotById("s1") { s -> s.addWidget(widget1) }
        }
        assertEquals(1, dashboard.panels[0].getSlotById("s1")?.widgets?.size)
        assertEquals("w1", dashboard.panels[0].getSlotById("s1")?.activeWidget?.id)

        // Add second widget to slot s1
        val widget2 = WidgetInstance(id = "w2", widgetTypeId = WidgetTypeIds.QUOTES, selectedDesignId = "minimal_italic")
        dashboard = dashboard.updatePanel("p1") { p ->
            p.updateSlotById("s1") { s -> s.addWidget(widget2) }
        }
        assertEquals(2, dashboard.panels[0].getSlotById("s1")?.widgets?.size)

        // Reorder widgets in slot s1
        dashboard = dashboard.updatePanel("p1") { p ->
            p.updateSlotById("s1") { s -> s.reorderWidgets(listOf("w2", "w1")) }
        }
        assertEquals("w2", dashboard.panels[0].getSlotById("s1")?.widgets?.get(0)?.id)

        // Remove widget from slot s1
        dashboard = dashboard.updatePanel("p1") { p ->
            p.updateSlotById("s1") { s -> s.removeWidget("w2") }
        }
        assertEquals(1, dashboard.panels[0].getSlotById("s1")?.widgets?.size)
        assertEquals("w1", dashboard.panels[0].getSlotById("s1")?.activeWidget?.id)

        // Remove Panel
        dashboard = dashboard.removePanel("p1")
        assertEquals(0, dashboard.panels.size)
    }

    @Test
    fun testEmptyConfigurationResilience() {
        val emptyDashboard = Dashboard(id = "d0", panels = emptyList())
        assertNull(emptyDashboard.activePanel)
        assertEquals(emptyDashboard, emptyDashboard.nextPanel())
        assertEquals(emptyDashboard, emptyDashboard.previousPanel())

        val emptySlot = WidgetSlot(id = "s0", slotIndex = 0, widgets = emptyList())
        assertNull(emptySlot.activeWidget)
        assertEquals(emptySlot, emptySlot.nextWidget())
        assertEquals(emptySlot, emptySlot.previousWidget())
    }

    @Test
    fun testDefaultDashboardContainsThreePanels() {
        val defaultDashboard = DefaultDashboardFactory.createDefaultDashboard()
        assertEquals(3, defaultDashboard.panels.size)

        // Panel 1: TwoSplitHorizontal
        assertEquals(PanelLayout.TwoSplitHorizontal, defaultDashboard.panels[0].layout)
        assertEquals(2, defaultDashboard.panels[0].slots.size)

        // Panel 2: Single
        assertEquals(PanelLayout.Single, defaultDashboard.panels[1].layout)
        assertEquals(1, defaultDashboard.panels[1].slots.size)

        // Panel 3: Grid4
        assertEquals(PanelLayout.Grid4, defaultDashboard.panels[2].layout)
        assertEquals(4, defaultDashboard.panels[2].slots.size)
    }

    @Test
    fun testWidgetRegistryFallback() {
        val registry = WidgetRegistry()
        BuiltInWidgetDefinitions.allBuiltInProviders.forEach { registry.register(it) }

        // Valid registered widget
        assertNotNull(registry.getDefinition(WidgetTypeIds.DIGITAL_CLOCK))

        // Unknown widget fallback
        val fallbackRenderer = registry.getRenderer("unregistered_widget_type", "unknown_design")
        assertNotNull(fallbackRenderer)
    }
}
