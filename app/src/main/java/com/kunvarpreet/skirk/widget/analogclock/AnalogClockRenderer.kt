package com.kunvarpreet.skirk.widget.analogclock

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.kunvarpreet.skirk.domain.model.WidgetDesign
import com.kunvarpreet.skirk.domain.model.WidgetInstance
import com.kunvarpreet.skirk.widget.common.rememberCurrentTime
import com.kunvarpreet.skirk.widget.core.WidgetContentRenderer

/**
 * Production renderer for Analog Clock widgets.
 * Observes lifecycle-aware time updates and renders the selected analog dial design.
 */
class AnalogClockRenderer : WidgetContentRenderer {

    @Composable
    override fun Render(
        instance: WidgetInstance,
        design: WidgetDesign,
        modifier: Modifier
    ) {
        val config = AnalogClockConfig.from(instance.config)
        val timeSnapshot by rememberCurrentTime(includeSeconds = config.showSeconds)

        when (design.id) {
            "bauhaus" -> BauhausAnalogClock(
                timeSnapshot = timeSnapshot,
                config = config,
                modifier = modifier
            )
            "chronograph" -> ChronographAnalogClock(
                timeSnapshot = timeSnapshot,
                config = config,
                modifier = modifier
            )
            else -> ClassicAnalogClock( // "classic" or default fallback
                timeSnapshot = timeSnapshot,
                config = config,
                modifier = modifier
            )
        }
    }
}
