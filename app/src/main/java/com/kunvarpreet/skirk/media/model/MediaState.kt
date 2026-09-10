package com.kunvarpreet.skirk.media.model

import android.graphics.Bitmap

/**
 * Access state reflecting whether the system notification listener permission
 * is granted and whether an active media session exists.
 */
enum class MediaAccessState {
    ACTIVE,
    NO_MEDIA,
    PERMISSION_REQUIRED
}

/**
 * Normalized playback status across Android media sessions.
 */
enum class PlaybackStatus {
    PLAYING,
    PAUSED,
    BUFFERING,
    STOPPED,
    NONE
}

/**
 * Actions supported by the currently active media session.
 */
data class MediaPlaybackActions(
    val canPlay: Boolean = false,
    val canPause: Boolean = false,
    val canSkipNext: Boolean = false,
    val canSkipPrevious: Boolean = false,
    val canSeek: Boolean = false
)

/**
 * Metadata about the client app owning the media session.
 */
data class MediaAppInfo(
    val packageName: String,
    val appName: String
)

/**
 * State representation of the active media playback session.
 */
data class MediaState(
    val accessState: MediaAccessState = MediaAccessState.NO_MEDIA,
    val trackTitle: String? = null,
    val artistName: String? = null,
    val albumName: String? = null,
    val artwork: Bitmap? = null,
    val isPlaying: Boolean = false,
    val playbackStatus: PlaybackStatus = PlaybackStatus.NONE,
    val positionMs: Long = 0L,
    val durationMs: Long = 0L,
    val playbackSpeed: Float = 1.0f,
    val lastUpdateTime: Long = 0L,
    val actions: MediaPlaybackActions = MediaPlaybackActions(),
    val appInfo: MediaAppInfo? = null
) {
    /**
     * Estimates current playback position based on elapsed time and speed when playing.
     */
    fun currentInterpolatedPositionMs(currentTimeMs: Long = System.currentTimeMillis()): Long {
        if (!isPlaying || playbackSpeed <= 0f || lastUpdateTime <= 0L) {
            return positionMs.coerceAtLeast(0L)
        }
        val elapsed = currentTimeMs - lastUpdateTime
        val estimated = positionMs + (elapsed * playbackSpeed).toLong()
        return if (durationMs > 0L) {
            estimated.coerceIn(0L, durationMs)
        } else {
            estimated.coerceAtLeast(0L)
        }
    }

    val progressFraction: Float
        get() {
            if (durationMs <= 0L) return 0f
            return (positionMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f)
        }

    companion object {
        val Sample = MediaState(
            accessState = MediaAccessState.ACTIVE,
            trackTitle = "Blinding Lights",
            artistName = "The Weeknd",
            albumName = "After Hours",
            artwork = null,
            isPlaying = true,
            playbackStatus = PlaybackStatus.PLAYING,
            positionMs = 102_000L, // 01:42
            durationMs = 200_000L, // 03:20
            playbackSpeed = 1.0f,
            lastUpdateTime = System.currentTimeMillis(),
            actions = MediaPlaybackActions(
                canPlay = true,
                canPause = true,
                canSkipNext = true,
                canSkipPrevious = true,
                canSeek = true
            ),
            appInfo = MediaAppInfo(
                packageName = "com.spotify.music",
                appName = "Spotify"
            )
        )

        val Empty = MediaState(
            accessState = MediaAccessState.NO_MEDIA
        )

        val PermissionRequired = MediaState(
            accessState = MediaAccessState.PERMISSION_REQUIRED
        )
    }
}
