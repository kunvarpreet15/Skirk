package com.kunvarpreet.skirk.media

import com.kunvarpreet.skirk.media.model.MediaAccessState
import com.kunvarpreet.skirk.media.model.MediaState
import com.kunvarpreet.skirk.media.model.PlaybackStatus
import com.kunvarpreet.skirk.widget.mediaplayer.MediaFormatUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MediaStateTest {

    @Test
    fun defaultMediaState_isInactive() {
        val state = MediaState()
        assertEquals(MediaAccessState.NO_MEDIA, state.accessState)
        assertFalse(state.isPlaying)
        assertEquals(PlaybackStatus.NONE, state.playbackStatus)
        assertEquals(0L, state.positionMs)
        assertEquals(0L, state.durationMs)
        assertEquals(0f, state.progressFraction, 0.001f)
    }

    @Test
    fun sampleMediaState_hasExpectedData() {
        val sample = MediaState.Sample
        assertEquals(MediaAccessState.ACTIVE, sample.accessState)
        assertEquals("Blinding Lights", sample.trackTitle)
        assertEquals("The Weeknd", sample.artistName)
        assertEquals("After Hours", sample.albumName)
        assertTrue(sample.isPlaying)
        assertEquals(102_000L, sample.positionMs)
        assertEquals(200_000L, sample.durationMs)
        assertEquals(0.51f, sample.progressFraction, 0.01f)
    }

    @Test
    fun currentInterpolatedPositionMs_advancesWhenPlaying() {
        val baseTime = 1_000_000L
        val state = MediaState(
            isPlaying = true,
            positionMs = 10_000L,
            durationMs = 60_000L,
            playbackSpeed = 1.0f,
            lastUpdateTime = baseTime
        )

        val advanced = state.currentInterpolatedPositionMs(currentTimeMs = baseTime + 5_000L)
        assertEquals(15_000L, advanced)
    }

    @Test
    fun currentInterpolatedPositionMs_doesNotAdvanceWhenPaused() {
        val baseTime = 1_000_000L
        val state = MediaState(
            isPlaying = false,
            positionMs = 10_000L,
            durationMs = 60_000L,
            playbackSpeed = 1.0f,
            lastUpdateTime = baseTime
        )

        val result = state.currentInterpolatedPositionMs(currentTimeMs = baseTime + 5_000L)
        assertEquals(10_000L, result)
    }

    @Test
    fun currentInterpolatedPositionMs_clampedToDuration() {
        val baseTime = 1_000_000L
        val state = MediaState(
            isPlaying = true,
            positionMs = 50_000L,
            durationMs = 60_000L,
            playbackSpeed = 1.0f,
            lastUpdateTime = baseTime
        )

        val result = state.currentInterpolatedPositionMs(currentTimeMs = baseTime + 20_000L)
        assertEquals(60_000L, result)
    }

    @Test
    fun formatDuration_formatsCorrectly() {
        assertEquals("00:00", MediaFormatUtils.formatDuration(0L))
        assertEquals("00:42", MediaFormatUtils.formatDuration(42_000L))
        assertEquals("03:20", MediaFormatUtils.formatDuration(200_000L))
        assertEquals("1:15:30", MediaFormatUtils.formatDuration(4_530_000L))
    }
}
