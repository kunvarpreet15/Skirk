package com.kunvarpreet.skirk.domain.power

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ChargingStateTest {

    @Test
    fun disconnectedState_hasNoneSourceAndNotCharging() {
        val state = ChargingState.Disconnected
        assertFalse(state.isCharging)
        assertEquals(ChargingSource.NONE, state.source)
    }

    @Test
    fun chargingSources_haveDescriptiveDisplayNames() {
        assertEquals("AC Wall Charger", ChargingSource.AC.displayName)
        assertEquals("USB Port", ChargingSource.USB.displayName)
        assertEquals("Wireless Charger", ChargingSource.WIRELESS.displayName)
        assertEquals("Dock", ChargingSource.DOCK.displayName)
        assertEquals("Other Power Source", ChargingSource.OTHER.displayName)
    }

    @Test
    fun chargingState_creation() {
        val acCharging = ChargingState(isCharging = true, source = ChargingSource.AC)
        assertTrue(acCharging.isCharging)
        assertEquals(ChargingSource.AC, acCharging.source)

        val usbCharging = ChargingState(isCharging = true, source = ChargingSource.USB)
        assertTrue(usbCharging.isCharging)
        assertEquals(ChargingSource.USB, usbCharging.source)
    }
}
