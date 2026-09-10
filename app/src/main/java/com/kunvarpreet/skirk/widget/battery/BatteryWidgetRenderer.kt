package com.kunvarpreet.skirk.widget.battery

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.kunvarpreet.skirk.domain.model.WidgetDesign
import com.kunvarpreet.skirk.domain.model.WidgetInstance
import com.kunvarpreet.skirk.widget.core.WidgetContentRenderer

/**
 * Production renderer for Battery widgets.
 * Observes real-time event-driven battery state and renders the chosen battery design.
 */
class BatteryWidgetRenderer(
    private val batteryInfoProvider: BatteryInfoProvider
) : WidgetContentRenderer {

    @Composable
    override fun Render(
        instance: WidgetInstance,
        design: WidgetDesign,
        modifier: Modifier
    ) {
        val batteryInfo by batteryInfoProvider
            .observeBatteryInfo()
            .collectAsState(initial = batteryInfoProvider.getCurrentBatteryInfo())

        val config = BatteryWidgetConfig.from(instance.config)

        when (design.id) {
            "bar" -> BarBatteryDesign(
                batteryInfo = batteryInfo,
                config = config,
                modifier = modifier
            )
            "minimal" -> MinimalBatteryDesign(
                batteryInfo = batteryInfo,
                config = config,
                modifier = modifier
            )
            else -> RingBatteryDesign( // "ring" or default fallback
                batteryInfo = batteryInfo,
                config = config,
                modifier = modifier
            )
        }
    }
}
