package com.kunvarpreet.skirk.widget.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
 * Timeline Schedule design.
 * Features a vertical connected line with event nodes, formatted start/end times,
 * titles, and location tags.
 */
@Composable
fun TimelineScheduleDesign(
    data: ScheduleData,
    config: ScheduleWidgetConfig,
    modifier: Modifier = Modifier,
    onEventClick: (CalendarEvent) -> Unit = {}
) {
    val displayedEvents = remember(data.events, config) {
        data.events
            .filter { config.showAllDayEvents || !it.isAllDay }
            .take(config.maxEvents)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF0F172A).copy(alpha = 0.5f))
            .padding(14.dp),
        contentAlignment = Alignment.Center
    ) {
        if (displayedEvents.isEmpty()) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "🗓️", fontSize = 28.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "No Upcoming Events",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFF1F5F9)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Your schedule is completely clear",
                    fontSize = 11.sp,
                    color = Color(0xFF64748B)
                )
            }
        } else {
            Column(modifier = Modifier.fillMaxSize()) {
                Text(
                    text = "TIMELINE",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF38BDF8),
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    itemsIndexed(displayedEvents) { index, event ->
                        val isLast = index == displayedEvents.lastIndex
                        val eventColor = Color(event.color ?: 0xFF3B82F6.toInt())

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    onEventClick(event)
                                }
                        ) {
                            // Timeline track
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.width(20.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(eventColor)
                                )

                                if (!isLast) {
                                    Box(
                                        modifier = Modifier
                                            .width(1.5.dp)
                                            .height(36.dp)
                                            .background(Color.White.copy(alpha = 0.15f))
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            // Event content
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(bottom = if (isLast) 0.dp else 10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = event.title,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFFF8FAFC),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f)
                                    )

                                    Text(
                                        text = CalendarDateUtils.formatEventTime(
                                            startMillis = event.startEpochMillis,
                                            endMillis = event.endEpochMillis,
                                            isAllDay = event.isAllDay
                                        ),
                                        fontSize = 10.sp,
                                        color = Color(0xFF94A3B8),
                                        fontWeight = FontWeight.Medium
                                    )
                                }

                                if (config.showLocation && !event.location.isNullOrBlank()) {
                                    Spacer(modifier = Modifier.height(1.dp))
                                    Text(
                                        text = "📍 ${event.location}",
                                        fontSize = 10.sp,
                                        color = Color(0xFF64748B),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
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
fun TimelineScheduleDesignPreview() {
    TimelineScheduleDesign(
        data = ScheduleData.Sample,
        config = ScheduleWidgetConfig()
    )
}
