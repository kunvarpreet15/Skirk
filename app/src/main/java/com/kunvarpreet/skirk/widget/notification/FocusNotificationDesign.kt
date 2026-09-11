package com.kunvarpreet.skirk.widget.notification

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kunvarpreet.skirk.notification.model.NotificationAccessState
import com.kunvarpreet.skirk.notification.model.NotificationState

@Composable
fun FocusNotificationDesign(
    state: NotificationState,
    config: NotificationWidgetConfig,
    onRequestAccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (state.accessState == NotificationAccessState.PERMISSION_REQUIRED) {
        NotificationEmptyView(
            accessState = NotificationAccessState.PERMISSION_REQUIRED,
            onRequestAccess = onRequestAccess,
            modifier = modifier
        )
        return
    }

    val filtered = state.notifications
        .filter { config.includeOngoing || !it.isOngoing }
        .take(config.maxNotifications)

    if (filtered.isEmpty()) {
        NotificationEmptyView(
            accessState = NotificationAccessState.GRANTED,
            onRequestAccess = onRequestAccess,
            modifier = modifier
        )
        return
    }

    val newest = filtered.first()
    val remainingCount = filtered.size - 1

    Column(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF131418))
            .padding(14.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFF9F0A))
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "LATEST FOCUS",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                )
            }

            if (remainingCount > 0) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF26262B))
                        .padding(horizontal = 7.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "+$remainingCount more",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White.copy(alpha = 0.75f),
                            fontSize = 11.sp
                        )
                    )
                }
            }
        }

        // Spotlight Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = 8.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFF1E2026))
                .clickable(
                    enabled = newest.openNotification != null,
                    onClick = { newest.openNotification?.invoke() }
                )
                .padding(12.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (config.showAppNames) {
                            Text(
                                text = newest.appName.uppercase(),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF0A84FF),
                                    letterSpacing = 0.5.sp,
                                    fontSize = 10.sp
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        } else {
                            Spacer(modifier = Modifier.width(1.dp))
                        }

                        if (config.showTimestamps) {
                            Text(
                                text = NotificationTimeFormatter.formatRelativeTime(newest.postTime),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color.White.copy(alpha = 0.45f),
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    if (newest.title.isNotBlank()) {
                        Text(
                            text = newest.title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 15.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                    }

                    if (newest.text.isNotBlank()) {
                        Text(
                            text = newest.text,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color.White.copy(alpha = 0.75f),
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            ),
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                if (newest.openNotification != null) {
                    Text(
                        text = "Tap to view in app →",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(0xFF0A84FF),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 10.sp
                        )
                    )
                }
            }
        }

        // Summary footer
        if (remainingCount > 0) {
            val nextItem = filtered[1]
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Next: ",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.4f),
                        fontSize = 11.sp
                    )
                )
                Text(
                    text = "${nextItem.appName} — ${if (nextItem.title.isNotBlank()) nextItem.title else nextItem.text}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 11.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
            }
        } else {
            Text(
                text = "No other notifications",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Color.White.copy(alpha = 0.35f),
                    fontSize = 10.sp
                )
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000, widthDp = 340, heightDp = 240)
@Composable
private fun FocusNotificationDesignPreview() {
    FocusNotificationDesign(
        state = NotificationState.sample(),
        config = NotificationWidgetConfig(selectedDesign = NotificationWidgetConfig.DESIGN_FOCUS),
        onRequestAccess = {}
    )
}
