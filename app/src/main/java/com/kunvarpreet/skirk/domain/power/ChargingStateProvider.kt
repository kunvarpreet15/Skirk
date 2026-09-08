package com.kunvarpreet.skirk.domain.power

import kotlinx.coroutines.flow.Flow

/**
 * Interface providing access to real-time device power and battery charging status.
 * Decouples the application domain from Android broadcast/battery APIs.
 */
interface ChargingStateProvider {
    /**
     * Observes real-time power connection and battery status changes.
     */
    fun observeChargingState(): Flow<ChargingState>

    /**
     * Retrieves the instantaneous charging state synchronously.
     */
    fun getCurrentChargingState(): ChargingState
}
