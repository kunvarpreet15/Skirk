package com.kunvarpreet.skirk.domain.standby

import com.kunvarpreet.skirk.domain.power.ChargingSource
import com.kunvarpreet.skirk.domain.power.ChargingState
import com.kunvarpreet.skirk.domain.power.ChargingStateProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

private class FakeChargingStateProvider(initialState: ChargingState) : ChargingStateProvider {
    val flow = MutableStateFlow(initialState)
    override fun observeChargingState(): Flow<ChargingState> = flow
    override fun getCurrentChargingState(): ChargingState = flow.value
}

@OptIn(ExperimentalCoroutinesApi::class)
class StandByControllerTest {

    @Test
    fun chargingStarts_transitionsToEnteringAndEmitsLaunchCommand() = runTest {
        val fakeProvider = FakeChargingStateProvider(ChargingState.Disconnected)
        val controller = StandByController(
            chargingStateProvider = fakeProvider,
            scope = backgroundScope
        )

        assertEquals(StandByState.INACTIVE, controller.standByState.value)

        // Device starts charging from AC
        fakeProvider.flow.value = ChargingState(isCharging = true, source = ChargingSource.AC)
        advanceTimeBy(350) // pass debounce
        runCurrent()

        assertEquals(StandByState.ENTERING, controller.standByState.value)
        val command = controller.commands.first()
        assertEquals(StandByCommand.LaunchStandByActivity, command)

        // Activity signals it is active
        controller.notifyStandByActivityActive()
        assertEquals(StandByState.ACTIVE, controller.standByState.value)

        // Charger disconnected
        fakeProvider.flow.value = ChargingState.Disconnected
        advanceTimeBy(350) // pass debounce
        runCurrent()

        assertEquals(StandByState.EXITING, controller.standByState.value)

        // Activity signals finish
        controller.notifyStandByActivityFinished()
        assertEquals(StandByState.INACTIVE, controller.standByState.value)
    }

    @Test
    fun manualEntry_allowsStandByWithoutCharging() = runTest {
        val fakeProvider = FakeChargingStateProvider(ChargingState.Disconnected)
        val controller = StandByController(
            chargingStateProvider = fakeProvider,
            scope = backgroundScope
        )

        assertEquals(StandByState.INACTIVE, controller.standByState.value)

        // Trigger manual entry
        controller.enterStandByManually()
        assertEquals(StandByState.ENTERING, controller.standByState.value)

        controller.notifyStandByActivityActive()
        assertEquals(StandByState.ACTIVE, controller.standByState.value)

        // Request exit manually
        controller.exitStandBy()
        assertEquals(StandByState.EXITING, controller.standByState.value)

        controller.notifyStandByActivityFinished()
        assertEquals(StandByState.INACTIVE, controller.standByState.value)
    }
}
