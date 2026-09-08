package com.kunvarpreet.skirk.widget

import com.kunvarpreet.skirk.domain.model.WidgetCategory
import com.kunvarpreet.skirk.domain.model.WidgetDefinition
import com.kunvarpreet.skirk.domain.model.WidgetDesign
import com.kunvarpreet.skirk.widget.core.WidgetContentRenderer
import com.kunvarpreet.skirk.widget.core.WidgetProvider
import com.kunvarpreet.skirk.widget.core.WidgetRegistry
import com.kunvarpreet.skirk.widget.definitions.BuiltInWidgetDefinitions
import com.kunvarpreet.skirk.widget.model.WidgetTypeIds
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class WidgetRegistryTest {

    private lateinit var registry: WidgetRegistry

    @Before
    fun setup() {
        registry = WidgetRegistry()
    }

    @Test
    fun register_andRetrieveBuiltInDefinitions() {
        BuiltInWidgetDefinitions.allBuiltInProviders.forEach { registry.register(it) }

        val definitions = registry.getAllDefinitions()
        assertEquals(14, definitions.size)

        val clockDef = registry.getDefinition(WidgetTypeIds.DIGITAL_CLOCK)
        assertNotNull(clockDef)
        assertEquals("Digital Clock", clockDef?.displayName)
        assertEquals(WidgetCategory.TIME, clockDef?.category)
        assertTrue(clockDef!!.availableDesigns.size >= 4)
    }

    @Test
    fun findDesign_returnsFallbackWhenNotFound() {
        val def = WidgetDefinition(
            id = "test_widget",
            displayName = "Test",
            description = "Desc",
            category = WidgetCategory.SYSTEM,
            availableDesigns = listOf(
                WidgetDesign("primary", "Primary Design")
            ),
            defaultDesignId = "primary"
        )

        assertEquals("Primary Design", def.findDesign("non_existent").displayName)
    }

    @Test
    fun getRenderer_returnsFallbackPlaceholderForUnknownWidget() {
        val renderer = registry.getRenderer("unknown_type", "default")
        assertNotNull(renderer)
    }
}
