package com.kunvarpreet.skirk.widget.mediaplayer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kunvarpreet.skirk.media.model.MediaAccessState
import com.kunvarpreet.skirk.media.model.MediaState

/**
 * Glanceable empty and permission fallback states for the Media Player widget.
 */
@Composable
fun EmptyMediaView(
    mediaState: MediaState,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF0F172A).copy(alpha = 0.6f))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        if (mediaState.accessState == MediaAccessState.PERMISSION_REQUIRED) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "🔒",
                    fontSize = 28.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Media Access Needed",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFF1F5F9),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Enable notification access to control music & podcasts in StandBy",
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8),
                    textAlign = TextAlign.Center,
                    lineHeight = 14.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = onOpenSettings,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF3B82F6),
                        contentColor = Color.White
                    ),
                    modifier = Modifier.height(34.dp)
                ) {
                    Text(
                        text = "Grant Access",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "🎵",
                    fontSize = 32.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "No Media Playing",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFF1F5F9)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Play music or podcasts in any app",
                    fontSize = 11.sp,
                    color = Color(0xFF64748B)
                )
            }
        }
    }
}

@Preview(widthDp = 240, heightDp = 180)
@Composable
fun EmptyMediaViewPermissionPreview() {
    MaterialTheme {
        EmptyMediaView(
            mediaState = MediaState.PermissionRequired,
            onOpenSettings = {}
        )
    }
}

@Preview(widthDp = 240, heightDp = 180)
@Composable
fun EmptyMediaViewNoMediaPreview() {
    MaterialTheme {
        EmptyMediaView(
            mediaState = MediaState.Empty,
            onOpenSettings = {}
        )
    }
}
