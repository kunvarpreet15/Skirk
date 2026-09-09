package com.kunvarpreet.skirk.domain.repository

import com.kunvarpreet.skirk.domain.model.Dashboard
import com.kunvarpreet.skirk.domain.model.Panel
import com.kunvarpreet.skirk.domain.model.WidgetConfig
import com.kunvarpreet.skirk.domain.model.WidgetInstance
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface defining dashboard persistence and state synchronization operations.
 * Independent of Android UI and persistence implementations.
 */
interface DashboardRepository {
    /**
     * Observes the active dashboard hierarchy including panels, slots, and widgets.
     */
    fun observeActiveDashboard(): Flow<Dashboard>

    /**
     * Retrieves the current dashboard snapshot.
     */
    suspend fun getDashboard(): Dashboard

    /**
     * Persists updates to the full dashboard.
     */
    suspend fun saveDashboard(dashboard: Dashboard)

    /**
     * Updates the active panel index (triggered by horizontal navigation).
     */
    suspend fun setActivePanel(panelIndex: Int)

    /**
     * Updates the active widget index for a specific slot within a panel
     * (triggered by vertical swipe navigation).
     */
    suspend fun setActiveWidget(panelId: String, slotIndex: Int, widgetIndex: Int)

    /**
     * Appends a new panel to the active dashboard.
     */
    suspend fun addPanel(panel: Panel)

    /**
     * Removes a panel by ID from the active dashboard.
     */
    suspend fun removePanel(panelId: String)

    /**
     * Reorders panels based on the ordered list of panel IDs.
     */
    suspend fun reorderPanels(panelIds: List<String>)

    /**
     * Adds a widget instance to a specific slot within a panel.
     */
    suspend fun addWidgetToSlot(panelId: String, slotId: String, widget: WidgetInstance)

    /**
     * Removes a widget instance from a specific slot.
     */
    suspend fun removeWidgetFromSlot(panelId: String, slotId: String, widgetId: String)

    /**
     * Reorders widgets within a specific slot.
     */
    suspend fun reorderWidgetsInSlot(panelId: String, slotId: String, widgetIds: List<String>)

    /**
     * Updates custom settings/configuration for a widget instance.
     */
    suspend fun updateWidgetConfig(instanceId: String, config: WidgetConfig)

    /**
     * Resets the dashboard to its factory default state.
     */
    suspend fun resetToDefault(): Dashboard
}
