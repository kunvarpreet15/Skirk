package com.kunvarpreet.skirk.domain.model

import kotlinx.serialization.Serializable

/**
 * Defines the layout structure of a panel.
 * Specifies the arrangement and total number of available widget slots.
 */
@Serializable
sealed interface PanelLayout {
    val id: String
    val displayName: String
    val slotCount: Int

    @Serializable
    data object Single : PanelLayout {
        override val id: String = "layout_single"
        override val displayName: String = "Single Widget"
        override val slotCount: Int = 1
    }

    @Serializable
    data object TwoSplitHorizontal : PanelLayout {
        override val id: String = "layout_two_split_h"
        override val displayName: String = "Two Columns (Side-by-Side)"
        override val slotCount: Int = 2
    }

    @Serializable
    data object TwoSplitVertical : PanelLayout {
        override val id: String = "layout_two_split_v"
        override val displayName: String = "Two Rows (Stacked)"
        override val slotCount: Int = 2
    }

    @Serializable
    data object Grid4 : PanelLayout {
        override val id: String = "layout_grid_4"
        override val displayName: String = "Four Widgets (2x2 Grid)"
        override val slotCount: Int = 4
    }

    @Serializable
    data object Grid6 : PanelLayout {
        override val id: String = "layout_grid_6"
        override val displayName: String = "Six Widgets (3x2 Grid)"
        override val slotCount: Int = 6
    }

    @Serializable
    data class Custom(
        override val id: String,
        override val displayName: String,
        override val slotCount: Int
    ) : PanelLayout

    companion object {
        val standardLayouts: List<PanelLayout> = listOf(
            Single,
            TwoSplitHorizontal,
            TwoSplitVertical,
            Grid4,
            Grid6
        )

        fun fromId(id: String): PanelLayout {
            return standardLayouts.find { it.id == id } ?: Single
        }
    }
}
