package com.kunvarpreet.skirk.widget

import com.kunvarpreet.skirk.system.model.NetworkStatus
import com.kunvarpreet.skirk.system.model.NetworkType
import com.kunvarpreet.skirk.system.model.RamInfo
import com.kunvarpreet.skirk.system.model.StorageInfo
import com.kunvarpreet.skirk.system.model.SystemDashboardData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SystemModelsTest {

    @Test
    fun ramInfo_formatting_isCorrect() {
        val totalBytes = 16L * 1024 * 1024 * 1024 // 16 GB
        val usedBytes = 8L * 1024 * 1024 * 1024  // 8 GB
        val availBytes = 8L * 1024 * 1024 * 1024

        val ram = RamInfo(
            totalBytes = totalBytes,
            availableBytes = availBytes,
            usedBytes = usedBytes,
            usedPercentage = 50
        )

        assertEquals("16.0 GB", ram.totalGbFormatted)
        assertEquals("8.0 GB", ram.usedGbFormatted)
        assertEquals("8.0 GB", ram.availableGbFormatted)
        assertEquals(50, ram.usedPercentage)
    }

    @Test
    fun storageInfo_formatting_isCorrect() {
        val totalBytes = 256L * 1024 * 1024 * 1024 // 256 GB
        val usedBytes = 64L * 1024 * 1024 * 1024   // 64 GB
        val availBytes = 192L * 1024 * 1024 * 1024

        val storage = StorageInfo(
            totalBytes = totalBytes,
            availableBytes = availBytes,
            usedBytes = usedBytes,
            usedPercentage = 25
        )

        assertEquals("256 GB", storage.totalGbFormatted)
        assertEquals("64 GB", storage.usedGbFormatted)
        assertEquals("192 GB", storage.availableGbFormatted)
        assertEquals(25, storage.usedPercentage)
    }

    @Test
    fun sampleData_containsExpectedHardwareValues() {
        val sample = SystemDashboardData.sample()

        assertEquals(34, sample.ram.usedPercentage)
        assertEquals(36, sample.storage.usedPercentage)
        assertEquals(87, sample.battery.percentage)
        assertTrue(sample.battery.isCharging)
        assertTrue(sample.network.isConnected)
        assertEquals(NetworkType.WIFI, sample.network.type)
        assertEquals("Wi-Fi", sample.network.displayLabel)
        assertTrue(sample.processorCount > 0)
    }
}
