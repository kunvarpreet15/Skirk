package com.kunvarpreet.skirk.domain

import com.kunvarpreet.skirk.domain.model.Dashboard
import com.kunvarpreet.skirk.domain.model.Panel
import com.kunvarpreet.skirk.domain.model.PanelLayout
import com.kunvarpreet.skirk.domain.model.WidgetInstance
import com.kunvarpreet.skirk.domain.model.WidgetSlot
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class DashboardModelTest {

    @Test
    fun widgetSlot_nextAndPreviousNavigationCyclesCorrectly() {
        val widgetA = WidgetInstance(id = "a", widgetTypeId = "type_clock", selectedDesignId = "minimal")
        val widgetB = WidgetInstance(id = "b", widgetTypeId = "type_calendar", selectedDesignId = "grid")
        val widgetC = WidgetInstance(id = "c", widgetTypeId = "type_quote", selectedDesignId = "serif")

        var slot = WidgetSlot(
            slotIndex = 0,
            widgets = listOf(widgetA, widgetB, widgetC),
            activeWidgetIndex = 0
        )

        assertEquals(widgetA, slot.activeWidget)

        // Cycle forward
        slot = slot.nextWidget()
        assertEquals(1, slot.activeWidgetIndex)
        assertEquals(widgetB, slot.activeWidget)

        slot = slot.nextWidget()
        assertEquals(2, slot.activeWidgetIndex)
        assertEquals(widgetC, slot.activeWidget)

        // Wrap around to start
        slot = slot.nextWidget()
        assertEquals(0, slot.activeWidgetIndex)
        assertEquals(widgetA, slot.activeWidget)

        // Cycle backward (wrap around to end)
        slot = slot.previousWidget()
        assertEquals(2, slot.activeWidgetIndex)
        assertEquals(widgetC, slot.activeWidget)
    }

    @Test
    fun widgetSlot_emptySlotReturnsNullActiveWidget() {
        val emptySlot = WidgetSlot(slotIndex = 0, widgets = emptyList())
        assertNull(emptySlot.activeWidget)
    }

    @Test
    fun dashboard_panelNavigationWrapsAround() {
        val panel1 = Panel(id = "p1", name = "Panel 1", layout = PanelLayout.Single)
        val panel2 = Panel(id = "p2", name = "Panel 2", layout = PanelLayout.TwoSplitHorizontal)

        var dashboard = Dashboard(
            id = "d1",
            panels = listOf(panel1, panel2),
            activePanelIndex = 0
        )

        assertEquals("Panel 1", dashboard.activePanel?.name)

        dashboard = dashboard.nextPanel()
        assertEquals(1, dashboard.activePanelIndex)
        assertEquals("Panel 2", dashboard.activePanel?.name)

        dashboard = dashboard.nextPanel()
        assertEquals(0, dashboard.activePanelIndex)
        assertEquals("Panel 1", dashboard.activePanel?.name)

        dashboard = dashboard.previousPanel()
        assertEquals(1, dashboard.activePanelIndex)
        assertEquals("Panel 2", dashboard.activePanel?.name)
    }

    @Test
    fun panelLayout_standardLayoutsHaveAccurateSlotCounts() {
        assertEquals(1, PanelLayout.Single.slotCount)
        assertEquals(2, PanelLayout.TwoSplitHorizontal.slotCount)
        assertEquals(2, PanelLayout.TwoSplitVertical.slotCount)
        assertEquals(4, PanelLayout.Grid4.slotCount)
        assertEquals(6, PanelLayout.Grid6.slotCount)
    }
}
