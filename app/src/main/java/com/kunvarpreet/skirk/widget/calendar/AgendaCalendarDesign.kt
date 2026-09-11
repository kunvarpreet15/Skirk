package com.kunvarpreet.skirk.widget.calendar

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.kunvarpreet.skirk.calendar.model.MonthCalendarData
import com.kunvarpreet.skirk.calendar.util.CalendarDateUtils
import java.time.LocalDate

/**
 * Agenda Calendar design.
 * Combines a compact month grid on the left with an upcoming day agenda list on the right.
 */
@Composable
fun AgendaCalendarDesign(
    data: MonthCalendarData,
    config: CalendarWidgetConfig,
    modifier: Modifier = Modifier,
    onDaySelected: (LocalDate) -> Unit = {}
) {
    var selectedDate by remember(data.selectedDate) { mutableStateOf(data.selectedDate) }
    val weekdayLabels = remember { CalendarDateUtils.getWeekdayLabels() }
    val monthTitle = remember(data.yearMonth) {
        data.yearMonth.month.name.take(3) + " " + data.yearMonth.year
    }

    // Resolve events for the currently selected date
    val selectedDayEvents = remember(selectedDate, data.days) {
        data.days.find { it.date == selectedDate }?.events ?: emptyList()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF0F172A).copy(alpha = 0.5f))
            .padding(10.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Left Half: Month Grid
            Column(
                modifier = Modifier
                    .weight(1.1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = monthTitle,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFF1F5F9),
                    modifier = Modifier.padding(start = 2.dp)
                )

                Spacer(modifier = Modifier.height(2.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    weekdayLabels.forEach { label ->
                        Text(
                            text = label,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color(0xFF64748B),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                val rows = remember(data.days) { data.days.chunked(7) }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    rows.forEach { week ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            week.forEach { dayCell ->
                                val isSelected = dayCell.date == selectedDate
                                val isToday = dayCell.isToday

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(22.dp)
                                        .clip(CircleShape)
                                        .background(
                                            when {
                                                isSelected -> Color(0xFF3B82F6)
                                                isToday -> Color.White.copy(alpha = 0.12f)
                                                else -> Color.Transparent
                                            }
                                        )
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null
                                        ) {
                                            selectedDate = dayCell.date
                                            onDaySelected(dayCell.date)
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (dayCell.isCurrentMonth || config.showAdjacentMonthDays) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            Text(
                                                text = dayCell.date.dayOfMonth.toString(),
                                                fontSize = 9.sp,
                                                fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal,
                                                color = when {
                                                    isSelected -> Color.White
                                                    isToday -> Color(0xFF38BDF8)
                                                    dayCell.isCurrentMonth -> Color(0xFFE2E8F0)
                                                    else -> Color(0xFF475569)
                                                },
                                                lineHeight = 9.sp
                                            )
                                            if (config.showEventIndicators && dayCell.hasEvents && !isSelected) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(2.dp)
                                                        .clip(CircleShape)
                                                        .background(Color(0xFF38BDF8))
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

            // Divider line
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .fillMaxHeight()
                    .background(Color.White.copy(alpha = 0.08f))
            )

            // Right Half: Day Agenda List
            Column(
                modifier = Modifier
                    .weight(0.9f)
                    .fillMaxHeight()
            ) {
                Text(
                    text = CalendarDateUtils.formatDateHeader(selectedDate),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF38BDF8),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                if (selectedDayEvents.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No events",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(selectedDayEvents) { event ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color.White.copy(alpha = 0.05f))
                                    .padding(horizontal = 6.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(3.dp, 16.dp)
                                        .clip(RoundedCornerShape(1.5.dp))
                                        .background(Color(event.color ?: 0xFF3B82F6.toInt()))
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = event.title,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFFF1F5F9),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = CalendarDateUtils.formatEventTime(
                                            startMillis = event.startEpochMillis,
                                            endMillis = event.endEpochMillis,
                                            isAllDay = event.isAllDay
                                        ),
                                        fontSize = 8.sp,
                                        color = Color(0xFF94A3B8)
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

@Preview(widthDp = 340, heightDp = 180)
@Composable
fun AgendaCalendarDesignPreview() {
    AgendaCalendarDesign(
        data = MonthCalendarData.Sample,
        config = CalendarWidgetConfig()
    )
}
