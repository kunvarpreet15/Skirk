package com.kunvarpreet.skirk.system.data

import android.app.ActivityManager
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Environment
import android.os.StatFs
import com.kunvarpreet.skirk.system.domain.SystemDashboardRepository
import com.kunvarpreet.skirk.system.model.NetworkStatus
import com.kunvarpreet.skirk.system.model.NetworkType
import com.kunvarpreet.skirk.system.model.RamInfo
import com.kunvarpreet.skirk.system.model.StorageInfo
import com.kunvarpreet.skirk.system.model.SystemDashboardData
import com.kunvarpreet.skirk.widget.battery.BatteryInfoProvider
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow

/**
 * Production implementation of [SystemDashboardRepository] that queries system APIs.
 *
 * StandBy Efficiency & Architecture:
 * - Battery: Reuses [BatteryInfoProvider] event-driven updates.
 * - Network: Event-driven via [ConnectivityManager.NetworkCallback].
 * - RAM: Polled at conservative 5-second intervals.
 * - Storage: Polled at low-frequency 30-second intervals (primary internal data directory).
 * - CPU: Exposes stable hardware core count; volatile CPU load percentage is omitted
 *   as per Android SELinux /proc/stat restrictions.
 */
class AndroidSystemDashboardRepository(
    private val context: Context,
    private val batteryInfoProvider: BatteryInfoProvider
) : SystemDashboardRepository {

    private val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
    private val processorCount = Runtime.getRuntime().availableProcessors()

    override fun observeSystemDashboard(): Flow<SystemDashboardData> {
        val networkFlow = observeNetworkStatus()
        val ramFlow = observeRamInfo()
        val storageFlow = observeStorageInfo()
        val batteryFlow = batteryInfoProvider.observeBatteryInfo()

        return combine(
            ramFlow,
            storageFlow,
            networkFlow,
            batteryFlow
        ) { ram, storage, network, battery ->
            SystemDashboardData(
                ram = ram,
                storage = storage,
                network = network,
                battery = battery,
                processorCount = processorCount
            )
        }.distinctUntilChanged()
    }

    private fun observeNetworkStatus(): Flow<NetworkStatus> = callbackFlow {
        trySend(queryNetworkStatus())

        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                trySend(queryNetworkStatus())
            }

            override fun onLost(network: Network) {
                trySend(queryNetworkStatus())
            }

            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                trySend(queryNetworkStatus())
            }
        }

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        try {
            connectivityManager?.registerNetworkCallback(request, callback)
        } catch (e: Exception) {
            trySend(queryNetworkStatus())
        }

        awaitClose {
            try {
                connectivityManager?.unregisterNetworkCallback(callback)
            } catch (ignored: Exception) {
            }
        }
    }.distinctUntilChanged()

    private fun observeRamInfo(): Flow<RamInfo> = flow {
        while (true) {
            emit(queryRamInfo())
            delay(5_000L) // 5 seconds interval for memory metrics
        }
    }.distinctUntilChanged()

    private fun observeStorageInfo(): Flow<StorageInfo> = flow {
        while (true) {
            emit(queryStorageInfo())
            delay(30_000L) // 30 seconds interval for storage metrics
        }
    }.distinctUntilChanged()

    private fun queryRamInfo(): RamInfo {
        val am = activityManager ?: return RamInfo()
        val memInfo = ActivityManager.MemoryInfo()
        am.getMemoryInfo(memInfo)
        val total = memInfo.totalMem
        val avail = memInfo.availMem
        val used = (total - avail).coerceAtLeast(0L)
        val pct = if (total > 0) {
            ((used.toDouble() / total.toDouble()) * 100).toInt().coerceIn(0, 100)
        } else {
            0
        }
        return RamInfo(
            totalBytes = total,
            availableBytes = avail,
            usedBytes = used,
            usedPercentage = pct
        )
    }

    private fun queryStorageInfo(): StorageInfo {
        return try {
            val path = Environment.getDataDirectory().path
            val stat = StatFs(path)
            val blockSize = stat.blockSizeLong
            val totalBlocks = stat.blockCountLong
            val availBlocks = stat.availableBlocksLong
            val total = totalBlocks * blockSize
            val avail = availBlocks * blockSize
            val used = (total - avail).coerceAtLeast(0L)
            val pct = if (total > 0) {
                ((used.toDouble() / total.toDouble()) * 100).toInt().coerceIn(0, 100)
            } else {
                0
            }
            StorageInfo(
                totalBytes = total,
                availableBytes = avail,
                usedBytes = used,
                usedPercentage = pct
            )
        } catch (e: Exception) {
            StorageInfo()
        }
    }

    private fun queryNetworkStatus(): NetworkStatus {
        val cm = connectivityManager ?: return NetworkStatus(isConnected = false, type = NetworkType.OFFLINE, displayLabel = "Offline")
        val activeNetwork = cm.activeNetwork ?: return NetworkStatus(isConnected = false, type = NetworkType.OFFLINE, displayLabel = "Offline")
        val caps = cm.getNetworkCapabilities(activeNetwork) ?: return NetworkStatus(isConnected = false, type = NetworkType.OFFLINE, displayLabel = "Offline")

        return when {
            caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                NetworkStatus(isConnected = true, type = NetworkType.WIFI, displayLabel = "Wi-Fi")
            }
            caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                NetworkStatus(isConnected = true, type = NetworkType.CELLULAR, displayLabel = "Cellular")
            }
            caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                NetworkStatus(isConnected = true, type = NetworkType.ETHERNET, displayLabel = "Ethernet")
            }
            caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) -> {
                NetworkStatus(isConnected = true, type = NetworkType.OTHER, displayLabel = "Connected")
            }
            else -> {
                NetworkStatus(isConnected = false, type = NetworkType.OFFLINE, displayLabel = "Offline")
            }
        }
    }
}
