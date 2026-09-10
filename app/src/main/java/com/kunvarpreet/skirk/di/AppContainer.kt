package com.kunvarpreet.skirk.di

import android.content.Context
import androidx.compose.runtime.staticCompositionLocalOf
import com.kunvarpreet.skirk.data.local.storage.JsonDashboardFileStorage
import com.kunvarpreet.skirk.data.repository.DashboardRepositoryImpl
import com.kunvarpreet.skirk.data.repository.UserSettingsRepositoryImpl
import com.kunvarpreet.skirk.domain.repository.DashboardRepository
import com.kunvarpreet.skirk.domain.repository.UserSettingsRepository
import com.kunvarpreet.skirk.widget.core.WidgetRegistry
import com.kunvarpreet.skirk.widget.definitions.BuiltInWidgetDefinitions

import com.kunvarpreet.skirk.data.power.AndroidChargingStateProvider
import com.kunvarpreet.skirk.domain.power.ChargingStateProvider
import com.kunvarpreet.skirk.domain.standby.StandByController

import com.kunvarpreet.skirk.media.data.AndroidMediaSessionRepository
import com.kunvarpreet.skirk.media.domain.MediaSessionRepository
import com.kunvarpreet.skirk.widget.battery.BatteryInfoProvider
import com.kunvarpreet.skirk.widget.battery.AndroidBatteryInfoProvider

/**
 * Dependency Injection container interface providing application-wide singletons.
 */
interface AppContainer {
    val dashboardRepository: DashboardRepository
    val userSettingsRepository: UserSettingsRepository
    val widgetRegistry: WidgetRegistry
    val chargingStateProvider: ChargingStateProvider
    val standByController: StandByController
    val batteryInfoProvider: BatteryInfoProvider
    val mediaSessionRepository: MediaSessionRepository
}

/**
 * Default implementation of [AppContainer] creating and holding long-lived application dependencies.
 */
class DefaultAppContainer(private val context: Context) : AppContainer {

    override val batteryInfoProvider: BatteryInfoProvider by lazy {
        AndroidBatteryInfoProvider(context = context.applicationContext)
    }

    override val mediaSessionRepository: MediaSessionRepository by lazy {
        AndroidMediaSessionRepository(context = context.applicationContext)
    }

    override val widgetRegistry: WidgetRegistry by lazy {
        WidgetRegistry().apply {
            BuiltInWidgetDefinitions.createBuiltInProviders(
                batteryInfoProvider = batteryInfoProvider,
                mediaSessionRepository = mediaSessionRepository
            ).forEach { register(it) }
        }
    }

    private val jsonStorage: JsonDashboardFileStorage by lazy {
        JsonDashboardFileStorage(context.applicationContext)
    }

    override val dashboardRepository: DashboardRepository by lazy {
        DashboardRepositoryImpl(storage = jsonStorage)
    }

    override val userSettingsRepository: UserSettingsRepository by lazy {
        UserSettingsRepositoryImpl(context = context.applicationContext)
    }

    override val chargingStateProvider: ChargingStateProvider by lazy {
        AndroidChargingStateProvider(context = context.applicationContext)
    }

    override val standByController: StandByController by lazy {
        StandByController(chargingStateProvider = chargingStateProvider)
    }
}

val LocalAppContainer = staticCompositionLocalOf<AppContainer> {
    error("No AppContainer provided in CompositionLocal")
}
