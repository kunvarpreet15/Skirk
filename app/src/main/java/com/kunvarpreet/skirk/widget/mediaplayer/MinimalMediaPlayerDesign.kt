package com.kunvarpreet.skirk.widget.mediaplayer

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
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kunvarpreet.skirk.media.model.MediaState

/**
 * Minimal Media Player design.
 * Features an ultra-clean typographic presentation with focused playback controls
 * and sleek minimal progress tracking.
 */
@Composable
fun MinimalMediaPlayerDesign(
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
            .background(Color(0xFF0F172A).copy(alpha = 0.4f))
            .padding(horizontal = 20.dp, vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Song title
            Text(
                text = mediaState.trackTitle ?: "Unknown Track",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFF8FAFC),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(3.dp))

            // Artist & Source
            Text(
                text = mediaState.artistName ?: mediaState.appInfo?.appName ?: "Unknown Artist",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF94A3B8),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )

            // Thin progress indicator
            if (config.showProgress && mediaState.durationMs > 0L) {
                Spacer(modifier = Modifier.height(10.dp))
                LinearProgressIndicator(
                    progress = { mediaState.progressFraction },
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(2.dp)
                        .clip(RoundedCornerShape(1.dp)),
                    color = Color(0xFF38BDF8),
                    trackColor = Color.White.copy(alpha = 0.1f)
                )
            }

            // Controls
            if (config.showControls) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {}
                ) {
                    if (mediaState.actions.canSkipPrevious) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .clickable(onClick = onSkipPrevious),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "⏮",
                                fontSize = 14.sp,
                                color = Color(0xFFCBD5E1)
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.12f))
                            .clickable(onClick = if (mediaState.isPlaying) onPause else onPlay),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (mediaState.isPlaying) "❚❚" else "▶",
                            fontSize = 16.sp,
                            color = Color(0xFFF8FAFC)
                        )
                    }

                    if (mediaState.actions.canSkipNext) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .clickable(onClick = onSkipNext),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "⏭",
                                fontSize = 14.sp,
                                color = Color(0xFFCBD5E1)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(widthDp = 280, heightDp = 140)
@Composable
fun MinimalMediaPlayerDesignPreview() {
    MinimalMediaPlayerDesign(
        mediaState = MediaState.Sample,
        config = MediaPlayerConfig(),
        onPlay = {},
        onPause = {},
        onSkipNext = {},
        onSkipPrevious = {}
    )
}
