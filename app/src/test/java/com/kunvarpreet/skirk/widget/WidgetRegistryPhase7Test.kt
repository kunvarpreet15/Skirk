package com.kunvarpreet.skirk.widget

import com.kunvarpreet.skirk.domain.model.WidgetCategory
import com.kunvarpreet.skirk.notification.domain.MockNotificationRepository
import com.kunvarpreet.skirk.system.domain.MockSystemDashboardRepository
import com.kunvarpreet.skirk.widget.core.WidgetRegistry
import com.kunvarpreet.skirk.widget.definitions.BuiltInWidgetDefinitions
import com.kunvarpreet.skirk.widget.model.WidgetTypeIds
import com.kunvarpreet.skirk.widget.notification.NotificationWidgetConfig
import com.kunvarpreet.skirk.widget.notification.NotificationWidgetRenderer
import com.kunvarpreet.skirk.widget.system.SystemDashboardConfig
import com.kunvarpreet.skirk.widget.system.SystemDashboardWidgetRenderer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class WidgetRegistryPhase7Test {

    private lateinit var registry: WidgetRegistry

    @Before
    fun setup() {
        registry = WidgetRegistry()
        BuiltInWidgetDefinitions.allBuiltInProviders.forEach { registry.register(it) }
    }

    @Test
    fun notifications_registeredWithProductionProviderAndDesigns() {
        val def = registry.getDefinition(WidgetTypeIds.NOTIFICATIONS)
        assertNotNull(def)
        assertEquals("Notifications", def?.displayName)
        assertEquals(WidgetCategory.SYSTEM, def?.category)

        val designIds = def!!.availableDesigns.map { it.id }
        assertTrue(designIds.contains(NotificationWidgetConfig.DESIGN_LIST))
        assertTrue(designIds.contains(NotificationWidgetConfig.DESIGN_COMPACT))
        assertTrue(designIds.contains(NotificationWidgetConfig.DESIGN_FOCUS))

        val renderer = registry.getRenderer(WidgetTypeIds.NOTIFICATIONS, NotificationWidgetConfig.DESIGN_LIST)
        assertTrue(renderer is NotificationWidgetRenderer)
    }

    @Test
    fun systemDashboard_registeredWithProductionProviderAndDesigns() {
        val def = registry.getDefinition(WidgetTypeIds.SYSTEM_DASHBOARD)
        assertNotNull(def)
        assertEquals("System Dashboard", def?.displayName)
        assertEquals(WidgetCategory.SYSTEM, def?.category)

        val designIds = def!!.availableDesigns.map { it.id }
        assertTrue(designIds.contains(SystemDashboardConfig.DESIGN_GRID))
        assertTrue(designIds.contains(SystemDashboardConfig.DESIGN_RINGS))
        assertTrue(designIds.contains(SystemDashboardConfig.DESIGN_MINIMAL))

        val renderer = registry.getRenderer(WidgetTypeIds.SYSTEM_DASHBOARD, SystemDashboardConfig.DESIGN_GRID)
        assertTrue(renderer is SystemDashboardWidgetRenderer)
    }

    @Test
    fun createBuiltInProviders_withCustomPhase7Repositories_registersCorrectly() {
        val customNotifRepo = MockNotificationRepository()
        val customSysRepo = MockSystemDashboardRepository()
        val customRegistry = WidgetRegistry()

        BuiltInWidgetDefinitions.createBuiltInProviders(
            notificationRepository = customNotifRepo,
            systemDashboardRepository = customSysRepo
        ).forEach {
            customRegistry.register(it)
        }

        val notifRenderer = customRegistry.getRenderer(WidgetTypeIds.NOTIFICATIONS, NotificationWidgetConfig.DESIGN_COMPACT)
        assertTrue(notifRenderer is NotificationWidgetRenderer)

        val sysRenderer = customRegistry.getRenderer(WidgetTypeIds.SYSTEM_DASHBOARD, SystemDashboardConfig.DESIGN_RINGS)
        assertTrue(sysRenderer is SystemDashboardWidgetRenderer)
    }
}
