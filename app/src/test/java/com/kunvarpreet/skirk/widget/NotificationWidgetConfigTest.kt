package com.kunvarpreet.skirk.widget

import com.kunvarpreet.skirk.domain.model.WidgetConfig
import com.kunvarpreet.skirk.widget.notification.NotificationWidgetConfig
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NotificationWidgetConfigTest {

    @Test
    fun defaults_areCorrect() {
        val config = NotificationWidgetConfig()
        assertEquals(NotificationWidgetConfig.DESIGN_LIST, config.selectedDesign)
        assertEquals(5, config.maxNotifications)
        assertTrue(config.showAppNames)
        assertTrue(config.showTimestamps)
        assertFalse(config.includeOngoing)
    }

    @Test
    fun serialization_roundTripPreservesValues() {
        val original = NotificationWidgetConfig(
            selectedDesign = NotificationWidgetConfig.DESIGN_FOCUS,
            maxNotifications = 10,
            showAppNames = false,
            showTimestamps = true,
            includeOngoing = true
        )

        val widgetConfig = original.toWidgetConfig()
        val parsed = NotificationWidgetConfig.fromWidgetConfig(widgetConfig)

        assertEquals(original.selectedDesign, parsed.selectedDesign)
        assertEquals(original.maxNotifications, parsed.maxNotifications)
        assertEquals(original.showAppNames, parsed.showAppNames)
        assertEquals(original.showTimestamps, parsed.showTimestamps)
        assertEquals(original.includeOngoing, parsed.includeOngoing)
    }

    @Test
    fun fromWidgetConfig_clampsMaxNotifications() {
        val configOverMax = WidgetConfig(
            settings = mapOf(NotificationWidgetConfig.KEY_MAX_NOTIFICATIONS to "999")
        )
        assertEquals(20, NotificationWidgetConfig.fromWidgetConfig(configOverMax).maxNotifications)

        val configUnderMin = WidgetConfig(
            settings = mapOf(NotificationWidgetConfig.KEY_MAX_NOTIFICATIONS to "0")
        )
        assertEquals(1, NotificationWidgetConfig.fromWidgetConfig(configUnderMin).maxNotifications)
    }

    @Test
    fun fromWidgetConfig_nullYieldsDefaults() {
        val config = NotificationWidgetConfig.fromWidgetConfig(null)
        assertEquals(NotificationWidgetConfig.DESIGN_LIST, config.selectedDesign)
        assertEquals(5, config.maxNotifications)
        assertTrue(config.showAppNames)
        assertTrue(config.showTimestamps)
        assertFalse(config.includeOngoing)
    }
}
