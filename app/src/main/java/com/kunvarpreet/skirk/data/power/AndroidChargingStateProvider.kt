package com.kunvarpreet.skirk.data.power

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import com.kunvarpreet.skirk.domain.power.ChargingSource
import com.kunvarpreet.skirk.domain.power.ChargingState
import com.kunvarpreet.skirk.domain.power.ChargingStateProvider
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

/**
 * Android implementation of [ChargingStateProvider] using broadcast receivers and sticky battery intents.
 */
class AndroidChargingStateProvider(
    private val context: Context
) : ChargingStateProvider {

    override fun observeChargingState(): Flow<ChargingState> = callbackFlow {
        // Emit current instantaneous status immediately on subscription
        trySend(getCurrentChargingState())

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                intent?.let {
                    val state = parseBatteryIntent(it)
                    trySend(state)
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

    override fun getCurrentChargingState(): ChargingState {
        val stickyIntent = context.registerReceiver(
            null,
            IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        )
        return parseBatteryIntent(stickyIntent)
    }

    internal fun parseBatteryIntent(intent: Intent?): ChargingState {
        if (intent == null) return ChargingState.Disconnected

        val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL

        val chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0)
        val source = parseChargingSource(chargePlug, isCharging)

        return ChargingState(
            isCharging = isCharging && source != ChargingSource.NONE,
            source = source
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
                } else if (chargePlug != 0) {
                    ChargingSource.OTHER
                } else if (isCharging) {
                    ChargingSource.OTHER
                } else {
                    ChargingSource.NONE
                }
            }
        }
    }
}
