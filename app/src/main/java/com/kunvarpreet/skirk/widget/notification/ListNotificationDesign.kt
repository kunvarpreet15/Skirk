package com.kunvarpreet.skirk.widget.notification

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.kunvarpreet.skirk.notification.model.NotificationItem
import com.kunvarpreet.skirk.notification.model.NotificationState

@Composable
fun ListNotificationDesign(
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

    Column(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF141416))
            .padding(14.dp)
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
                        .background(Color(0xFFFF3B30))
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "NOTIFICATIONS",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF26262B))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "${filtered.size}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Notification List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 4.dp)
        ) {
            items(filtered, key = { it.key }) { item ->
                NotificationListCard(
                    item = item,
                    showAppName = config.showAppNames,
                    showTimestamp = config.showTimestamps
                )
            }
        }
    }
}

@Composable
private fun NotificationListCard(
    item: NotificationItem,
    showAppName: Boolean,
    showTimestamp: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF202024))
            .clickable(
                enabled = item.openNotification != null,
                onClick = { item.openNotification?.invoke() }
            )
            .padding(10.dp)
    ) {
        Column {
            if (showAppName || showTimestamp) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (showAppName) {
                        Text(
                            text = item.appName,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0A84FF),
                                fontSize = 11.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    } else {
                        Spacer(modifier = Modifier.width(1.dp))
                    }

                    if (showTimestamp) {
                        Text(
                            text = NotificationTimeFormatter.formatRelativeTime(item.postTime),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.White.copy(alpha = 0.45f),
                                fontSize = 10.sp
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
            }

            if (item.title.isNotBlank()) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        fontSize = 13.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (item.text.isNotBlank()) {
                Text(
                    text = item.text,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000, widthDp = 340, heightDp = 240)
@Composable
private fun ListNotificationDesignPreview() {
    ListNotificationDesign(
        state = NotificationState.sample(),
        config = NotificationWidgetConfig(),
        onRequestAccess = {}
    )
}
