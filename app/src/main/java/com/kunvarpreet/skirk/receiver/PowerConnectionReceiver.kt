package com.kunvarpreet.skirk.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.kunvarpreet.skirk.SkirkApplication
import com.kunvarpreet.skirk.presentation.standby.StandByActivity

/**
 * Manifest-registered broadcast receiver for device power connection events.
 * Triggered by the system when the device begins or stops charging.
 */
class PowerConnectionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val app = context.applicationContext as? SkirkApplication ?: return
        val standByController = app.appContainer.standByController

        when (intent.action) {
            Intent.ACTION_POWER_CONNECTED -> {
                // Launch StandByActivity directly when power is plugged in
                val activityIntent = StandByActivity.createIntent(context)
                context.startActivity(activityIntent)
            }
            Intent.ACTION_POWER_DISCONNECTED -> {
                standByController.exitStandBy()
            }
        }
    }
}
