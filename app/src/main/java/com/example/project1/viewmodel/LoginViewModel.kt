package com.example.project1.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.preference.PreferenceManager
import com.example.project1.DataStoreManager
import com.example.project1.SMS.SMSData.SMSHistoryItem
import com.example.project1.SMS.network.SMSNetworkService
import com.example.project1.URL.URLData.URLHistoryItem
import com.example.project1.URL.network.URLNetworkService
import com.example.project1.authentication.profile.ProfileApiService
import com.example.project1.authentication.register.LoginApiService
import com.example.project1.statistics.StatisticsManager
import com.example.project1.statistics.StatisticsNetworkService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileWriter

class LoginViewModel : ViewModel() {
    private val apiService = LoginApiService()
    private val profileApiService = ProfileApiService()
    private val urlNetworkService = URLNetworkService()
    private val smsNetworkService = SMSNetworkService()
    private val statisticsService = StatisticsNetworkService()

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Initial)
    val uiState: StateFlow<LoginUiState> = _uiState

    fun login(email: String, password: String, context: Context, onSuccess: () -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = LoginUiState.Error("Please enter both email and password")
            return
        }

        _uiState.value = LoginUiState.Loading

        viewModelScope.launch {
            try {
                val response = apiService.login(email, password)

                if (response.success && response.token != null) {
                    // Save token to SharedPreferences
                    val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)
                    sharedPrefs.edit()
                        .putString("AUTH_TOKEN", response.token)
                        .putString("VERIFY_EMAIL", email)
                        .apply()

                    // Set onboarding status
                    DataStoreManager.saveOnboardingStatus(context, true)

                    // After successful login, fetch and save user data
                    fetchUserProfile(context)

                    // Fetch all history and statistics
                    fetchInitialData(context, response.token)

                    _uiState.value = LoginUiState.Success
                    onSuccess()
                } else {
                    _uiState.value = LoginUiState.Error(response.message.ifEmpty { "Login failed" })
                }
            } catch (e: Exception) {
                _uiState.value = LoginUiState.Error("Error: ${e.message}")
            }
        }
    }

    private suspend fun fetchUserProfile(context: Context) {
        try {
            // Fetch and save username
            val usernameResponse = profileApiService.fetchUsername(context)
            if (usernameResponse.success) {
                DataStoreManager.saveUsername(context, usernameResponse.username)
                Log.d("LoginViewModel", "Username saved: ${usernameResponse.username}")
            } else {
                Log.e("LoginViewModel", "Failed to fetch username: ${usernameResponse.message}")
            }

            // Fetch and save profile picture
            val profilePictureSuccess = profileApiService.fetchAndSaveProfilePicture(context)
            if (profilePictureSuccess) {
                Log.d("LoginViewModel", "Profile picture saved successfully")
            } else {
                Log.e("LoginViewModel", "Failed to fetch profile picture")
            }
        } catch (e: Exception) {
            Log.e("LoginViewModel", "Error fetching user profile: ${e.message}")
        }
    }

    private suspend fun fetchInitialData(context: Context, authToken: String) {
        try {
            // Fetch URL history
            val urlHistory = urlNetworkService.fetchUrlHistory(authToken)
            saveUrlHistoryToLocal(context, urlHistory)

            // Fetch SMS history
            val smsHistory = smsNetworkService.fetchSMSHistory(authToken)
            saveSmsHistoryToLocal(context, smsHistory)

            // Fetch statistics
            val statistics = statisticsService.fetchUserStatistics(authToken)
            StatisticsManager.getInstance(context).updateStatistics(statistics)

            Log.d("LoginViewModel", "Initial data fetched successfully")
        } catch (e: Exception) {
            Log.e("LoginViewModel", "Error fetching initial data: ${e.message}")
        }
    }

    private fun saveUrlHistoryToLocal(context: Context, historyItems: List<URLHistoryItem>) {
        try {
            val jsonArray = JSONArray()
            historyItems.forEach { item ->
                val jsonObject = JSONObject().apply {
                    put("url", item.url)
                    put("isSafe", item.isSafe)
                    put("timestamp", item.timestamp)
                }
                jsonArray.put(jsonObject)
            }

            val file = File(context.filesDir, URLViewModel.HISTORY_JSON_FILE)
            FileWriter(file).use { writer ->
                writer.write(jsonArray.toString())
            }
        } catch (e: Exception) {
            Log.e("LoginViewModel", "Error saving URL history: ${e.message}")
        }
    }

    private fun saveSmsHistoryToLocal(context: Context, historyItems: List<SMSHistoryItem>) {
        try {
            val jsonArray = JSONArray()
            historyItems.forEach { item ->
                val jsonObject = JSONObject().apply {
                    put("message", item.message)
                    put("isSafe", item.isSafe)
                    put("timestamp", item.timestamp)
                }
                jsonArray.put(jsonObject)
            }

            val file = File(context.filesDir, "sms_history.json")
            FileWriter(file).use { writer ->
                writer.write(jsonArray.toString())
            }
        } catch (e: Exception) {
            Log.e("LoginViewModel", "Error saving SMS history: ${e.message}")
        }
    }
}

// UI states for Login screen
sealed class LoginUiState {
    object Initial : LoginUiState()
    object Loading : LoginUiState()
    object Success : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}