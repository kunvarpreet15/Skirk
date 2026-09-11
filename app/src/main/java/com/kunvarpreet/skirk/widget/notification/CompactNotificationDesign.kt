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
import com.kunvarpreet.skirk.notification.model.NotificationItem
import com.kunvarpreet.skirk.notification.model.NotificationState

@Composable
fun CompactNotificationDesign(
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
            .background(Color(0xFF111214))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "ALERTS",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.2.sp,
                    color = Color.White.copy(alpha = 0.5f)
                )
            )
            Text(
                text = "${filtered.size} active",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Color(0xFF30D158),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp
                )
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Compact Rows
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            filtered.take(4).forEach { item ->
                CompactNotificationRow(
                    item = item,
                    showAppName = config.showAppNames,
                    showTimestamp = config.showTimestamps
                )
            }
        }
    }
}

@Composable
private fun CompactNotificationRow(
    item: NotificationItem,
    showAppName: Boolean,
    showTimestamp: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF1C1D21))
            .clickable(
                enabled = item.openNotification != null,
                onClick = { item.openNotification?.invoke() }
            )
            .padding(horizontal = 10.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Dot indicator
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(Color(0xFF0A84FF))
        )
        Spacer(modifier = Modifier.width(8.dp))

        if (showAppName) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFF282A30))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = item.appName,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 10.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        val primaryText = if (item.title.isNotBlank()) item.title else item.text
        Text(
            text = primaryText,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Medium,
                color = Color.White,
                fontSize = 12.sp
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )

        if (showTimestamp) {
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = NotificationTimeFormatter.formatRelativeTime(item.postTime),
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Color.White.copy(alpha = 0.4f),
                    fontSize = 10.sp
                )
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000, widthDp = 340, heightDp = 240)
@Composable
private fun CompactNotificationDesignPreview() {
    CompactNotificationDesign(
        state = NotificationState.sample(),
        config = NotificationWidgetConfig(selectedDesign = NotificationWidgetConfig.DESIGN_COMPACT),
        onRequestAccess = {}
    )
}
