package com.kunvarpreet.skirk.domain.model

import kotlinx.serialization.Serializable
import java.util.UUID

/**
 * A single horizontally swipeable screen within a dashboard.
 * Contains a layout configuration and a list of widget slots conforming to that layout.
 */
@Serializable
data class Panel(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val layout: PanelLayout,
    val slots: List<WidgetSlot> = emptyList()
) {
    /**
     * Retrieves the slot at the given slot index, or null if out of bounds.
     */
    fun getSlot(index: Int): WidgetSlot? = slots.find { it.slotIndex == index }

    /**
     * Replaces or updates the slot at the specified slot index.
     */
    fun updateSlot(slotIndex: Int, transform: (WidgetSlot) -> WidgetSlot): Panel {
        val updatedSlots = slots.map { slot ->
            if (slot.slotIndex == slotIndex) transform(slot) else slot
        }
        return copy(slots = updatedSlots)
    }
}
