package com.kunvarpreet.skirk.media.domain

import android.content.Context
import com.kunvarpreet.skirk.media.model.MediaState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Repository interface providing decoupled access to the Android media session subsystem.
 */
interface MediaSessionRepository {
    fun observeMediaState(): Flow<MediaState>
    fun play()
    fun pause()
    fun skipToNext()
    fun skipToPrevious()
    fun seekTo(positionMs: Long)
    fun isNotificationAccessGranted(): Boolean
    fun openNotificationAccessSettings(context: Context)
}

/**
 * Mock implementation for testing and sample previews without system service bindings.
 */
class MockMediaSessionRepository(
    initialState: MediaState = MediaState.Sample
) : MediaSessionRepository {

    private val _state = MutableStateFlow(initialState)

    override fun observeMediaState(): Flow<MediaState> = _state.asStateFlow()

    override fun play() {
        val current = _state.value
        _state.value = current.copy(isPlaying = true)
    }

    override fun pause() {
        val current = _state.value
        _state.value = current.copy(isPlaying = false)
    }

    override fun skipToNext() {
        val current = _state.value
        _state.value = current.copy(
            trackTitle = "Save Your Tears",
            positionMs = 0L
        )
    }

    override fun skipToPrevious() {
        val current = _state.value
        _state.value = current.copy(positionMs = 0L)
    }

    override fun seekTo(positionMs: Long) {
        val current = _state.value
        _state.value = current.copy(positionMs = positionMs)
    }

    override fun isNotificationAccessGranted(): Boolean = true

    override fun openNotificationAccessSettings(context: Context) {
        // No-op in mock
    }
}
