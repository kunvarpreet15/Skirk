package com.kunvarpreet.skirk.widget.calendar

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kunvarpreet.skirk.calendar.model.MonthCalendarData
import com.kunvarpreet.skirk.calendar.util.CalendarDateUtils
import java.time.LocalDate

/**
 * Classic monthly calendar design.
 * Features a traditional 7-column month grid with weekday labels, current day emphasis,
 * event indicator dots, and interactive day selection.
 */
@Composable
fun ClassicCalendarDesign(
    data: MonthCalendarData,
    config: CalendarWidgetConfig,
    modifier: Modifier = Modifier,
    onDaySelected: (LocalDate) -> Unit = {}
) {
    var localSelectedDate by remember(data.selectedDate) { mutableStateOf(data.selectedDate) }
    val weekdayLabels = remember { CalendarDateUtils.getWeekdayLabels() }
    val monthHeader = remember(data.yearMonth) { CalendarDateUtils.formatMonthHeader(data.yearMonth) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF0F172A).copy(alpha = 0.5f))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Month Header
            Text(
                text = monthHeader,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFF1F5F9),
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Weekday row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                weekdayLabels.forEach { label ->
                    Text(
                        text = label,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF64748B),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Grid rows (chunks of 7)
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
                            val isSelected = dayCell.date == localSelectedDate
                            val isToday = dayCell.isToday && config.highlightCurrentDay
                            val isVisible = dayCell.isCurrentMonth || config.showAdjacentMonthDays

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(28.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            isToday -> Color(0xFF3B82F6)
                                            isSelected -> Color.White.copy(alpha = 0.15f)
                                            else -> Color.Transparent
                                        }
                                    )
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null,
                                        enabled = isVisible
                                    ) {
                                        localSelectedDate = dayCell.date
                                        onDaySelected(dayCell.date)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                if (isVisible) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text = dayCell.date.dayOfMonth.toString(),
                                            fontSize = 11.sp,
                                            fontWeight = if (isToday || isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = when {
                                                isToday -> Color.White
                                                isSelected -> Color(0xFF38BDF8)
                                                dayCell.isCurrentMonth -> Color(0xFFE2E8F0)
                                                else -> Color(0xFF475569)
                                            },
                                            lineHeight = 11.sp
                                        )

                                        // Event indicator dot
                                        if (config.showEventIndicators && dayCell.hasEvents) {
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Box(
                                                modifier = Modifier
                                                    .size(3.dp)
                                                    .clip(CircleShape)
                                                    .background(
                                                        if (isToday) Color.White else Color(0xFF38BDF8)
                                                    )
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
    }
}

@Preview(widthDp = 280, heightDp = 180)
@Composable
fun ClassicCalendarDesignPreview() {
    ClassicCalendarDesign(
        data = MonthCalendarData.Sample,
        config = CalendarWidgetConfig()
    )
}
