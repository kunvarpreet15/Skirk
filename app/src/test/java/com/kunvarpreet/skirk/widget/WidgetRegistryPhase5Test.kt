package com.kunvarpreet.skirk.widget

import com.kunvarpreet.skirk.domain.model.WidgetCategory
import com.kunvarpreet.skirk.media.domain.MockMediaSessionRepository
import com.kunvarpreet.skirk.media.model.MediaState
import com.kunvarpreet.skirk.widget.core.WidgetRegistry
import com.kunvarpreet.skirk.widget.definitions.BuiltInWidgetDefinitions
import com.kunvarpreet.skirk.widget.mediaplayer.MediaPlayerProvider
import com.kunvarpreet.skirk.widget.mediaplayer.MediaPlayerRenderer
import com.kunvarpreet.skirk.widget.model.WidgetTypeIds
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class WidgetRegistryPhase5Test {

    private lateinit var registry: WidgetRegistry

    @Before
    fun setup() {
        registry = WidgetRegistry()
        BuiltInWidgetDefinitions.allBuiltInProviders.forEach { registry.register(it) }
    }

    @Test
    fun mediaPlayer_registeredWithProductionProviderAndDesigns() {
        val def = registry.getDefinition(WidgetTypeIds.MEDIA_PLAYER)
        assertNotNull(def)
        assertEquals("Media Player", def?.displayName)
        assertEquals(WidgetCategory.MEDIA, def?.category)

        val designIds = def!!.availableDesigns.map { it.id }
        assertTrue(designIds.contains("compact"))
        assertTrue(designIds.contains("album_art"))
        assertTrue(designIds.contains("minimal"))

        val renderer = registry.getRenderer(WidgetTypeIds.MEDIA_PLAYER, "compact")
        assertTrue(renderer is MediaPlayerRenderer)
    }

    @Test
    fun createBuiltInProviders_withCustomMediaSessionRepository_registersCorrectly() {
        val customRepo = MockMediaSessionRepository(MediaState.Sample)
        val customRegistry = WidgetRegistry()

        BuiltInWidgetDefinitions.createBuiltInProviders(
            mediaSessionRepository = customRepo
        ).forEach {
            customRegistry.register(it)
        }

        val renderer = customRegistry.getRenderer(WidgetTypeIds.MEDIA_PLAYER, "album_art")
        assertTrue(renderer is MediaPlayerRenderer)
    }
}
