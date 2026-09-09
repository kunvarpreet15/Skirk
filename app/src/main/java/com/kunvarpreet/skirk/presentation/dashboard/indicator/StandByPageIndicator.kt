package com.kunvarpreet.skirk.presentation.dashboard.indicator

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Clean, subtle page indicator designed specifically for StandBy mode.
 * Communicates the active panel without cluttering the screen or distracting the user.
 */
@Composable
fun StandByPageIndicator(
    pageCount: Int,
    currentPage: Int,
    onSelectPage: (Int) -> Unit,
    modifier: Modifier = Modifier,
    activeColor: Color = Color(0xFF38BDF8),
    inactiveColor: Color = Color.White.copy(alpha = 0.25f),
    dotSize: Dp = 6.dp,
    activeDotWidth: Dp = 18.dp,
    spacing: Dp = 6.dp
) {
    if (pageCount <= 1) return

    Row(
        modifier = modifier
            .padding(vertical = 4.dp)
            .semantics {
                contentDescription = "Panel ${currentPage + 1} of $pageCount"
            },
        horizontalArrangement = Arrangement.spacedBy(spacing),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (index in 0 until pageCount) {
            val isSelected = index == currentPage

            val animatedWidth by animateDpAsState(
                targetValue = if (isSelected) activeDotWidth else dotSize,
                animationSpec = tween(durationMillis = 250),
                label = "dotWidth"
            )

            val animatedColor by animateColorAsState(
                targetValue = if (isSelected) activeColor else inactiveColor,
                animationSpec = tween(durationMillis = 250),
                label = "dotColor"
            )

            Box(
                modifier = Modifier
                    .height(dotSize)
                    .width(animatedWidth)
                    .clip(CircleShape)
                    .background(animatedColor)
                    .clickable(
                        role = Role.Button,
                        onClickLabel = "Go to panel ${index + 1}"
                    ) {
                        onSelectPage(index)
                    }
            )
        }
    }
}
