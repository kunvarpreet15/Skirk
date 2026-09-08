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

    fun withActivePanelIndex(index: Int): Dashboard {
        if (panels.isEmpty()) return this
        val clamped = index.coerceIn(0, panels.lastIndex)
        return copy(activePanelIndex = clamped)
    }

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
}
