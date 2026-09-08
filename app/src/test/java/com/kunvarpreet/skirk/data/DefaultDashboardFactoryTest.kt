package com.kunvarpreet.skirk.data

import com.kunvarpreet.skirk.data.repository.DefaultDashboardFactory
import com.kunvarpreet.skirk.domain.model.PanelLayout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DefaultDashboardFactoryTest {

    @Test
    fun createDefaultDashboard_buildsValidDashboardHierarchy() {
        val dashboard = DefaultDashboardFactory.createDefaultDashboard()

        assertNotNull(dashboard)
        assertEquals("Primary StandBy", dashboard.name)
        assertEquals(2, dashboard.panels.size)

        // Panel 1: TwoSplitHorizontal
        val mainPanel = dashboard.panels[0]
        assertEquals("StandBy Glance", mainPanel.name)
        assertEquals(PanelLayout.TwoSplitHorizontal, mainPanel.layout)
        assertEquals(2, mainPanel.slots.size)

        // Verify slot stacks
        val slot0 = mainPanel.slots[0]
        assertTrue("Slot 0 should contain multiple widgets", slot0.widgets.size >= 2)
        assertNotNull(slot0.activeWidget)

        val slot1 = mainPanel.slots[1]
        assertTrue("Slot 1 should contain multiple widgets", slot1.widgets.size >= 2)
        assertNotNull(slot1.activeWidget)

        // Panel 2: Single layout
        val focusPanel = dashboard.panels[1]
        assertEquals("Focus & Media", focusPanel.name)
        assertEquals(PanelLayout.Single, focusPanel.layout)
        assertEquals(1, focusPanel.slots.size)
    }
}
