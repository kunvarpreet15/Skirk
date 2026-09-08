package com.kunvarpreet.skirk

import android.app.Application
import com.kunvarpreet.skirk.di.AppContainer
import com.kunvarpreet.skirk.di.DefaultAppContainer
import com.kunvarpreet.skirk.domain.standby.StandByCommand
import com.kunvarpreet.skirk.presentation.standby.StandByActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Application class for Skirk, initializing the global DI container
 * and coordinating application-level StandBy launches.
 */
class SkirkApplication : Application() {
    lateinit var appContainer: AppContainer
        private set
    private val applicationScope = CoroutineScope(Dispatchers.Main)
    override fun onCreate() {
        super.onCreate()
        appContainer = DefaultAppContainer(this)

        applicationScope.launch {
            appContainer.standByController.commands.collect { command ->
                if (command == StandByCommand.LaunchStandByActivity) {
                    val intent = StandByActivity.createIntent(this@SkirkApplication)
                    startActivity(intent) } } } } }