package com.example.project1.ui.theme

import android.content.Context
import android.content.res.Configuration
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Create DataStore instance
private val Context.themeDataStore: DataStore<Preferences> by preferencesDataStore(name = "theme_settings")

object ThemeManager {
    private val DARK_MODE_KEY = androidx.datastore.preferences.core.booleanPreferencesKey("dark_mode")

    fun isDarkMode(context: Context): Flow<Boolean> {
        return context.themeDataStore.data.map { preferences ->
            // If no preference is saved, use system theme as default
            preferences[DARK_MODE_KEY] ?: isSystemInDarkTheme(context)
        }
    }

    suspend fun setDarkMode(context: Context, isDarkMode: Boolean) {
        context.themeDataStore.edit { preferences ->
            preferences[DARK_MODE_KEY] = isDarkMode
        }
    }

    private fun isSystemInDarkTheme(context: Context): Boolean {
        return when (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> true
            Configuration.UI_MODE_NIGHT_NO -> false
            else -> false // Default to light if unknown
        }
    }
}