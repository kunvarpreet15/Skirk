package com.kunvarpreet.skirk.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kunvarpreet.skirk.domain.model.Dashboard
import com.kunvarpreet.skirk.domain.repository.DashboardRepository
import com.kunvarpreet.skirk.domain.repository.UserSettings
import com.kunvarpreet.skirk.domain.repository.UserSettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DashboardUiState(
    val isLoading: Boolean = false,
    val dashboard: Dashboard? = null,
    val userSettings: UserSettings = UserSettings(),
    val errorMessage: String? = null
)

class DashboardViewModel(
    private val dashboardRepository: DashboardRepository,
    private val userSettingsRepository: UserSettingsRepository
) : ViewModel() {

    val uiState: StateFlow<DashboardUiState> = combine(
        dashboardRepository.observeActiveDashboard(),
        userSettingsRepository.observeSettings()
    ) { dashboard, settings ->
        DashboardUiState(
            isLoading = false,
            dashboard = dashboard,
            userSettings = settings
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardUiState(isLoading = true)
    )

    fun onSelectPanel(index: Int) {
        viewModelScope.launch {
            dashboardRepository.setActivePanel(index)
        }
    }

    fun onNextPanel() {
        val current = uiState.value.dashboard ?: return
        val nextIndex = (current.activePanelIndex + 1) % current.panels.size
        onSelectPanel(nextIndex)
    }

    fun onPreviousPanel() {
        val current = uiState.value.dashboard ?: return
        val prevIndex = if (current.activePanelIndex - 1 < 0) current.panels.size - 1 else current.activePanelIndex - 1
        onSelectPanel(prevIndex)
    }

    fun onNextWidgetInSlot(slotIndex: Int) {
        val current = uiState.value.dashboard ?: return
        val activePanel = current.activePanel ?: return
        val slot = activePanel.getSlot(slotIndex) ?: return
        if (slot.widgets.size <= 1) return
        val nextWidgetIndex = (slot.activeWidgetIndex + 1) % slot.widgets.size
        viewModelScope.launch {
            dashboardRepository.setActiveWidget(activePanel.id, slotIndex, nextWidgetIndex)
        }
    }

    fun onPreviousWidgetInSlot(slotIndex: Int) {
        val current = uiState.value.dashboard ?: return
        val activePanel = current.activePanel ?: return
        val slot = activePanel.getSlot(slotIndex) ?: return
        if (slot.widgets.size <= 1) return
        val prevWidgetIndex = if (slot.activeWidgetIndex - 1 < 0) slot.widgets.size - 1 else slot.activeWidgetIndex - 1
        viewModelScope.launch {
            dashboardRepository.setActiveWidget(activePanel.id, slotIndex, prevWidgetIndex)
        }
    }

    fun onResetToDefault() {
        viewModelScope.launch {
            dashboardRepository.resetToDefault()
        }
    }

    companion object {
        fun provideFactory(
            dashboardRepository: DashboardRepository,
            userSettingsRepository: UserSettingsRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return DashboardViewModel(dashboardRepository, userSettingsRepository) as T
            }
        }
    }
}
