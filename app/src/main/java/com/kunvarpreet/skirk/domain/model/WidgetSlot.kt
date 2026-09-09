package com.kunvarpreet.skirk.domain.model

import kotlinx.serialization.Serializable

/**
 * A dedicated slot/region within a panel.
 * Holds an ordered stack (list) of widgets. Vertical swiping inside this slot
 * alternates between widgets in the stack.
 */
@Serializable
data class WidgetSlot(
    val slotIndex: Int,
    val id: String = "slot_$slotIndex",
    val widgets: List<WidgetInstance> = emptyList(),
    val activeWidgetIndex: Int = 0
) {
    val activeWidget: WidgetInstance?
        get() = if (widgets.isNotEmpty() && activeWidgetIndex in widgets.indices) {
            widgets[activeWidgetIndex]
        } else if (widgets.isNotEmpty()) {
            widgets[0]
        } else {
            null
        }

    fun withActiveWidgetIndex(index: Int): WidgetSlot {
        if (widgets.isEmpty()) return copy(activeWidgetIndex = 0)
        val clampedIndex = index.coerceIn(0, widgets.lastIndex)
        return copy(activeWidgetIndex = clampedIndex)
    }

    fun nextWidget(): WidgetSlot {
        if (widgets.size <= 1) return this
        val next = (activeWidgetIndex + 1) % widgets.size
        return copy(activeWidgetIndex = next)
    }

    fun previousWidget(): WidgetSlot {
        if (widgets.size <= 1) return this
        val prev = if (activeWidgetIndex - 1 < 0) widgets.size - 1 else activeWidgetIndex - 1
        return copy(activeWidgetIndex = prev)
    }

    fun addWidget(widget: WidgetInstance): WidgetSlot {
        val updated = widgets + widget
        return copy(widgets = updated)
    }

    fun removeWidget(widgetId: String): WidgetSlot {
        val updated = widgets.filterNot { it.id == widgetId }
        val newActiveIndex = if (updated.isEmpty()) 0 else activeWidgetIndex.coerceIn(0, updated.lastIndex)
        return copy(widgets = updated, activeWidgetIndex = newActiveIndex)
    }

    fun reorderWidgets(newOrder: List<String>): WidgetSlot {
        val map = widgets.associateBy { it.id }
        val reordered = newOrder.mapNotNull { map[it] } + widgets.filterNot { it.id in newOrder }
        val currentActiveId = activeWidget?.id
        val newActiveIndex = reordered.indexOfFirst { it.id == currentActiveId }.let {
            if (it >= 0) it else 0
        }
        return copy(widgets = reordered, activeWidgetIndex = newActiveIndex)
    }

    fun selectWidgetById(widgetId: String): WidgetSlot {
        val index = widgets.indexOfFirst { it.id == widgetId }
        return if (index >= 0) withActiveWidgetIndex(index) else this
    }
}
