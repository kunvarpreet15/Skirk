package com.kunvarpreet.skirk.widget.mediaplayer

import com.kunvarpreet.skirk.domain.model.WidgetConfig

/**
 * Parsed configuration for the Media Player widget instances.
 */
data class MediaPlayerConfig(
    val showArtwork: Boolean = true,
    val showProgress: Boolean = true,
    val showControls: Boolean = true
) {
    companion object {
        const val KEY_SHOW_ARTWORK = "show_artwork"
        const val KEY_SHOW_PROGRESS = "show_progress"
        const val KEY_SHOW_CONTROLS = "show_controls"

        fun from(config: WidgetConfig): MediaPlayerConfig {
            return MediaPlayerConfig(
                showArtwork = config.getBoolean(KEY_SHOW_ARTWORK, defaultValue = true),
                showProgress = config.getBoolean(KEY_SHOW_PROGRESS, defaultValue = true),
                showControls = config.getBoolean(KEY_SHOW_CONTROLS, defaultValue = true)
            )
        }

        fun fromConfig(config: WidgetConfig): MediaPlayerConfig = from(config)
    }

    fun toWidgetConfig(): WidgetConfig {
        return WidgetConfig(
            settings = mapOf(
                KEY_SHOW_ARTWORK to showArtwork.toString(),
                KEY_SHOW_PROGRESS to showProgress.toString(),
                KEY_SHOW_CONTROLS to showControls.toString()
            )
        )
    }
}
