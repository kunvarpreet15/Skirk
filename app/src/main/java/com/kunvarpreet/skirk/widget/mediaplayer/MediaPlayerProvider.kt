package com.kunvarpreet.skirk.widget.mediaplayer

import com.kunvarpreet.skirk.domain.model.WidgetCategory
import com.kunvarpreet.skirk.domain.model.WidgetDefinition
import com.kunvarpreet.skirk.domain.model.WidgetDesign
import com.kunvarpreet.skirk.media.domain.MediaSessionRepository
import com.kunvarpreet.skirk.media.domain.MockMediaSessionRepository
import com.kunvarpreet.skirk.widget.core.WidgetContentRenderer
import com.kunvarpreet.skirk.widget.core.WidgetProvider
import com.kunvarpreet.skirk.widget.model.WidgetTypeIds

/**
 * Production provider for the built-in Media Player widget.
 */
class MediaPlayerProvider(
    private val mediaSessionRepository: MediaSessionRepository = MockMediaSessionRepository()
) : WidgetProvider {

    private val renderer = MediaPlayerRenderer(mediaSessionRepository)

    override val definition: WidgetDefinition = WidgetDefinition(
        id = WidgetTypeIds.MEDIA_PLAYER,
        displayName = "Media Player",
        description = "Now playing metadata, album art, and playback controls",
        category = WidgetCategory.MEDIA,
        availableDesigns = listOf(
            WidgetDesign(id = "compact", displayName = "Compact"),
            WidgetDesign(id = "album_art", displayName = "Full Album Art"),
            WidgetDesign(id = "minimal", displayName = "Minimal")
        ),
        defaultDesignId = "compact"
    )

    override fun getRenderer(designId: String): WidgetContentRenderer = renderer
}
