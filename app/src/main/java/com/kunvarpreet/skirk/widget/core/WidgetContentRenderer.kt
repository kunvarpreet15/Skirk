package com.kunvarpreet.skirk.widget.core

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kunvarpreet.skirk.domain.model.WidgetDesign
import com.kunvarpreet.skirk.domain.model.WidgetInstance

/**
 * Renders the visual Compose UI for a widget instance using a specific visual design.
 * Widgets provide their own implementations without coupling to the main dashboard container.
 */
fun interface WidgetContentRenderer {
    @Composable
    fun Render(
        instance: WidgetInstance,
        design: WidgetDesign,
        modifier: Modifier
    )
}
