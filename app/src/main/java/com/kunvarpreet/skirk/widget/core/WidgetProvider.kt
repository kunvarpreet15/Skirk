package com.kunvarpreet.skirk.widget.core

import com.kunvarpreet.skirk.domain.model.WidgetDefinition

/**
 * Self-contained provider for a distinct widget type.
 * Encapsulates the widget's metadata definition and maps design IDs to visual renderers.
 *
 * This provider pattern ensures:
 * 1. New widgets can be created by implementing [WidgetProvider] and registering it in [WidgetRegistry].
 * 2. New visual designs can be added to an existing widget by registering additional renderers.
 * 3. The dashboard engine has zero compile-time coupling to any individual widget implementation.
 */
interface WidgetProvider {
    val definition: WidgetDefinition

    /**
     * Resolves the visual renderer corresponding to the requested design ID.
     */
    fun getRenderer(designId: String): WidgetContentRenderer
}
