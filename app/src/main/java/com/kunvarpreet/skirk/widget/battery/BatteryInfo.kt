package com.kunvarpreet.skirk.widget.battery

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import com.kunvarpreet.skirk.domain.power.ChargingSource
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

/**
 * Domain model representing comprehensive battery and charging information.
 */
data class BatteryInfo(
    val percentage: Int = 100,
    val isCharging: Boolean = false,
    val isFull: Boolean = false,
    val source: ChargingSource = ChargingSource.NONE,
    val temperatureCelsius: Float = 25.0f,
    val health: String = "Good"
) {
    companion object {
        val Sample = BatteryInfo(
            percentage = 87,
            isCharging = true,
            isFull = false,
            source = ChargingSource.AC,
            temperatureCelsius = 31.5f,
            health = "Good"
        )
    }
}

/**
 * Contract for querying and observing real-time battery status.
 */
interface BatteryInfoProvider {
    fun observeBatteryInfo(): Flow<BatteryInfo>
    fun getCurrentBatteryInfo(): BatteryInfo
}

/**
 * Static/mock provider for previews and default registry initialization without Context.
 */
class SampleBatteryInfoProvider(
    private val sample: BatteryInfo = BatteryInfo.Sample
) : BatteryInfoProvider {
    override fun observeBatteryInfo(): Flow<BatteryInfo> = kotlinx.coroutines.flow.flowOf(sample)
    override fun getCurrentBatteryInfo(): BatteryInfo = sample
}

/**
 * Android implementation observing battery broadcasts via Kotlin Flow.
 * Operates purely event-driven without background polling loops.
 */
class AndroidBatteryInfoProvider(
    private val context: Context
) : BatteryInfoProvider {

    override fun observeBatteryInfo(): Flow<BatteryInfo> = callbackFlow {
        // Emit instantaneous status immediately upon subscription
        trySend(getCurrentBatteryInfo())

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(c: Context?, intent: Intent?) {
                intent?.let {
                    trySend(parseBatteryIntent(it))
                }
            }
        }

        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_BATTERY_CHANGED)
            addAction(Intent.ACTION_POWER_CONNECTED)
            addAction(Intent.ACTION_POWER_DISCONNECTED)
        }

        context.registerReceiver(receiver, filter)

        awaitClose {
            try {
                context.unregisterReceiver(receiver)
            } catch (e: Exception) {
                // Ignore if already unregistered
            }
        }
    }.distinctUntilChanged()

    override fun getCurrentBatteryInfo(): BatteryInfo {
        val stickyIntent = context.registerReceiver(
            null,
            IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        )
        return parseBatteryIntent(stickyIntent)
    }

    internal fun parseBatteryIntent(intent: Intent?): BatteryInfo {
        if (intent == null) return BatteryInfo()

        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        val percentage = if (level >= 0 && scale > 0) {
            ((level.toFloat() / scale.toFloat()) * 100).toInt().coerceIn(0, 100)
        } else {
            50
        }

        val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL
        val isFull = status == BatteryManager.BATTERY_STATUS_FULL

        val chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0)
        val source = parseChargingSource(chargePlug, isCharging)

        val rawTemp = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 250)
        val tempCelsius = rawTemp / 10.0f

        val healthCode = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, BatteryManager.BATTERY_HEALTH_GOOD)
        val healthString = when (healthCode) {
            BatteryManager.BATTERY_HEALTH_OVERHEAT -> "Overheat"
            BatteryManager.BATTERY_HEALTH_DEAD -> "Dead"
            BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> "Over Voltage"
            BatteryManager.BATTERY_HEALTH_COLD -> "Cold"
            else -> "Good"
        }

        return BatteryInfo(
            percentage = percentage,
            isCharging = isCharging && source != ChargingSource.NONE,
            isFull = isFull,
            source = source,
            temperatureCelsius = tempCelsius,
            health = healthString
        )
    }

    private fun parseChargingSource(chargePlug: Int, isCharging: Boolean): ChargingSource {
        if (!isCharging && chargePlug == 0) return ChargingSource.NONE

        return when (chargePlug) {
            BatteryManager.BATTERY_PLUGGED_AC -> ChargingSource.AC
            BatteryManager.BATTERY_PLUGGED_USB -> ChargingSource.USB
            BatteryManager.BATTERY_PLUGGED_WIRELESS -> ChargingSource.WIRELESS
            else -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                    chargePlug == BatteryManager.BATTERY_PLUGGED_DOCK
                ) {
                    ChargingSource.DOCK
                } else if (chargePlug != 0 || isCharging) {
                    ChargingSource.OTHER
                } else {
                    ChargingSource.NONE
                }
            }
        }
    }
}
