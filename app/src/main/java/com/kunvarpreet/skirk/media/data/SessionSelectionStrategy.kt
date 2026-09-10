package com.kunvarpreet.skirk.media.data

import android.media.MediaMetadata
import android.media.session.MediaController
import android.media.session.PlaybackState

/**
 * Lightweight representation of a media session candidate for deterministic selection.
 */
data class SessionCandidate(
    val id: String,
    val playbackState: Int = PlaybackState.STATE_NONE,
    val lastPositionUpdateTime: Long = 0L,
    val hasTitleOrArtist: Boolean = false
)

/**
 * Strategy prioritizing active playback and recent activity when multiple
 * media sessions are active simultaneously.
 */
object SessionSelectionStrategy {

    /**
     * Scores a candidate session:
     * - Playing: 100_000
     * - Buffering/FastForward/Rewinding: 50_000
     * - Paused: 10_000 + recency bonus
     * - Metadata presence bonus: +100
     */
    fun scoreCandidate(candidate: SessionCandidate): Long {
        var score = 0L

        when (candidate.playbackState) {
            PlaybackState.STATE_PLAYING -> score += 100_000_000L
            PlaybackState.STATE_BUFFERING,
            PlaybackState.STATE_FAST_FORWARDING,
            PlaybackState.STATE_REWINDING -> score += 50_000_000L
            PlaybackState.STATE_PAUSED -> {
                score += 10_000_000L
                // Normalize timestamp recency (clamped to positive)
                if (candidate.lastPositionUpdateTime > 0L) {
                    score += (candidate.lastPositionUpdateTime % 1_000_000L)
                }
            }
            else -> score += 0L
        }

        if (candidate.hasTitleOrArtist) {
            score += 1_000L
        }

        return score
    }

    /**
     * Deterministically selects the primary candidate from a list.
     */
    fun selectBestCandidate(candidates: List<SessionCandidate>): SessionCandidate? {
        if (candidates.isEmpty()) return null
        return candidates.maxByOrNull { scoreCandidate(it) }
    }

    /**
     * Resolves the primary [MediaController] from a list of platform controllers.
     */
    fun selectActiveController(controllers: List<MediaController>): MediaController? {
        if (controllers.isEmpty()) return null
        if (controllers.size == 1) return controllers.first()

        val candidateMap = controllers.associateBy { it.sessionToken.toString() }
        val candidates = controllers.map { controller ->
            val playbackState = controller.playbackState
            val stateCode = playbackState?.state ?: PlaybackState.STATE_NONE
            val updateTime = playbackState?.lastPositionUpdateTime ?: 0L
            val metadata = controller.metadata
            val hasTitleOrArtist = !metadata?.getString(MediaMetadata.METADATA_KEY_TITLE).isNullOrBlank() ||
                    !metadata?.getString(MediaMetadata.METADATA_KEY_ARTIST).isNullOrBlank()

            SessionCandidate(
                id = controller.sessionToken.toString(),
                playbackState = stateCode,
                lastPositionUpdateTime = updateTime,
                hasTitleOrArtist = hasTitleOrArtist
            )
        }

        val best = selectBestCandidate(candidates) ?: return controllers.first()
        return candidateMap[best.id] ?: controllers.first()
    }
}
