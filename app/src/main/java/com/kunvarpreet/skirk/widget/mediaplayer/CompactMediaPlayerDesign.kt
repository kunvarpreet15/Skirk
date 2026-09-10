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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kunvarpreet.skirk.media.model.MediaState

/**
 * Compact Media Player design.
 * Features a streamlined horizontal layout with glanceable title/artist
 * and dedicated playback controls.
 */
@Composable
fun CompactMediaPlayerDesign(
    mediaState: MediaState,
    config: MediaPlayerConfig,
    onPlay: () -> Unit,
    onPause: () -> Unit,
    onSkipNext: () -> Unit,
    onSkipPrevious: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF0F172A).copy(alpha = 0.5f))
            .padding(14.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Artwork or fallback music icon
                if (config.showArtwork) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF1E293B)),
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
                                text = "♪",
                                fontSize = 24.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))
                }

                // Track title & artist
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = mediaState.trackTitle ?: "Unknown Track",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFF8FAFC),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = mediaState.artistName ?: mediaState.appInfo?.appName ?: "Unknown Artist",
                        fontSize = 13.sp,
                        color = Color(0xFF94A3B8),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Interactive Controls Container (isolated from parent drags)
                if (config.showControls) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {}
                    ) {
                        if (mediaState.actions.canSkipPrevious) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .clickable(onClick = onSkipPrevious),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "⏮",
                                    fontSize = 15.sp,
                                    color = Color(0xFFE2E8F0)
                                )
                            }
                        }

                        // Play/Pause Primary Action
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF38BDF8))
                                .clickable(onClick = if (mediaState.isPlaying) onPause else onPlay),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (mediaState.isPlaying) "⏸" else "▶",
                                fontSize = 16.sp,
                                color = Color(0xFF0F172A)
                            )
                        }

                        if (mediaState.actions.canSkipNext) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .clickable(onClick = onSkipNext),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "⏭",
                                    fontSize = 15.sp,
                                    color = Color(0xFFE2E8F0)
                                )
                            }
                        }
                    }
                }
            }

            // Subtle progress bar at bottom if enabled
            if (config.showProgress && mediaState.durationMs > 0L) {
                Spacer(modifier = Modifier.height(10.dp))
                LinearProgressIndicator(
                    progress = { mediaState.progressFraction },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .clip(RoundedCornerShape(1.5.dp)),
                    color = Color(0xFF38BDF8),
                    trackColor = Color.White.copy(alpha = 0.12f)
                )
            }
        }
    }
}

@Preview(widthDp = 300, heightDp = 120)
@Composable
fun CompactMediaPlayerDesignPreview() {
    CompactMediaPlayerDesign(
        mediaState = MediaState.Sample,
        config = MediaPlayerConfig(),
        onPlay = {},
        onPause = {},
        onSkipNext = {},
        onSkipPrevious = {}
    )
}
