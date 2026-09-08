package com.kunvarpreet.skirk

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.kunvarpreet.skirk.di.LocalAppContainer
import com.kunvarpreet.skirk.domain.repository.ThemeMode
import com.kunvarpreet.skirk.navigation.SkirkNavGraph
import com.kunvarpreet.skirk.ui.theme.SkirkTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val appContainer = (application as SkirkApplication).appContainer

        setContent {
            val userSettings by appContainer.userSettingsRepository.observeSettings().collectAsState(initial = null)
            val isDarkTheme = when (userSettings?.themeMode) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
                ThemeMode.SYSTEM, null -> isSystemInDarkTheme()
            }
            val dynamicColor = userSettings?.dynamicColor ?: true

            CompositionLocalProvider(LocalAppContainer provides appContainer) {
                SkirkTheme(
                    darkTheme = isDarkTheme,
                    dynamicColor = dynamicColor
                ) {
                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        val navController = rememberNavController()
                        SkirkNavGraph(
                            appContainer = appContainer,
                            navController = navController,
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }
}