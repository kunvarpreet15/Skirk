package com.kunvarpreet.skirk.presentation.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kunvarpreet.skirk.domain.model.Dashboard
import com.kunvarpreet.skirk.presentation.dashboard.indicator.StandByPageIndicator
import com.kunvarpreet.skirk.presentation.dashboard.layout.PanelLayoutContainer
import com.kunvarpreet.skirk.widget.core.WidgetRegistry
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

/**
 * Interactive model-driven dashboard rendering engine.
 *
 * Capabilities:
 * - Horizontal panel swiping using Compose [HorizontalPager].
 * - Bidirectional synchronization between pager state and [Dashboard.activePanelIndex].
 * - Smooth StandBy page transitions and subtle [StandByPageIndicator].
 * - Accessibility actions for next/previous panel navigation.
 * - Used identically in normal application mode and inside the StandBy runtime.
 */
@Composable
fun DashboardView(
    modifier: Modifier = Modifier,
    dashboard: Dashboard?,
    widgetRegistry: WidgetRegistry,
    onNextPanel: () -> Unit,
    onPreviousPanel: () -> Unit,
    onSelectPanel: (index: Int) -> Unit,
    onNextWidgetInSlot: (slotIndex: Int) -> Unit,
    onPreviousWidgetInSlot: (slotIndex: Int) -> Unit,
    onResetToDefault: () -> Unit,
    showDebugControls: Boolean = false,
    showPanelControls: Boolean = showDebugControls
) {
    if (dashboard == null || dashboard.panels.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "No Panels Configured",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF94A3B8)
                )
                Text(
                    text = "The dashboard configuration is currently empty.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF64748B)
                )
                Button(onClick = onResetToDefault) {
                    Text("Restore Default Dashboard")
                }
            }
        }
        return
    }

    val coroutineScope = rememberCoroutineScope()
    val panelCount = dashboard.panels.size
    val initialPage = dashboard.activePanelIndex.coerceIn(0, (panelCount - 1).coerceAtLeast(0))

    val pagerState = rememberPagerState(
        initialPage = initialPage,
        pageCount = { panelCount }
    )

    // Sync Pager swipes -> Dashboard State (persisted via onSelectPanel)
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }
            .distinctUntilChanged()
            .collect { page ->
                if (page != dashboard.activePanelIndex && page in dashboard.panels.indices) {
                    onSelectPanel(page)
                }
            }
    }

    // Sync external Dashboard State updates -> Pager scroll position
    LaunchedEffect(dashboard.activePanelIndex) {
        if (pagerState.currentPage != dashboard.activePanelIndex &&
            dashboard.activePanelIndex in dashboard.panels.indices) {
            pagerState.animateScrollToPage(dashboard.activePanelIndex)
        }
    }

    val canGoNext = pagerState.currentPage < panelCount - 1
    val canGoPrev = pagerState.currentPage > 0

    Column(
        modifier = modifier
            .fillMaxSize()
            .semantics {
                val actions = mutableListOf<CustomAccessibilityAction>()
                if (canGoNext) {
                    actions.add(CustomAccessibilityAction("Next Panel") {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                        true
                    })
                }
                if (canGoPrev) {
                    actions.add(CustomAccessibilityAction("Previous Panel") {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage - 1)
                        }
                        true
                    })
                }
                customActions = actions
            }
    ) {
        // Horizontal Pager for Swipeable Dashboard Panels
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            HorizontalPager(
                state = pagerState,
                userScrollEnabled = panelCount > 1,
                modifier = Modifier.fillMaxSize()
            ) { pageIndex ->
                val panel = dashboard.panels.getOrNull(pageIndex)
                if (panel != null) {
                    PanelLayoutContainer(
                        panel = panel,
                        widgetRegistry = widgetRegistry,
                        onNextWidgetInSlot = onNextWidgetInSlot,
                        onPrevWidgetInSlot = onPreviousWidgetInSlot,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }

        // Clean StandBy Page Indicator
        if (panelCount > 1) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                StandByPageIndicator(
                    pageCount = panelCount,
                    currentPage = pagerState.currentPage,
                    onSelectPage = { targetPage ->
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(targetPage)
                        }
                    }
                )
            }
        }

        // Optional Developer Debug Navigation Bar (hidden by default)
        if (showDebugControls && panelCount > 1) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                color = Color.Black.copy(alpha = 0.5f),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    FilledTonalButton(
                        onClick = onPreviousPanel,
                        shape = RoundedCornerShape(8.dp),
                        enabled = canGoPrev
                    ) {
                        Text("‹ Prev Panel", fontSize = 12.sp)
                    }

                    val currentPanelName = dashboard.panels.getOrNull(pagerState.currentPage)?.name ?: ""
                    Text(
                        text = currentPanelName,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFF1F5F9)
                    )

                    FilledTonalButton(
                        onClick = onNextPanel,
                        shape = RoundedCornerShape(8.dp),
                        enabled = canGoNext
                    ) {
                        Text("Next Panel ›", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
