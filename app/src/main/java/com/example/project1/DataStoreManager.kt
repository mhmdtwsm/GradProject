package com.example.project1

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_settings")

object DataStoreManager {
    val USERNAME_KEY = stringPreferencesKey("username")
    val EMAIL = stringPreferencesKey("user_email")
    val AUTH_TOKEN = stringPreferencesKey("AUTH_TOKEN")
    val VERIFY_TOKEN = stringPreferencesKey("refresh_token")
    val ONBOARDING_KEY = booleanPreferencesKey("onboarding_completed")
    // Add more keys as needed

    suspend fun saveUsername(context: Context, username: String) {
        context.dataStore.edit { prefs ->
            prefs[USERNAME_KEY] = username
        }
    }

    fun getAuthToken(context: Context): Flow<String?> {
        return context.dataStore.data.map { prefs ->
            prefs[AUTH_TOKEN]
        }
    }

    fun getUsername(context: Context): Flow<String?> {
        return context.dataStore.data.map { prefs ->
            prefs[USERNAME_KEY]
        }
    }

    suspend fun saveOnboardingStatus(context: Context, completed: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[ONBOARDING_KEY] = completed
        }
    }

    fun isOnboardingCompleted(context: Context): Flow<Boolean> {
        return context.dataStore.data.map { prefs ->
            prefs[ONBOARDING_KEY] ?: false
        }
    }

    suspend fun clearData(context: Context) {
        context.dataStore.edit { prefs ->
            prefs.clear()
        }
    }
}