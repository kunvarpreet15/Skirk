package com.kunvarpreet.skirk.domain.model

import kotlinx.serialization.Serializable
import java.util.UUID

/**
 * Root container for all dashboard panels and user configuration.
 */
@Serializable
data class Dashboard(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "Default Dashboard",
    val schemaVersion: Int = 1,
    val panels: List<Panel> = emptyList(),
    val activePanelIndex: Int = 0
) {
    val activePanel: Panel?
        get() = if (panels.isNotEmpty() && activePanelIndex in panels.indices) {
            panels[activePanelIndex]
        } else if (panels.isNotEmpty()) {
            panels[0]
        } else {
            null
        }

    fun getPanel(panelId: String): Panel? = panels.find { it.id == panelId }

    fun withActivePanelIndex(index: Int): Dashboard {
        if (panels.isEmpty()) return copy(activePanelIndex = 0)
        val clamped = index.coerceIn(0, panels.lastIndex)
        return copy(activePanelIndex = clamped)
    }

    fun selectPanel(index: Int): Dashboard = withActivePanelIndex(index)

    fun nextPanel(): Dashboard {
        if (panels.size <= 1) return this
        return copy(activePanelIndex = (activePanelIndex + 1) % panels.size)
    }

    fun previousPanel(): Dashboard {
        if (panels.size <= 1) return this
        val prev = if (activePanelIndex - 1 < 0) panels.size - 1 else activePanelIndex - 1
        return copy(activePanelIndex = prev)
    }

    fun updatePanel(panelId: String, transform: (Panel) -> Panel): Dashboard {
        val updated = panels.map { panel ->
            if (panel.id == panelId) transform(panel) else panel
        }
        return copy(panels = updated)
    }

    fun addPanel(panel: Panel): Dashboard {
        return copy(panels = panels + panel)
    }

    fun removePanel(panelId: String): Dashboard {
        val updated = panels.filterNot { it.id == panelId }
        val newIndex = if (updated.isEmpty()) 0 else activePanelIndex.coerceIn(0, updated.lastIndex)
        return copy(panels = updated, activePanelIndex = newIndex)
    }

    fun reorderPanels(orderedPanelIds: List<String>): Dashboard {
        val panelMap = panels.associateBy { it.id }
        val reordered = orderedPanelIds.mapNotNull { panelMap[it] } + panels.filterNot { it.id in orderedPanelIds }
        val currentActivePanelId = activePanel?.id
        val newIndex = reordered.indexOfFirst { it.id == currentActivePanelId }.let {
            if (it >= 0) it else 0
        }
        return copy(panels = reordered, activePanelIndex = newIndex)
    }
}
