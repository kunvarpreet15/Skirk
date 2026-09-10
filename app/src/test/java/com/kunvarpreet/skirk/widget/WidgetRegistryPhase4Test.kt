package com.kunvarpreet.skirk.widget

import com.kunvarpreet.skirk.domain.model.WidgetCategory
import com.kunvarpreet.skirk.widget.analogclock.AnalogClockProvider
import com.kunvarpreet.skirk.widget.analogclock.AnalogClockRenderer
import com.kunvarpreet.skirk.widget.battery.BatteryInfo
import com.kunvarpreet.skirk.widget.battery.BatteryWidgetProvider
import com.kunvarpreet.skirk.widget.battery.BatteryWidgetRenderer
import com.kunvarpreet.skirk.widget.battery.SampleBatteryInfoProvider
import com.kunvarpreet.skirk.widget.core.WidgetRegistry
import com.kunvarpreet.skirk.widget.definitions.BuiltInWidgetDefinitions
import com.kunvarpreet.skirk.widget.digitalclock.DigitalClockProvider
import com.kunvarpreet.skirk.widget.digitalclock.DigitalClockRenderer
import com.kunvarpreet.skirk.widget.model.WidgetTypeIds
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class WidgetRegistryPhase4Test {

    private lateinit var registry: WidgetRegistry

    @Before
    fun setup() {
        registry = WidgetRegistry()
        BuiltInWidgetDefinitions.allBuiltInProviders.forEach { registry.register(it) }
    }

    @Test
    fun digitalClock_registeredWithProductionProviderAndDesigns() {
        val def = registry.getDefinition(WidgetTypeIds.DIGITAL_CLOCK)
        assertNotNull(def)
        assertEquals("Digital Clock", def?.displayName)
        assertEquals(WidgetCategory.TIME, def?.category)

        val designIds = def!!.availableDesigns.map { it.id }
        assertTrue(designIds.contains("large"))
        assertTrue(designIds.contains("minimal"))
        assertTrue(designIds.contains("retro"))
        assertTrue(designIds.contains("modern"))

        val renderer = registry.getRenderer(WidgetTypeIds.DIGITAL_CLOCK, "large")
        assertTrue(renderer is DigitalClockRenderer)
    }

    @Test
    fun analogClock_registeredWithProductionProviderAndDesigns() {
        val def = registry.getDefinition(WidgetTypeIds.ANALOG_CLOCK)
        assertNotNull(def)
        assertEquals("Analog Clock", def?.displayName)
        assertEquals(WidgetCategory.TIME, def?.category)

        val designIds = def!!.availableDesigns.map { it.id }
        assertTrue(designIds.contains("classic"))
        assertTrue(designIds.contains("bauhaus"))
        assertTrue(designIds.contains("chronograph"))

        val renderer = registry.getRenderer(WidgetTypeIds.ANALOG_CLOCK, "classic")
        assertTrue(renderer is AnalogClockRenderer)
    }

    @Test
    fun battery_registeredWithProductionProviderAndDesigns() {
        val def = registry.getDefinition(WidgetTypeIds.BATTERY)
        assertNotNull(def)
        assertEquals("Battery Info", def?.displayName)
        assertEquals(WidgetCategory.SYSTEM, def?.category)

        val designIds = def!!.availableDesigns.map { it.id }
        assertTrue(designIds.contains("ring"))
        assertTrue(designIds.contains("bar"))
        assertTrue(designIds.contains("minimal"))

        val renderer = registry.getRenderer(WidgetTypeIds.BATTERY, "ring")
        assertTrue(renderer is BatteryWidgetRenderer)
    }

    @Test
    fun createBuiltInProviders_withCustomBatteryInfoProvider_registersCorrectly() {
        val customInfo = BatteryInfo(percentage = 42, isCharging = false)
        val customProvider = SampleBatteryInfoProvider(customInfo)
        val customRegistry = WidgetRegistry()

        BuiltInWidgetDefinitions.createBuiltInProviders(customProvider).forEach {
            customRegistry.register(it)
        }

        val renderer = customRegistry.getRenderer(WidgetTypeIds.BATTERY, "bar")
        assertTrue(renderer is BatteryWidgetRenderer)
    }
}
