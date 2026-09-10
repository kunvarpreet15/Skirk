package com.kunvarpreet.skirk.widget.digitalclock

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.kunvarpreet.skirk.domain.model.WidgetDesign
import com.kunvarpreet.skirk.domain.model.WidgetInstance
import com.kunvarpreet.skirk.widget.common.rememberCurrentTime
import com.kunvarpreet.skirk.widget.core.WidgetContentRenderer

/**
 * Production renderer for Digital Clock widgets.
 * Resolves instance configuration, observes lifecycle-aware time updates,
 * and delegates to the designated visual design.
 */
class DigitalClockRenderer : WidgetContentRenderer {

    @Composable
    override fun Render(
        instance: WidgetInstance,
        design: WidgetDesign,
        modifier: Modifier
    ) {
        val context = LocalContext.current
        val config = DigitalClockConfig.from(instance.config, context)
        val timeSnapshot by rememberCurrentTime(includeSeconds = config.showSeconds)

        when (design.id) {
            "minimal" -> MinimalDigitalClock(
                timeSnapshot = timeSnapshot,
                config = config,
                modifier = modifier
            )
            "modern" -> ModernDigitalClock(
                timeSnapshot = timeSnapshot,
                config = config,
                modifier = modifier
            )
            else -> LargeDigitalClock( // "large" or default fallback
                timeSnapshot = timeSnapshot,
                config = config,
                modifier = modifier
            )
        }
    }
}
