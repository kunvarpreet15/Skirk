package com.kunvarpreet.skirk.domain.repository

import com.kunvarpreet.skirk.domain.model.Dashboard
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
     * Resets the dashboard to its factory default state.
     */
    suspend fun resetToDefault(): Dashboard
}
