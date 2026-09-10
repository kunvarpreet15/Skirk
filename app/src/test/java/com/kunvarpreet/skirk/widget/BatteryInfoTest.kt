package com.kunvarpreet.skirk.widget

import com.kunvarpreet.skirk.domain.power.ChargingSource
import com.kunvarpreet.skirk.widget.battery.BatteryInfo
import com.kunvarpreet.skirk.widget.battery.SampleBatteryInfoProvider
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BatteryInfoTest {

    @Test
    fun defaultBatteryInfo_hasSensibleDefaults() {
        val info = BatteryInfo()
        assertEquals(100, info.percentage)
        assertFalse(info.isCharging)
        assertFalse(info.isFull)
        assertEquals(ChargingSource.NONE, info.source)
        assertEquals(25.0f, info.temperatureCelsius, 0.01f)
        assertEquals("Good", info.health)
    }

    @Test
    fun sampleBatteryInfo_hasExpectedSampleData() {
        val sample = BatteryInfo.Sample
        assertEquals(87, sample.percentage)
        assertTrue(sample.isCharging)
        assertFalse(sample.isFull)
        assertEquals(ChargingSource.AC, sample.source)
        assertEquals(31.5f, sample.temperatureCelsius, 0.01f)
        assertEquals("Good", sample.health)
    }

    @Test
    fun sampleBatteryInfoProvider_emitsCurrentAndFlow() = runBlocking {
        val custom = BatteryInfo(
            percentage = 55,
            isCharging = true,
            source = ChargingSource.WIRELESS
        )
        val provider = SampleBatteryInfoProvider(custom)

        assertEquals(custom, provider.getCurrentBatteryInfo())
        val emitted = provider.observeBatteryInfo().first()
        assertEquals(custom, emitted)
    }
}
