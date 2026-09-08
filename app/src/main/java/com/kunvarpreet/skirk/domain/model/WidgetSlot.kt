package com.kunvarpreet.skirk.domain.model

import kotlinx.serialization.Serializable

/**
 * A dedicated slot/region within a panel.
 * Holds a stack (list) of widgets. Vertical swiping inside this slot
 * alternates between widgets in the stack.
 */
@Serializable
data class WidgetSlot(
    val slotIndex: Int,
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
        if (widgets.isEmpty()) return this
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
}
