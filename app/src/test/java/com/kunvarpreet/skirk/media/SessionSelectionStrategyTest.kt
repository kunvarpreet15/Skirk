package com.kunvarpreet.skirk.media

import android.media.session.PlaybackState
import com.kunvarpreet.skirk.media.data.SessionCandidate
import com.kunvarpreet.skirk.media.data.SessionSelectionStrategy
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SessionSelectionStrategyTest {

    @Test
    fun selectBestCandidate_emptyList_returnsNull() {
        val result = SessionSelectionStrategy.selectBestCandidate(emptyList())
        assertNull(result)
    }

    @Test
    fun selectBestCandidate_prefersPlayingOverPaused() {
        val paused = SessionCandidate(
            id = "paused_app",
            playbackState = PlaybackState.STATE_PAUSED,
            lastPositionUpdateTime = 5000L,
            hasTitleOrArtist = true
        )
        val playing = SessionCandidate(
            id = "playing_app",
            playbackState = PlaybackState.STATE_PLAYING,
            lastPositionUpdateTime = 1000L,
            hasTitleOrArtist = true
        )

        val best = SessionSelectionStrategy.selectBestCandidate(listOf(paused, playing))
        assertEquals("playing_app", best?.id)
    }

    @Test
    fun selectBestCandidate_prefersBufferingOverPaused() {
        val paused = SessionCandidate(
            id = "paused_app",
            playbackState = PlaybackState.STATE_PAUSED,
            lastPositionUpdateTime = 5000L
        )
        val buffering = SessionCandidate(
            id = "buffering_app",
            playbackState = PlaybackState.STATE_BUFFERING,
            lastPositionUpdateTime = 1000L
        )

        val best = SessionSelectionStrategy.selectBestCandidate(listOf(paused, buffering))
        assertEquals("buffering_app", best?.id)
    }

    @Test
    fun selectBestCandidate_prefersMoreRecentPausedSession() {
        val olderPaused = SessionCandidate(
            id = "older_paused",
            playbackState = PlaybackState.STATE_PAUSED,
            lastPositionUpdateTime = 1000L
        )
        val newerPaused = SessionCandidate(
            id = "newer_paused",
            playbackState = PlaybackState.STATE_PAUSED,
            lastPositionUpdateTime = 9000L
        )

        val best = SessionSelectionStrategy.selectBestCandidate(listOf(olderPaused, newerPaused))
        assertEquals("newer_paused", best?.id)
    }

    @Test
    fun selectBestCandidate_prefersCandidateWithMetadataWhenTied() {
        val withoutMeta = SessionCandidate(
            id = "no_meta",
            playbackState = PlaybackState.STATE_PLAYING,
            hasTitleOrArtist = false
        )
        val withMeta = SessionCandidate(
            id = "with_meta",
            playbackState = PlaybackState.STATE_PLAYING,
            hasTitleOrArtist = true
        )

        val best = SessionSelectionStrategy.selectBestCandidate(listOf(withoutMeta, withMeta))
        assertEquals("with_meta", best?.id)
    }
}
