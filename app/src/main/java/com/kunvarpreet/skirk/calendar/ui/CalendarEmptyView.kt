package com.kunvarpreet.skirk.calendar.ui

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
import com.kunvarpreet.skirk.calendar.model.CalendarAccessState

/**
 * Empty and permission fallback views for Calendar and Schedule widgets.
 */
@Composable
fun CalendarEmptyView(
    state: CalendarAccessState,
    onGrantPermission: () -> Unit,
    modifier: Modifier = Modifier,
    emptyMessage: String = "No upcoming events"
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF0F172A).copy(alpha = 0.5f))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        when (state) {
            CalendarAccessState.DENIED -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = "📅", fontSize = 28.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Calendar Access Required",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFF1F5F9),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Grant calendar permission to view dates and events in StandBy",
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8),
                        textAlign = TextAlign.Center,
                        lineHeight = 14.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = onGrantPermission,
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
            }
            CalendarAccessState.NO_CALENDARS -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = "📆", fontSize = 28.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "No Calendars Found",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFF1F5F9)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Add an account with a calendar in system settings",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B),
                        textAlign = TextAlign.Center
                    )
                }
            }
            CalendarAccessState.ERROR -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = "⚠️", fontSize = 28.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Calendar Unavailable",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFF1F5F9)
                    )
                }
            }
            CalendarAccessState.GRANTED -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = "🗓️", fontSize = 28.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = emptyMessage,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFF1F5F9)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Nothing scheduled right now",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }
        }
    }
}

@Preview(widthDp = 240, heightDp = 180)
@Composable
fun CalendarEmptyViewPermissionPreview() {
    CalendarEmptyView(
        state = CalendarAccessState.DENIED,
        onGrantPermission = {}
    )
}

@Preview(widthDp = 240, heightDp = 180)
@Composable
fun CalendarEmptyViewNoEventsPreview() {
    CalendarEmptyView(
        state = CalendarAccessState.GRANTED,
        onGrantPermission = {}
    )
}
