package com.kunvarpreet.skirk.data.repository

import com.kunvarpreet.skirk.data.local.storage.JsonDashboardFileStorage
import com.kunvarpreet.skirk.domain.model.Dashboard
import com.kunvarpreet.skirk.domain.repository.DashboardRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Concrete implementation of [DashboardRepository] backed by local persistent file storage.
 * Maintains an in-memory [MutableStateFlow] for hot reactive UI updates.
 */
class DashboardRepositoryImpl(
    private val storage: JsonDashboardFileStorage,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) : DashboardRepository {

    private val mutex = Mutex()
    private val _dashboardFlow = MutableStateFlow(DefaultDashboardFactory.createDefaultDashboard())

    init {
        scope.launch {
            val persisted = storage.loadDashboard()
            if (persisted != null) {
                _dashboardFlow.value = persisted
            } else {
                val defaultDashboard = DefaultDashboardFactory.createDefaultDashboard()
                storage.saveDashboard(defaultDashboard)
                _dashboardFlow.value = defaultDashboard
            }
        }
    }

    override fun observeActiveDashboard(): Flow<Dashboard> = _dashboardFlow.asStateFlow()

    override suspend fun getDashboard(): Dashboard = _dashboardFlow.value

    override suspend fun saveDashboard(dashboard: Dashboard) {
        mutex.withLock {
            _dashboardFlow.value = dashboard
            storage.saveDashboard(dashboard)
        }
    }

    override suspend fun setActivePanel(panelIndex: Int) {
        mutex.withLock {
            val current = _dashboardFlow.value
            val updated = current.withActivePanelIndex(panelIndex)
            _dashboardFlow.value = updated
            storage.saveDashboard(updated)
        }
    }

    override suspend fun setActiveWidget(panelId: String, slotIndex: Int, widgetIndex: Int) {
        mutex.withLock {
            val current = _dashboardFlow.value
            val updated = current.updatePanel(panelId) { panel ->
                panel.updateSlot(slotIndex) { slot ->
                    slot.withActiveWidgetIndex(widgetIndex)
                }
            }
            _dashboardFlow.value = updated
            storage.saveDashboard(updated)
        }
    }

    override suspend fun resetToDefault(): Dashboard {
        return mutex.withLock {
            val defaultDashboard = DefaultDashboardFactory.createDefaultDashboard()
            storage.saveDashboard(defaultDashboard)
            _dashboardFlow.value = defaultDashboard
            defaultDashboard
        }
    }
}
