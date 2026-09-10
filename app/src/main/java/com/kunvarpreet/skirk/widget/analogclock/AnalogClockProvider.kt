package com.kunvarpreet.skirk.widget.analogclock

import com.kunvarpreet.skirk.domain.model.WidgetCategory
import com.kunvarpreet.skirk.domain.model.WidgetDefinition
import com.kunvarpreet.skirk.domain.model.WidgetDesign
import com.kunvarpreet.skirk.widget.core.WidgetContentRenderer
import com.kunvarpreet.skirk.widget.core.WidgetProvider
import com.kunvarpreet.skirk.widget.model.WidgetTypeIds

/**
 * Production provider for the built-in Analog Clock widget.
 */
class AnalogClockProvider : WidgetProvider {

    private val renderer = AnalogClockRenderer()

    override val definition: WidgetDefinition = WidgetDefinition(
        id = WidgetTypeIds.ANALOG_CLOCK,
        displayName = "Analog Clock",
        description = "Classic dial clock with hour, minute, and second hands",
        category = WidgetCategory.TIME,
        availableDesigns = listOf(
            WidgetDesign(id = "classic", displayName = "Classic"),
            WidgetDesign(id = "bauhaus", displayName = "Bauhaus"),
            WidgetDesign(id = "chronograph", displayName = "Chronograph")
        ),
        defaultDesignId = "classic"
    )

    override fun getRenderer(designId: String): WidgetContentRenderer = renderer
}
