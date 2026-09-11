package com.kunvarpreet.skirk.widget.schedule

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kunvarpreet.skirk.calendar.model.CalendarEvent
import com.kunvarpreet.skirk.calendar.model.ScheduleData
import com.kunvarpreet.skirk.calendar.util.CalendarDateUtils

/**
 * Large Event Schedule design.
 * Emphasizes the next upcoming event with prominent typography, location,
 * and time remaining, complemented by compact secondary upcoming items.
 */
@Composable
fun LargeEventScheduleDesign(
    data: ScheduleData,
    config: ScheduleWidgetConfig,
    modifier: Modifier = Modifier,
    onEventClick: (CalendarEvent) -> Unit = {}
) {
    val nextEvent = data.events.firstOrNull()
    val subsequentEvents = data.events.drop(1).take(2)

    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF0F172A).copy(alpha = 0.5f))
            .padding(14.dp),
        contentAlignment = Alignment.Center
    ) {
        if (nextEvent == null) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "✨", fontSize = 28.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "No Upcoming Events",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFF1F5F9)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Relax and enjoy your day",
                    fontSize = 11.sp,
                    color = Color(0xFF64748B)
                )
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Primary Hero Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Color(nextEvent.color ?: 0xFF3B82F6.toInt()).copy(alpha = 0.18f)
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            onEventClick(nextEvent)
                        }
                        .padding(12.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "NEXT EVENT",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(nextEvent.color ?: 0xFF38BDF8.toInt()),
                                letterSpacing = 1.sp
                            )

                            Text(
                                text = CalendarDateUtils.formatEventTime(
                                    startMillis = nextEvent.startEpochMillis,
                                    endMillis = nextEvent.endEpochMillis,
                                    isAllDay = nextEvent.isAllDay
                                ),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFFE2E8F0)
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = nextEvent.title,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFF8FAFC),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        if (config.showLocation && !nextEvent.location.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "📍 ${nextEvent.location}",
                                fontSize = 11.sp,
                                color = Color(0xFF94A3B8),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                // Subsequent upcoming events row
                if (subsequentEvents.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        subsequentEvents.forEach { event ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color.White.copy(alpha = 0.04f))
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        onEventClick(event)
                                    }
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(5.dp)
                                            .clip(CircleShape)
                                            .background(Color(event.color ?: 0xFF3B82F6.toInt()))
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = event.title,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFFCBD5E1),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                Text(
                                    text = CalendarDateUtils.formatEventTime(
                                        startMillis = event.startEpochMillis,
                                        endMillis = event.endEpochMillis,
                                        isAllDay = event.isAllDay
                                    ),
                                    fontSize = 9.sp,
                                    color = Color(0xFF64748B)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(widthDp = 300, heightDp = 180)
@Composable
fun LargeEventScheduleDesignPreview() {
    LargeEventScheduleDesign(
        data = ScheduleData.Sample,
        config = ScheduleWidgetConfig()
    )
}
