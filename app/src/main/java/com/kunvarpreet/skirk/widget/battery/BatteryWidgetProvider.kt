package com.kunvarpreet.skirk.widget.battery

import com.kunvarpreet.skirk.domain.model.WidgetCategory
import com.kunvarpreet.skirk.domain.model.WidgetDefinition
import com.kunvarpreet.skirk.domain.model.WidgetDesign
import com.kunvarpreet.skirk.widget.core.WidgetContentRenderer
import com.kunvarpreet.skirk.widget.core.WidgetProvider
import com.kunvarpreet.skirk.widget.model.WidgetTypeIds

/**
 * Production provider for the built-in Battery widget.
 */
class BatteryWidgetProvider(
    private val batteryInfoProvider: BatteryInfoProvider = SampleBatteryInfoProvider()
) : WidgetProvider {

    private val renderer = BatteryWidgetRenderer(batteryInfoProvider)

    override val definition: WidgetDefinition = WidgetDefinition(
        id = WidgetTypeIds.BATTERY,
        displayName = "Battery Info",
        description = "Charging status, percentage, and system power metrics",
        category = WidgetCategory.SYSTEM,
        availableDesigns = listOf(
            WidgetDesign(id = "ring", displayName = "Circular Ring"),
            WidgetDesign(id = "bar", displayName = "Horizontal Bar"),
            WidgetDesign(id = "minimal", displayName = "Minimal Capsule")
        ),
        defaultDesignId = "ring"
    )

    override fun getRenderer(designId: String): WidgetContentRenderer = renderer
}
