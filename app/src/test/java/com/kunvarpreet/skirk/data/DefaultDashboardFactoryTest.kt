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
        assertEquals(3, dashboard.panels.size)

        // Panel 1: TwoSplitHorizontal
        val panel1 = dashboard.panels[0]
        assertEquals("Clock & Battery", panel1.name)
        assertEquals(PanelLayout.TwoSplitHorizontal, panel1.layout)
        assertEquals(2, panel1.slots.size)

        // Verify slot stacks
        val slot0 = panel1.slots[0]
        assertTrue("Slot 0 should contain multiple widgets", slot0.widgets.size >= 2)
        assertNotNull(slot0.activeWidget)

        val slot1 = panel1.slots[1]
        assertTrue("Slot 1 should contain multiple widgets", slot1.widgets.size >= 2)
        assertNotNull(slot1.activeWidget)

        // Panel 2: Single layout
        val panel2 = dashboard.panels[1]
        assertEquals("Media & Focus", panel2.name)
        assertEquals(PanelLayout.Single, panel2.layout)
        assertEquals(1, panel2.slots.size)

        // Panel 3: Grid4 layout
        val panel3 = dashboard.panels[2]
        assertEquals("Glance Grid", panel3.name)
        assertEquals(PanelLayout.Grid4, panel3.layout)
        assertEquals(4, panel3.slots.size)
    }
}
