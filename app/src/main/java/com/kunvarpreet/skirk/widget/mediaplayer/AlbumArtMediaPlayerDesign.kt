package com.kunvarpreet.skirk.widget.mediaplayer

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kunvarpreet.skirk.media.model.MediaState

/**
 * Album Art Media Player design.
 * Features a rich StandBy layout centered around prominent album artwork,
 * full track metadata, interactive progress seek bar, and large controls.
 */
@Composable
fun AlbumArtMediaPlayerDesign(
    mediaState: MediaState,
    config: MediaPlayerConfig,
    onPlay: () -> Unit,
    onPause: () -> Unit,
    onSkipNext: () -> Unit,
    onSkipPrevious: () -> Unit,
    onSeekTo: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var isDraggingSlider by remember { mutableStateOf(false) }
    var sliderDragPosition by remember { mutableFloatStateOf(0f) }

    val currentPositionMs = if (isDraggingSlider) {
        (sliderDragPosition * mediaState.durationMs).toLong()
    } else {
        mediaState.currentInterpolatedPositionMs()
    }

    val progressFraction = if (mediaState.durationMs > 0L) {
        (currentPositionMs.toFloat() / mediaState.durationMs.toFloat()).coerceIn(0f, 1f)
    } else {
        0f
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1E1B4B).copy(alpha = 0.5f),
                        Color(0xFF0F172A).copy(alpha = 0.8f)
                    )
                )
            )
            .padding(14.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Prominent Artwork (Left / Primary)
            if (config.showArtwork) {
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF334155), Color(0xFF1E293B))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (mediaState.artwork != null) {
                        Image(
                            bitmap = mediaState.artwork.asImageBitmap(),
                            contentDescription = "Album Artwork",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Text(
                            text = "🎵",
                            fontSize = 42.sp
                        )
                    }
                }
            }

            // Info, Progress & Controls Column (Right)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                // Track metadata
                Column {
                    Text(
                        text = mediaState.trackTitle ?: "Unknown Track",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFF8FAFC),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = mediaState.artistName ?: mediaState.appInfo?.appName ?: "Unknown Artist",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF94A3B8),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Interactive Seek / Progress Bar (with gesture isolation)
                if (config.showProgress && mediaState.durationMs > 0L) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {}
                    ) {
                        Slider(
                            value = if (isDraggingSlider) sliderDragPosition else progressFraction,
                            onValueChange = { newValue ->
                                isDraggingSlider = true
                                sliderDragPosition = newValue
                            },
                            onValueChangeFinished = {
                                isDraggingSlider = false
                                val targetMs = (sliderDragPosition * mediaState.durationMs).toLong()
                                onSeekTo(targetMs)
                            },
                            enabled = mediaState.actions.canSeek,
                            colors = SliderDefaults.colors(
                                thumbColor = Color(0xFF38BDF8),
                                activeTrackColor = Color(0xFF38BDF8),
                                inactiveTrackColor = Color.White.copy(alpha = 0.15f)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(22.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = MediaFormatUtils.formatDuration(currentPositionMs),
                                fontSize = 10.sp,
                                color = Color(0xFF94A3B8)
                            )
                            Text(
                                text = MediaFormatUtils.formatDuration(mediaState.durationMs),
                                fontSize = 10.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }
                }

                // Controls Row (isolated from parent drags)
                if (config.showControls) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {},
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .clickable(enabled = mediaState.actions.canSkipPrevious, onClick = onSkipPrevious),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "⏮",
                                fontSize = 18.sp,
                                color = if (mediaState.actions.canSkipPrevious) Color(0xFFE2E8F0) else Color(0xFF475569)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF38BDF8))
                                .clickable(onClick = if (mediaState.isPlaying) onPause else onPlay),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (mediaState.isPlaying) "⏸" else "▶",
                                fontSize = 18.sp,
                                color = Color(0xFF0F172A)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .clickable(enabled = mediaState.actions.canSkipNext, onClick = onSkipNext),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "⏭",
                                fontSize = 18.sp,
                                color = if (mediaState.actions.canSkipNext) Color(0xFFE2E8F0) else Color(0xFF475569)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(widthDp = 360, heightDp = 160)
@Composable
fun AlbumArtMediaPlayerDesignPreview() {
    AlbumArtMediaPlayerDesign(
        mediaState = MediaState.Sample,
        config = MediaPlayerConfig(),
        onPlay = {},
        onPause = {},
        onSkipNext = {},
        onSkipPrevious = {},
        onSeekTo = {}
    )
}
