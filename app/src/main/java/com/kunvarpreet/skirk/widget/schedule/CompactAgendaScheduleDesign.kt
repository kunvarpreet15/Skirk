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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

/**
 * Compact Agenda Schedule design.
 * High-density list grouped by date headings ("TODAY", "TOMORROW", etc.)
 * optimized for displaying multiple upcoming commitments cleanly.
 */
@Composable
fun CompactAgendaScheduleDesign(
    data: ScheduleData,
    config: ScheduleWidgetConfig,
    modifier: Modifier = Modifier,
    zoneId: ZoneId = ZoneId.systemDefault(),
    onEventClick: (CalendarEvent) -> Unit = {}
) {
    val today = remember { LocalDate.now(zoneId) }
    val groupedEvents = remember(data.events, config, today) {
        val filtered = data.events
            .filter { config.showAllDayEvents || !it.isAllDay }
            .take(config.maxEvents)

        filtered.groupBy { event ->
            Instant.ofEpochMilli(event.startEpochMillis).atZone(zoneId).toLocalDate()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF0F172A).copy(alpha = 0.5f))
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        if (groupedEvents.isEmpty()) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "☕", fontSize = 28.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "No Upcoming Agenda",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFF1F5F9)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                groupedEvents.forEach { (date, eventsForDay) ->
                    item(key = "header_${date}") {
                        Text(
                            text = CalendarDateUtils.formatDateHeader(date, today).uppercase(),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF38BDF8),
                            letterSpacing = 1.sp,
                            modifier = Modifier.padding(bottom = 2.dp)
                        )
                    }

                    items(eventsForDay, key = { it.id }) { event ->
                        val eventColor = Color(event.color ?: 0xFF3B82F6.toInt())

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White.copy(alpha = 0.05f))
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    onEventClick(event)
                                }
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(3.dp, 20.dp)
                                    .clip(RoundedCornerShape(1.5.dp))
                                    .background(eventColor)
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = event.title,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFFF8FAFC),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                if (config.showLocation && !event.location.isNullOrBlank()) {
                                    Text(
                                        text = event.location,
                                        fontSize = 9.sp,
                                        color = Color(0xFF64748B),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = CalendarDateUtils.formatEventTime(
                                    startMillis = event.startEpochMillis,
                                    endMillis = event.endEpochMillis,
                                    isAllDay = event.isAllDay
                                ),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(widthDp = 300, heightDp = 180)
@Composable
fun CompactAgendaScheduleDesignPreview() {
    CompactAgendaScheduleDesign(
        data = ScheduleData.Sample,
        config = ScheduleWidgetConfig()
    )
}
