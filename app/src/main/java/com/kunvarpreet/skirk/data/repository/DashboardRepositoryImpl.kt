package com.kunvarpreet.skirk.data.repository

import com.kunvarpreet.skirk.data.local.storage.JsonDashboardFileStorage
import com.kunvarpreet.skirk.domain.model.Dashboard
import com.kunvarpreet.skirk.domain.model.Panel
import com.kunvarpreet.skirk.domain.model.WidgetConfig
import com.kunvarpreet.skirk.domain.model.WidgetInstance
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

    override suspend fun addPanel(panel: Panel) {
        mutex.withLock {
            val current = _dashboardFlow.value
            val updated = current.addPanel(panel)
            _dashboardFlow.value = updated
            storage.saveDashboard(updated)
        }
    }

    override suspend fun removePanel(panelId: String) {
        mutex.withLock {
            val current = _dashboardFlow.value
            val updated = current.removePanel(panelId)
            _dashboardFlow.value = updated
            storage.saveDashboard(updated)
        }
    }

    override suspend fun reorderPanels(panelIds: List<String>) {
        mutex.withLock {
            val current = _dashboardFlow.value
            val updated = current.reorderPanels(panelIds)
            _dashboardFlow.value = updated
            storage.saveDashboard(updated)
        }
    }

    override suspend fun addWidgetToSlot(panelId: String, slotId: String, widget: WidgetInstance) {
        mutex.withLock {
            val current = _dashboardFlow.value
            val updated = current.updatePanel(panelId) { panel ->
                panel.updateSlotById(slotId) { slot ->
                    slot.addWidget(widget)
                }
            }
            _dashboardFlow.value = updated
            storage.saveDashboard(updated)
        }
    }

    override suspend fun removeWidgetFromSlot(panelId: String, slotId: String, widgetId: String) {
        mutex.withLock {
            val current = _dashboardFlow.value
            val updated = current.updatePanel(panelId) { panel ->
                panel.updateSlotById(slotId) { slot ->
                    slot.removeWidget(widgetId)
                }
            }
            _dashboardFlow.value = updated
            storage.saveDashboard(updated)
        }
    }

    override suspend fun reorderWidgetsInSlot(
        panelId: String,
        slotId: String,
        widgetIds: List<String>
    ) {
        mutex.withLock {
            val current = _dashboardFlow.value
            val updated = current.updatePanel(panelId) { panel ->
                panel.updateSlotById(slotId) { slot ->
                    slot.reorderWidgets(widgetIds)
                }
            }
            _dashboardFlow.value = updated
            storage.saveDashboard(updated)
        }
    }

    override suspend fun updateWidgetConfig(instanceId: String, config: WidgetConfig) {
        mutex.withLock {
            val current = _dashboardFlow.value
            val updated = current.copy(
                panels = current.panels.map { panel ->
                    panel.copy(
                        slots = panel.slots.map { slot ->
                            slot.copy(
                                widgets = slot.widgets.map { widget ->
                                    if (widget.id == instanceId) widget.copy(config = config) else widget
                                }
                            )
                        }
                    )
                }
            )
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
