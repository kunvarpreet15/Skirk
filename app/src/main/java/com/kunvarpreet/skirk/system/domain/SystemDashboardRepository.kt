package com.kunvarpreet.skirk.system.domain

import com.kunvarpreet.skirk.system.model.SystemDashboardData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Domain repository abstraction for observing device hardware and connectivity state.
 */
interface SystemDashboardRepository {
    /**
     * Emits reactive hardware and system statistics.
     */
    fun observeSystemDashboard(): Flow<SystemDashboardData>
}

/**
 * Deterministic mock repository for Compose Previews and unit tests.
 */
class MockSystemDashboardRepository(
    initialData: SystemDashboardData = SystemDashboardData.sample()
) : SystemDashboardRepository {

    private val _data = MutableStateFlow(initialData)

    override fun observeSystemDashboard(): Flow<SystemDashboardData> = _data.asStateFlow()

    fun updateData(data: SystemDashboardData) {
        _data.value = data
    }
}
