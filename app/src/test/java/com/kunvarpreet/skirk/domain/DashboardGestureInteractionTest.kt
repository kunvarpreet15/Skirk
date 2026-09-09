package com.kunvarpreet.skirk.domain

import com.kunvarpreet.skirk.data.repository.DefaultDashboardFactory
import com.kunvarpreet.skirk.domain.model.Dashboard
import com.kunvarpreet.skirk.domain.model.Panel
import com.kunvarpreet.skirk.domain.model.PanelLayout
import com.kunvarpreet.skirk.domain.model.WidgetInstance
import com.kunvarpreet.skirk.domain.model.WidgetSlot
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class DashboardGestureInteractionTest {

    private val defaultDashboard: Dashboard = DefaultDashboardFactory.createDefaultDashboard()

    @Test
    fun panelNavigation_cyclesForwardAndBackwardAcrossPanels() {
        assertEquals(3, defaultDashboard.panels.size)
        assertEquals(0, defaultDashboard.activePanelIndex)

        // Panel 1 -> Panel 2
        val panel2 = defaultDashboard.selectPanel(1)
        assertEquals(1, panel2.activePanelIndex)
        assertEquals("Media & Focus", panel2.activePanel?.name)

        // Panel 2 -> Panel 3
        val panel3 = panel2.selectPanel(2)
        assertEquals(2, panel3.activePanelIndex)
        assertEquals("Glance Grid", panel3.activePanel?.name)

        // Panel 3 -> Panel 2
        val backToPanel2 = panel3.selectPanel(1)
        assertEquals(1, backToPanel2.activePanelIndex)

        // Panel 2 -> Panel 1
        val backToPanel1 = backToPanel2.selectPanel(0)
        assertEquals(0, backToPanel1.activePanelIndex)
        assertEquals("Clock & Battery", backToPanel1.activePanel?.name)
    }

    @Test
    fun panelNavigation_boundaryChecksAreClampedSafely() {
        // Out-of-bounds indices are safely clamped
        val negativeClamped = defaultDashboard.selectPanel(-1)
        assertEquals(0, negativeClamped.activePanelIndex)

        val overflowClamped = defaultDashboard.selectPanel(99)
        assertEquals(2, overflowClamped.activePanelIndex)
    }

    @Test
    fun slotNavigation_swipingUpAdvancesNextWidgetCyclically() {
        val slot = WidgetSlot(
            id = "slot_test",
            slotIndex = 0,
            widgets = listOf(
                WidgetInstance(widgetTypeId = "clock", selectedDesignId = "digital"),
                WidgetInstance(widgetTypeId = "calendar", selectedDesignId = "month"),
                WidgetInstance(widgetTypeId = "quotes", selectedDesignId = "quote")
            ),
            activeWidgetIndex = 0
        )

        // Swipe Up 1: 0 -> 1 (Clock -> Calendar)
        val step1 = slot.nextWidget()
        assertEquals(1, step1.activeWidgetIndex)
        assertEquals("calendar", step1.activeWidget?.widgetTypeId)

        // Swipe Up 2: 1 -> 2 (Calendar -> Quotes)
        val step2 = step1.nextWidget()
        assertEquals(2, step2.activeWidgetIndex)
        assertEquals("quotes", step2.activeWidget?.widgetTypeId)

        // Swipe Up 3: 2 -> 0 (Quotes -> Clock cyclic)
        val step3 = step2.nextWidget()
        assertEquals(0, step3.activeWidgetIndex)
        assertEquals("clock", step3.activeWidget?.widgetTypeId)
    }

    @Test
    fun slotNavigation_swipingDownAdvancesPreviousWidgetCyclically() {
        val slot = WidgetSlot(
            id = "slot_test",
            slotIndex = 0,
            widgets = listOf(
                WidgetInstance(widgetTypeId = "clock", selectedDesignId = "digital"),
                WidgetInstance(widgetTypeId = "calendar", selectedDesignId = "month"),
                WidgetInstance(widgetTypeId = "quotes", selectedDesignId = "quote")
            ),
            activeWidgetIndex = 0
        )

        // Swipe Down from index 0 -> wraps to index 2 (Clock -> Quotes)
        val step1 = slot.previousWidget()
        assertEquals(2, step1.activeWidgetIndex)
        assertEquals("quotes", step1.activeWidget?.widgetTypeId)

        // Swipe Down from index 2 -> index 1 (Quotes -> Calendar)
        val step2 = step1.previousWidget()
        assertEquals(1, step2.activeWidgetIndex)
        assertEquals("calendar", step2.activeWidget?.widgetTypeId)

        // Swipe Down from index 1 -> index 0 (Calendar -> Clock)
        val step3 = step2.previousWidget()
        assertEquals(0, step3.activeWidgetIndex)
        assertEquals("clock", step3.activeWidget?.widgetTypeId)
    }

    @Test
    fun slotIndependence_modifyingOneSlotDoesNotAffectOtherSlots() {
        val panel = defaultDashboard.panels[0] // TwoSplitHorizontal with 2 slots
        val slot0Initial = panel.getSlot(0)
        val slot1Initial = panel.getSlot(1)

        assertNotNull(slot0Initial)
        assertNotNull(slot1Initial)
        assertEquals(0, slot0Initial!!.activeWidgetIndex)
        assertEquals(0, slot1Initial!!.activeWidgetIndex)

        // Advance slot 0
        val updatedPanel = panel.updateSlot(0) { it.nextWidget() }

        // Verify slot 0 updated, but slot 1 remains unaffected
        assertEquals(1, updatedPanel.getSlot(0)?.activeWidgetIndex)
        assertEquals(0, updatedPanel.getSlot(1)?.activeWidgetIndex)
    }

    @Test
    fun multiPanelIndependence_switchingPanelsPreservesWidgetSelection() {
        // Change Slot 0 on Panel 0
        val panel0Id = defaultDashboard.panels[0].id
        val updatedDashboard = defaultDashboard.updatePanel(panel0Id) { panel ->
            panel.updateSlot(0) { it.nextWidget() }
        }

        // Navigate to Panel 1
        val onPanel1 = updatedDashboard.selectPanel(1)
        assertEquals(1, onPanel1.activePanelIndex)

        // Navigate back to Panel 0
        val backOnPanel0 = onPanel1.selectPanel(0)
        assertEquals(0, backOnPanel0.activePanelIndex)

        // Verify Slot 0 on Panel 0 retained its activeWidgetIndex = 1
        val slot0 = backOnPanel0.activePanel?.getSlot(0)
        assertEquals(1, slot0?.activeWidgetIndex)
    }

    @Test
    fun edgeCases_singleWidgetSlotDoesNotChangeIndex() {
        val singleSlot = WidgetSlot(
            id = "single_slot",
            slotIndex = 0,
            widgets = listOf(WidgetInstance(widgetTypeId = "single", selectedDesignId = "default")),
            activeWidgetIndex = 0
        )

        assertEquals(0, singleSlot.nextWidget().activeWidgetIndex)
        assertEquals(0, singleSlot.previousWidget().activeWidgetIndex)
    }

    @Test
    fun edgeCases_emptySlotDoesNotCrash() {
        val emptySlot = WidgetSlot(
            id = "empty_slot",
            slotIndex = 0,
            widgets = emptyList(),
            activeWidgetIndex = 0
        )

        assertEquals(0, emptySlot.nextWidget().activeWidgetIndex)
        assertEquals(0, emptySlot.previousWidget().activeWidgetIndex)
    }

    @Test
    fun edgeCases_singlePanelDashboardDoesNotCrashOnNavigation() {
        val singlePanelDashboard = Dashboard(
            id = "single_dash",
            panels = listOf(
                Panel(
                    id = "only_panel",
                    name = "Only Panel",
                    layout = PanelLayout.Single,
                    slots = emptyList()
                )
            ),
            activePanelIndex = 0
        )

        val navNext = singlePanelDashboard.selectPanel(1)
        assertEquals(0, navNext.activePanelIndex)

        val navPrev = singlePanelDashboard.selectPanel(-1)
        assertEquals(0, navPrev.activePanelIndex)
    }
}
