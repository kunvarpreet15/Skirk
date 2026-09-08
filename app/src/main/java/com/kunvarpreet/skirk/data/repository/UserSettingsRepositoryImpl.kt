package com.kunvarpreet.skirk.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.kunvarpreet.skirk.domain.repository.ThemeMode
import com.kunvarpreet.skirk.domain.repository.UserSettings
import com.kunvarpreet.skirk.domain.repository.UserSettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.userPreferencesDataStore: DataStore<Preferences> by preferencesDataStore(name = "user_settings")

/**
 * Implementation of [UserSettingsRepository] using AndroidX DataStore Preferences.
 */
class UserSettingsRepositoryImpl(
    private val context: Context
) : UserSettingsRepository {

    private object Keys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val DYNAMIC_COLOR = booleanPreferencesKey("dynamic_color")
        val KEEP_SCREEN_ON = booleanPreferencesKey("keep_screen_on")
    }

    override fun observeSettings(): Flow<UserSettings> {
        return context.userPreferencesDataStore.data.map { prefs ->
            val themeModeName = prefs[Keys.THEME_MODE] ?: ThemeMode.DARK.name
            val themeMode = try {
                ThemeMode.valueOf(themeModeName)
            } catch (e: Exception) {
                ThemeMode.DARK
            }
            UserSettings(
                themeMode = themeMode,
                dynamicColor = prefs[Keys.DYNAMIC_COLOR] ?: true,
                keepScreenOnWhenCharging = prefs[Keys.KEEP_SCREEN_ON] ?: true
            )
        }
    }

    override suspend fun getSettings(): UserSettings {
        return observeSettings().first()
    }

    override suspend fun updateThemeMode(themeMode: ThemeMode) {
        context.userPreferencesDataStore.edit { prefs ->
            prefs[Keys.THEME_MODE] = themeMode.name
        }
    }

    override suspend fun updateDynamicColor(enabled: Boolean) {
        context.userPreferencesDataStore.edit { prefs ->
            prefs[Keys.DYNAMIC_COLOR] = enabled
        }
    }

    override suspend fun updateKeepScreenOn(enabled: Boolean) {
        context.userPreferencesDataStore.edit { prefs ->
            prefs[Keys.KEEP_SCREEN_ON] = enabled
        }
    }
}
