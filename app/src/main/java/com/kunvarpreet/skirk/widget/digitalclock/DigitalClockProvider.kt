package com.kunvarpreet.skirk.widget.digitalclock

import com.kunvarpreet.skirk.domain.model.WidgetCategory
import com.kunvarpreet.skirk.domain.model.WidgetDefinition
import com.kunvarpreet.skirk.domain.model.WidgetDesign
import com.kunvarpreet.skirk.widget.core.WidgetContentRenderer
import com.kunvarpreet.skirk.widget.core.WidgetProvider
import com.kunvarpreet.skirk.widget.model.WidgetTypeIds

/**
 * Production provider for the built-in Digital Clock widget.
 */
class DigitalClockProvider : WidgetProvider {

    private val renderer = DigitalClockRenderer()

    override val definition: WidgetDefinition = WidgetDefinition(
        id = WidgetTypeIds.DIGITAL_CLOCK,
        displayName = "Digital Clock",
        description = "Displays the current time digitally with customizable styles",
        category = WidgetCategory.TIME,
        availableDesigns = listOf(
            WidgetDesign(id = "large", displayName = "Large Bold"),
            WidgetDesign(id = "minimal", displayName = "Minimal"),
            WidgetDesign(id = "retro", displayName = "Retro Flip"),
            WidgetDesign(id = "modern", displayName = "Modern Neon")
        ),
        defaultDesignId = "large"
    )

    override fun getRenderer(designId: String): WidgetContentRenderer = renderer
}
