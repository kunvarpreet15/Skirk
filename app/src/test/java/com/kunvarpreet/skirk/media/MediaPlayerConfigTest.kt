package com.kunvarpreet.skirk.media

import com.kunvarpreet.skirk.domain.model.WidgetConfig
import com.kunvarpreet.skirk.widget.mediaplayer.MediaPlayerConfig
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MediaPlayerConfigTest {

    @Test
    fun fromConfig_withDefaults_returnsDefaultValues() {
        val widgetConfig = WidgetConfig()
        val parsed = MediaPlayerConfig.fromConfig(widgetConfig)

        assertTrue(parsed.showArtwork)
        assertTrue(parsed.showProgress)
        assertTrue(parsed.showControls)
    }

    @Test
    fun fromConfig_withCustomProperties_parsesCorrectly() {
        val widgetConfig = WidgetConfig(
            settings = mapOf(
                MediaPlayerConfig.KEY_SHOW_ARTWORK to "false",
                MediaPlayerConfig.KEY_SHOW_PROGRESS to "false",
                MediaPlayerConfig.KEY_SHOW_CONTROLS to "false"
            )
        )
        val parsed = MediaPlayerConfig.fromConfig(widgetConfig)

        assertFalse(parsed.showArtwork)
        assertFalse(parsed.showProgress)
        assertFalse(parsed.showControls)
    }

    @Test
    fun toWidgetConfig_serializesPropertiesCorrectly() {
        val config = MediaPlayerConfig(
            showArtwork = false,
            showProgress = true,
            showControls = false
        )
        val widgetConfig = config.toWidgetConfig()

        assertEquals("false", widgetConfig.settings[MediaPlayerConfig.KEY_SHOW_ARTWORK])
        assertEquals("true", widgetConfig.settings[MediaPlayerConfig.KEY_SHOW_PROGRESS])
        assertEquals("false", widgetConfig.settings[MediaPlayerConfig.KEY_SHOW_CONTROLS])
    }
}
