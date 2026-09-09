package com.kunvarpreet.skirk.domain.model

import kotlinx.serialization.Serializable
import java.util.UUID

/**
 * A single horizontally swipeable screen within a dashboard.
 * Contains a layout configuration and an ordered list of widget slots conforming to that layout.
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
     * Retrieves the slot by its unique ID.
     */
    fun getSlotById(slotId: String): WidgetSlot? = slots.find { it.id == slotId }

    /**
     * Replaces or updates the slot at the specified slot index.
     */
    fun updateSlot(slotIndex: Int, transform: (WidgetSlot) -> WidgetSlot): Panel {
        val updatedSlots = slots.map { slot ->
            if (slot.slotIndex == slotIndex) transform(slot) else slot
        }
        return copy(slots = updatedSlots)
    }

    /**
     * Replaces or updates the slot with the specified slot ID.
     */
    fun updateSlotById(slotId: String, transform: (WidgetSlot) -> WidgetSlot): Panel {
        val updatedSlots = slots.map { slot ->
            if (slot.id == slotId) transform(slot) else slot
        }
        return copy(slots = updatedSlots)
    }

    /**
     * Appends a slot to the panel.
     */
    fun addSlot(slot: WidgetSlot): Panel {
        return copy(slots = slots + slot)
    }

    /**
     * Removes a slot from the panel by its ID.
     */
    fun removeSlot(slotId: String): Panel {
        val filtered = slots.filterNot { it.id == slotId }
        val reindexed = filtered.mapIndexed { index, slot -> slot.copy(slotIndex = index) }
        return copy(slots = reindexed)
    }
}
