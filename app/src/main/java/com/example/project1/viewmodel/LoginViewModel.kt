package com.example.project1.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.preference.PreferenceManager
import com.example.project1.DataStoreManager
import com.example.project1.authentication.profile.ProfileApiService
import com.example.project1.authentication.register.LoginApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val apiService = LoginApiService()
    private val profileApiService = ProfileApiService()

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

                    // After successful login, fetch and save username and profile picture
                    fetchUserProfile(context)

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
                // Save username to DataStore
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
}

// UI states for Login screen
sealed class LoginUiState {
    object Initial : LoginUiState()
    object Loading : LoginUiState()
    object Success : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}