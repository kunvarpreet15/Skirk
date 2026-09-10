package com.kunvarpreet.skirk.widget.mediaplayer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.kunvarpreet.skirk.domain.model.WidgetDesign
import com.kunvarpreet.skirk.domain.model.WidgetInstance
import com.kunvarpreet.skirk.media.domain.MediaSessionRepository
import com.kunvarpreet.skirk.media.model.MediaAccessState
import com.kunvarpreet.skirk.media.model.MediaState
import com.kunvarpreet.skirk.widget.core.WidgetContentRenderer

/**
 * Production renderer for the Media Player widget.
 * Bridges [MediaSessionRepository] state to declarative Jetpack Compose designs.
 */
class MediaPlayerRenderer(
    private val mediaSessionRepository: MediaSessionRepository
) : WidgetContentRenderer {

    @Composable
    override fun Render(
        instance: WidgetInstance,
        design: WidgetDesign,
        modifier: Modifier
    ) {
        val context = LocalContext.current
        val config = MediaPlayerConfig.from(instance.config)
        val mediaState by mediaSessionRepository.observeMediaState()
            .collectAsState(initial = MediaState.Empty)

        if (mediaState.accessState != MediaAccessState.ACTIVE) {
            EmptyMediaView(
                mediaState = mediaState,
                onOpenSettings = {
                    mediaSessionRepository.openNotificationAccessSettings(context)
                },
                modifier = modifier
            )
            return
        }

        when (design.id) {
            "album_art", "full_art" -> {
                AlbumArtMediaPlayerDesign(
                    mediaState = mediaState,
                    config = config,
                    onPlay = { mediaSessionRepository.play() },
                    onPause = { mediaSessionRepository.pause() },
                    onSkipNext = { mediaSessionRepository.skipToNext() },
                    onSkipPrevious = { mediaSessionRepository.skipToPrevious() },
                    onSeekTo = { posMs -> mediaSessionRepository.seekTo(posMs) },
                    modifier = modifier
                )
            }
            "minimal" -> {
                MinimalMediaPlayerDesign(
                    mediaState = mediaState,
                    config = config,
                    onPlay = { mediaSessionRepository.play() },
                    onPause = { mediaSessionRepository.pause() },
                    onSkipNext = { mediaSessionRepository.skipToNext() },
                    onSkipPrevious = { mediaSessionRepository.skipToPrevious() },
                    modifier = modifier
                )
            }
            else -> {
                CompactMediaPlayerDesign(
                    mediaState = mediaState,
                    config = config,
                    onPlay = { mediaSessionRepository.play() },
                    onPause = { mediaSessionRepository.pause() },
                    onSkipNext = { mediaSessionRepository.skipToNext() },
                    onSkipPrevious = { mediaSessionRepository.skipToPrevious() },
                    modifier = modifier
                )
            }
        }
    }
}
