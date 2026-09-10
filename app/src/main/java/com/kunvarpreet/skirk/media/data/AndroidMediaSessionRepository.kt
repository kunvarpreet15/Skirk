package com.kunvarpreet.skirk.media.data

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaMetadata
import android.media.session.MediaController
import android.media.session.MediaSessionManager
import android.media.session.PlaybackState
import android.os.Build
import android.provider.Settings
import androidx.core.app.NotificationManagerCompat
import com.kunvarpreet.skirk.media.domain.MediaSessionRepository
import com.kunvarpreet.skirk.media.model.MediaAccessState
import com.kunvarpreet.skirk.media.model.MediaAppInfo
import com.kunvarpreet.skirk.media.model.MediaPlaybackActions
import com.kunvarpreet.skirk.media.model.MediaState
import com.kunvarpreet.skirk.media.model.PlaybackStatus
import com.kunvarpreet.skirk.media.service.SkirkNotificationListenerService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * Android platform implementation of [MediaSessionRepository] observing
 * external media sessions through [MediaSessionManager] and [MediaController].
 */
class AndroidMediaSessionRepository(
    private val context: Context
) : MediaSessionRepository {

    private val mediaSessionManager by lazy {
        context.getSystemService(Context.MEDIA_SESSION_SERVICE) as? MediaSessionManager
    }

    @Volatile
    private var currentActiveController: MediaController? = null

    override fun observeMediaState(): Flow<MediaState> = callbackFlow {
        val listenerComponent = SkirkNotificationListenerService.getComponentName(context)
        var controllerCallback: MediaController.Callback? = null
        var progressJob: Job? = null
        val progressScope = CoroutineScope(Dispatchers.Default)

        fun updateState() {
            if (!isNotificationAccessGranted()) {
                currentActiveController = null
                progressJob?.cancel()
                trySend(MediaState(accessState = MediaAccessState.PERMISSION_REQUIRED))
                return
            }

            val manager = mediaSessionManager
            if (manager == null) {
                currentActiveController = null
                progressJob?.cancel()
                trySend(MediaState(accessState = MediaAccessState.NO_MEDIA))
                return
            }

            val controllers = try {
                manager.getActiveSessions(listenerComponent)
            } catch (e: SecurityException) {
                currentActiveController = null
                progressJob?.cancel()
                trySend(MediaState(accessState = MediaAccessState.PERMISSION_REQUIRED))
                return
            } catch (e: Exception) {
                emptyList()
            }

            val bestController = SessionSelectionStrategy.selectActiveController(controllers)

            if (bestController == null) {
                currentActiveController = null
                progressJob?.cancel()
                trySend(MediaState(accessState = MediaAccessState.NO_MEDIA))
                return
            }

            // If switching controllers, detach previous callback
            if (currentActiveController?.sessionToken != bestController.sessionToken) {
                controllerCallback?.let { cb ->
                    try {
                        currentActiveController?.unregisterCallback(cb)
                    } catch (e: Exception) {}
                }

                currentActiveController = bestController

                val newCallback = object : MediaController.Callback() {
                    override fun onPlaybackStateChanged(state: PlaybackState?) {
                        updateState()
                    }

                    override fun onMetadataChanged(metadata: MediaMetadata?) {
                        updateState()
                    }

                    override fun onSessionDestroyed() {
                        updateState()
                    }
                }
                controllerCallback = newCallback
                try {
                    bestController.registerCallback(newCallback)
                } catch (e: Exception) {}
            }

            val mediaState = extractMediaState(bestController)
            trySend(mediaState)

            // Manage progress ticker: tick each second only while actively playing
            if (mediaState.isPlaying) {
                if (progressJob == null || progressJob?.isActive == false) {
                    progressJob = progressScope.launch {
                        while (isActive) {
                            delay(1000L)
                            currentActiveController?.let { ctrl ->
                                trySend(extractMediaState(ctrl))
                            }
                        }
                    }
                }
            } else {
                progressJob?.cancel()
                progressJob = null
            }
        }

        // Initial fetch
        updateState()

        val sessionsChangedListener = MediaSessionManager.OnActiveSessionsChangedListener {
            updateState()
        }

        try {
            mediaSessionManager?.addOnActiveSessionsChangedListener(
                sessionsChangedListener,
                listenerComponent
            )
        } catch (e: Exception) {
            // Permission might be revoked or service uninstalled
            trySend(MediaState(accessState = MediaAccessState.PERMISSION_REQUIRED))
        }

        awaitClose {
            progressJob?.cancel()
            try {
                mediaSessionManager?.removeOnActiveSessionsChangedListener(sessionsChangedListener)
            } catch (e: Exception) {}

            controllerCallback?.let { cb ->
                try {
                    currentActiveController?.unregisterCallback(cb)
                } catch (e: Exception) {}
            }
            currentActiveController = null
        }
    }.distinctUntilChanged()

    override fun play() {
        try {
            currentActiveController?.transportControls?.play()
        } catch (e: Exception) {}
    }

    override fun pause() {
        try {
            currentActiveController?.transportControls?.pause()
        } catch (e: Exception) {}
    }

    override fun skipToNext() {
        try {
            currentActiveController?.transportControls?.skipToNext()
        } catch (e: Exception) {}
    }

    override fun skipToPrevious() {
        try {
            currentActiveController?.transportControls?.skipToPrevious()
        } catch (e: Exception) {}
    }

    override fun seekTo(positionMs: Long) {
        try {
            currentActiveController?.transportControls?.seekTo(positionMs)
        } catch (e: Exception) {}
    }

    override fun isNotificationAccessGranted(): Boolean {
        return try {
            val enabledListeners = NotificationManagerCompat.getEnabledListenerPackages(context)
            if (enabledListeners.contains(context.packageName)) {
                return true
            }
            val flat = Settings.Secure.getString(
                context.contentResolver,
                "enabled_notification_listeners"
            )
            flat != null && flat.contains(context.packageName)
        } catch (e: Exception) {
            false
        }
    }

    override fun openNotificationAccessSettings(context: Context) {
        val listenerComponent = SkirkNotificationListenerService.getComponentName(context)
        val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Intent(Settings.ACTION_NOTIFICATION_LISTENER_DETAIL_SETTINGS).apply {
                putExtra(
                    Settings.EXTRA_NOTIFICATION_LISTENER_COMPONENT_NAME,
                    listenerComponent.flattenToString()
                )
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        } else {
            Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        }

        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            try {
                context.startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                })
            } catch (e2: Exception) {
                try {
                    context.startActivity(Intent(Settings.ACTION_SETTINGS).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    })
                } catch (e3: Exception) {}
            }
        }
    }

    private fun extractMediaState(controller: MediaController): MediaState {
        val metadata = controller.metadata
        val playbackState = controller.playbackState

        val stateCode = playbackState?.state ?: PlaybackState.STATE_NONE
        val isPlaying = stateCode == PlaybackState.STATE_PLAYING

        val status = when (stateCode) {
            PlaybackState.STATE_PLAYING -> PlaybackStatus.PLAYING
            PlaybackState.STATE_PAUSED -> PlaybackStatus.PAUSED
            PlaybackState.STATE_BUFFERING -> PlaybackStatus.BUFFERING
            PlaybackState.STATE_STOPPED -> PlaybackStatus.STOPPED
            else -> PlaybackStatus.NONE
        }

        val rawPosition = playbackState?.position ?: 0L
        val updateTime = playbackState?.lastPositionUpdateTime ?: 0L
        val speed = playbackState?.playbackSpeed ?: 1.0f
        val duration = metadata?.getLong(MediaMetadata.METADATA_KEY_DURATION) ?: 0L

        val actionsMask = playbackState?.actions ?: 0L
        val canPlay = (actionsMask and PlaybackState.ACTION_PLAY) != 0L || (actionsMask and PlaybackState.ACTION_PLAY_PAUSE) != 0L
        val canPause = (actionsMask and PlaybackState.ACTION_PAUSE) != 0L || (actionsMask and PlaybackState.ACTION_PLAY_PAUSE) != 0L
        val canSkipNext = (actionsMask and PlaybackState.ACTION_SKIP_TO_NEXT) != 0L
        val canSkipPrevious = (actionsMask and PlaybackState.ACTION_SKIP_TO_PREVIOUS) != 0L
        val canSeek = (actionsMask and PlaybackState.ACTION_SEEK_TO) != 0L

        val title = metadata?.getString(MediaMetadata.METADATA_KEY_TITLE)
            ?: metadata?.getString(MediaMetadata.METADATA_KEY_DISPLAY_TITLE)
        val artist = metadata?.getString(MediaMetadata.METADATA_KEY_ARTIST)
            ?: metadata?.getString(MediaMetadata.METADATA_KEY_ALBUM_ARTIST)
            ?: metadata?.getString(MediaMetadata.METADATA_KEY_DISPLAY_SUBTITLE)
        val album = metadata?.getString(MediaMetadata.METADATA_KEY_ALBUM)
            ?: metadata?.getString(MediaMetadata.METADATA_KEY_DISPLAY_DESCRIPTION)

        val rawArtwork = metadata?.getBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART)
            ?: metadata?.getBitmap(MediaMetadata.METADATA_KEY_ART)
        val scaledArtwork = downscaleBitmapIfNeeded(rawArtwork, maxDimension = 512)

        val appInfo = try {
            val pm = context.packageManager
            val ai = pm.getApplicationInfo(controller.packageName, 0)
            val label = pm.getApplicationLabel(ai).toString()
            MediaAppInfo(packageName = controller.packageName, appName = label)
        } catch (e: Exception) {
            null
        }

        return MediaState(
            accessState = MediaAccessState.ACTIVE,
            trackTitle = title,
            artistName = artist,
            albumName = album,
            artwork = scaledArtwork,
            isPlaying = isPlaying,
            playbackStatus = status,
            positionMs = rawPosition.coerceAtLeast(0L),
            durationMs = duration.coerceAtLeast(0L),
            playbackSpeed = speed,
            lastUpdateTime = updateTime,
            actions = MediaPlaybackActions(
                canPlay = canPlay || !isPlaying,
                canPause = canPause || isPlaying,
                canSkipNext = canSkipNext,
                canSkipPrevious = canSkipPrevious,
                canSeek = canSeek && duration > 0L
            ),
            appInfo = appInfo
        )
    }

    private fun downscaleBitmapIfNeeded(bitmap: Bitmap?, maxDimension: Int): Bitmap? {
        if (bitmap == null) return null
        val width = bitmap.width
        val height = bitmap.height
        if (width <= maxDimension && height <= maxDimension) return bitmap

        val ratio = width.toFloat() / height.toFloat()
        val newWidth: Int
        val newHeight: Int
        if (width > height) {
            newWidth = maxDimension
            newHeight = (maxDimension / ratio).toInt().coerceAtLeast(1)
        } else {
            newHeight = maxDimension
            newWidth = (maxDimension * ratio).toInt().coerceAtLeast(1)
        }

        return try {
            Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
        } catch (e: Exception) {
            bitmap
        }
    }
}
