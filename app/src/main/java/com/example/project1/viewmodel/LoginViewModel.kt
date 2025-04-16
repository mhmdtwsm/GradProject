package com.example.project1.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.preference.PreferenceManager
import com.example.project1.DataStoreManager
import com.example.project1.authentication.register.LoginApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val apiService = LoginApiService()

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

                    _uiState.value = LoginUiState.Success
                    DataStoreManager.saveOnboardingStatus(context, true)

                    onSuccess()
                } else {
                    _uiState.value = LoginUiState.Error(response.message.ifEmpty { "Login failed" })
                }
            } catch (e: Exception) {
                _uiState.value = LoginUiState.Error("Error: ${e.message}")
            }
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
