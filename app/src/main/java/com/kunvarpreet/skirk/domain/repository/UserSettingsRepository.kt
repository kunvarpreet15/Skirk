package com.kunvarpreet.skirk.domain.repository

import kotlinx.coroutines.flow.Flow

enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK
}

data class UserSettings(
    val themeMode: ThemeMode = ThemeMode.DARK,
    val dynamicColor: Boolean = true,
    val keepScreenOnWhenCharging: Boolean = true
)

/**
 * Repository interface for global application preferences and display settings.
 */
interface UserSettingsRepository {
    fun observeSettings(): Flow<UserSettings>
    suspend fun getSettings(): UserSettings
    suspend fun updateThemeMode(themeMode: ThemeMode)
    suspend fun updateDynamicColor(enabled: Boolean)
    suspend fun updateKeepScreenOn(enabled: Boolean)
}
