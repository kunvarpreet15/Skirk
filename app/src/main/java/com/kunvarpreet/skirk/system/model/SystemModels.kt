package com.kunvarpreet.skirk.system.model

import com.kunvarpreet.skirk.widget.battery.BatteryInfo
import java.util.Locale

/**
 * Memory utilization reported via [android.app.ActivityManager.MemoryInfo].
 */
data class RamInfo(
    val totalBytes: Long = 0L,
    val availableBytes: Long = 0L,
    val usedBytes: Long = 0L,
    val usedPercentage: Int = 0
) {
    val totalGbFormatted: String
        get() = String.format(Locale.US, "%.1f GB", totalBytes.toDouble() / (1024 * 1024 * 1024))

    val usedGbFormatted: String
        get() = String.format(Locale.US, "%.1f GB", usedBytes.toDouble() / (1024 * 1024 * 1024))

    val availableGbFormatted: String
        get() = String.format(Locale.US, "%.1f GB", availableBytes.toDouble() / (1024 * 1024 * 1024))
}

/**
 * Storage utilization reported via primary internal user storage [android.os.StatFs].
 * Primary user storage directory source: [android.os.Environment.getDataDirectory] (/data).
 */
data class StorageInfo(
    val totalBytes: Long = 0L,
    val availableBytes: Long = 0L,
    val usedBytes: Long = 0L,
    val usedPercentage: Int = 0
) {
    val totalGbFormatted: String
        get() = String.format(Locale.US, "%.0f GB", totalBytes.toDouble() / (1024 * 1024 * 1024))

    val usedGbFormatted: String
        get() = String.format(Locale.US, "%.0f GB", usedBytes.toDouble() / (1024 * 1024 * 1024))

    val availableGbFormatted: String
        get() = String.format(Locale.US, "%.0f GB", availableBytes.toDouble() / (1024 * 1024 * 1024))
}

enum class NetworkType {
    WIFI,
    CELLULAR,
    ETHERNET,
    OFFLINE,
    OTHER
}

data class NetworkStatus(
    val isConnected: Boolean = false,
    val type: NetworkType = NetworkType.OFFLINE,
    val displayLabel: String = "Offline"
)

data class SystemDashboardData(
    val ram: RamInfo = RamInfo(),
    val storage: StorageInfo = StorageInfo(),
    val network: NetworkStatus = NetworkStatus(),
    val battery: BatteryInfo = BatteryInfo(),
    val processorCount: Int = Runtime.getRuntime().availableProcessors()
) {
    companion object {
        fun sample(): SystemDashboardData {
            val totalRam = 24L * 1024 * 1024 * 1024 // 24 GB
            val usedRam = (8.2 * 1024 * 1024 * 1024).toLong() // 8.2 GB
            val availRam = totalRam - usedRam

            val totalStorage = 512L * 1024 * 1024 * 1024 // 512 GB
            val usedStorage = 184L * 1024 * 1024 * 1024 // 184 GB
            val availStorage = totalStorage - usedStorage

            return SystemDashboardData(
                ram = RamInfo(
                    totalBytes = totalRam,
                    availableBytes = availRam,
                    usedBytes = usedRam,
                    usedPercentage = 34
                ),
                storage = StorageInfo(
                    totalBytes = totalStorage,
                    availableBytes = availStorage,
                    usedBytes = usedStorage,
                    usedPercentage = 36
                ),
                network = NetworkStatus(
                    isConnected = true,
                    type = NetworkType.WIFI,
                    displayLabel = "Wi-Fi"
                ),
                battery = BatteryInfo(
                    percentage = 87,
                    isCharging = true,
                    isFull = false,
                    temperatureCelsius = 31.0f
                ),
                processorCount = 8
            )
        }
    }
}
