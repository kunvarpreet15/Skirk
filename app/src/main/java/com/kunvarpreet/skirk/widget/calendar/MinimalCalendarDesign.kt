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
import androidx.compose.runtime.remember
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
 * Minimal monthly calendar design.
 * Features an ultra-clean layout emphasizing the current date with high-contrast typography
 * and subtle dot indicators.
 */
@Composable
fun MinimalCalendarDesign(
    data: MonthCalendarData,
    config: CalendarWidgetConfig,
    modifier: Modifier = Modifier,
    onDaySelected: (LocalDate) -> Unit = {}
) {
    val weekdayLabels = remember { CalendarDateUtils.getWeekdayLabels() }
    val monthTitle = remember(data.yearMonth) {
        data.yearMonth.month.name.lowercase().replaceFirstChar { it.uppercase() } + " " + data.yearMonth.year
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF0F172A).copy(alpha = 0.35f))
            .padding(10.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Row: Month Name + Event Count Badge
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = monthTitle,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFE2E8F0)
                )

                val todayCell = data.days.find { it.isToday }
                if (todayCell != null && todayCell.events.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFF38BDF8).copy(alpha = 0.18f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${todayCell.events.size} today",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF38BDF8)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            // Weekday row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                weekdayLabels.forEach { label ->
                    Text(
                        text = label,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF475569),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Grid rows
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
                            val isToday = dayCell.isToday && config.highlightCurrentDay

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(24.dp)
                                    .clip(CircleShape)
                                    .background(if (isToday) Color(0xFF38BDF8) else Color.Transparent)
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {
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
                                            fontSize = 10.sp,
                                            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                                            color = when {
                                                isToday -> Color(0xFF0F172A)
                                                dayCell.isCurrentMonth -> Color(0xFFCBD5E1)
                                                else -> Color(0xFF334155)
                                            },
                                            lineHeight = 10.sp
                                        )

                                        if (config.showEventIndicators && dayCell.hasEvents && !isToday) {
                                            Spacer(modifier = Modifier.height(1.dp))
                                            Box(
                                                modifier = Modifier
                                                    .size(2.5.dp)
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
    }
}

@Preview(widthDp = 260, heightDp = 160)
@Composable
fun MinimalCalendarDesignPreview() {
    MinimalCalendarDesign(
        data = MonthCalendarData.Sample,
        config = CalendarWidgetConfig()
    )
}
