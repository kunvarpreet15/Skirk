package com.kunvarpreet.skirk.presentation.standby

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.kunvarpreet.skirk.SkirkApplication
import com.kunvarpreet.skirk.domain.standby.StandByCommand
import com.kunvarpreet.skirk.presentation.dashboard.DashboardViewModel
import com.kunvarpreet.skirk.ui.theme.SkirkTheme
import kotlinx.coroutines.launch

/**
 * Dedicated Activity for the StandBy display experience.
 *
 * Implements:
 * - Full-screen immersive system bar hiding.
 * - Landscape orientation lock with sensor support.
 * - Display wake lock via FLAG_KEEP_SCREEN_ON.
 * - Integration with live model-driven DashboardView.
 * - Lifecycle synchronization with StandByController.
 */
class StandByActivity : ComponentActivity() {

    private val appContainer by lazy {
        (application as SkirkApplication).appContainer
    }

    private val standByController by lazy {
        appContainer.standByController
    }

    private val dashboardViewModel: DashboardViewModel by viewModels {
        DashboardViewModel.provideFactory(
            dashboardRepository = appContainer.dashboardRepository,
            userSettingsRepository = appContainer.userSettingsRepository
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Configure landscape orientation
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE

        // Configure full-screen immersive mode
        hideSystemBars()

        // Keep screen awake while in StandBy mode
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        // Notify controller that StandBy UI is active
        standByController.notifyStandByActivityActive()

        // Listen for controller exit commands
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                standByController.commands.collect { command ->
                    if (command == StandByCommand.FinishStandByActivity) {
                        finish()
                    }
                }
            }
        }

        setContent {
            SkirkTheme(darkTheme = true) {
                StandByScreen(
                    dashboardViewModel = dashboardViewModel,
                    standByController = standByController,
                    widgetRegistry = appContainer.widgetRegistry,
                    onExitStandBy = {
                        standByController.exitStandBy()
                        finish()
                    }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        hideSystemBars()
        standByController.notifyStandByActivityActive()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            hideSystemBars()
        }
    }

    private fun hideSystemBars() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val insetsController = WindowCompat.getInsetsController(window, window.decorView)
        insetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        insetsController.hide(WindowInsetsCompat.Type.systemBars())
    }

    override fun onDestroy() {
        super.onDestroy()
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        standByController.notifyStandByActivityFinished()
    }

    companion object {
        fun createIntent(context: Context): Intent {
            return Intent(context, StandByActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
        }
    }
}
