package com.kunvarpreet.skirk

import android.app.Application
import com.kunvarpreet.skirk.di.AppContainer
import com.kunvarpreet.skirk.di.DefaultAppContainer

/**
 * Application class for Skirk, initializing the global DI container.
 */
class SkirkApplication : Application() {

    lateinit var appContainer: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        appContainer = DefaultAppContainer(this)
    }
}
