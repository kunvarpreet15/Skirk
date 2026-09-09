package com.kunvarpreet.skirk.domain.standby

import com.kunvarpreet.skirk.domain.power.ChargingState
import com.kunvarpreet.skirk.domain.power.ChargingStateProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

sealed interface StandByCommand {
    data object LaunchStandByActivity : StandByCommand
    data object FinishStandByActivity : StandByCommand
}

/**
 * Central controller coordinating StandBy lifecycle transitions based on
 * charging events and user manual triggers.
 */
@OptIn(FlowPreview::class)
class StandByController(
    private val chargingStateProvider: ChargingStateProvider,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main)
) {
    private val _standByState = MutableStateFlow(StandByState.INACTIVE)
    val standByState: StateFlow<StandByState> = _standByState.asStateFlow()

    private val _chargingState = MutableStateFlow(chargingStateProvider.getCurrentChargingState())
    val chargingState: StateFlow<ChargingState> = _chargingState.asStateFlow()

    private val _commands = MutableSharedFlow<StandByCommand>(replay = 1, extraBufferCapacity = 10)
    val commands: SharedFlow<StandByCommand> = _commands.asSharedFlow()

    private var isManualSession: Boolean = false
    private val observerJob = scope.launch {
        chargingStateProvider.observeChargingState()
            .debounce(300L) // Prevent thrashing from flickering or dirty charging connections
            .collect { state ->
                _chargingState.value = state
                handleChargingStateChange(state)
            }
    }

    /**
     * Stops background charging observation.
     */
    fun stop() {
        observerJob.cancel()
    }

    private fun handleChargingStateChange(state: ChargingState) {
        if (state.isCharging) {
            if (_standByState.value == StandByState.INACTIVE) {
                isManualSession = false
                enterStandBy()
            }
        } else {
            // Charging stopped
            if (!isManualSession && (_standByState.value == StandByState.ACTIVE || _standByState.value == StandByState.ENTERING)) {
                exitStandBy()
            }
        }
    }

    /**
     * Manually request entry into StandBy mode (useful for developer testing or user preview).
     */
    fun enterStandByManually() {
        isManualSession = true
        enterStandBy()
    }

    private fun enterStandBy() {
        if (_standByState.value == StandByState.ACTIVE || _standByState.value == StandByState.ENTERING) {
            return
        }
        _standByState.value = StandByState.ENTERING
        _commands.tryEmit(StandByCommand.LaunchStandByActivity)
    }

    /**
     * Request exit from StandBy mode.
     */
    fun exitStandBy() {
        if (_standByState.value == StandByState.INACTIVE || _standByState.value == StandByState.EXITING) {
            return
        }
        _standByState.value = StandByState.EXITING
        _commands.tryEmit(StandByCommand.FinishStandByActivity)
    }

    /**
     * Called by StandByActivity when onStart or onResume occurs to confirm the UI is active.
     */
    fun notifyStandByActivityActive() {
        if (_standByState.value != StandByState.EXITING) {
            _standByState.value = StandByState.ACTIVE
        }
    }

    /**
     * Called by StandByActivity when onDestroy occurs to finalize transition to INACTIVE.
     */
    fun notifyStandByActivityFinished() {
        isManualSession = false
        _standByState.value = StandByState.INACTIVE
    }
}
