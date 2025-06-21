package com.example.project1.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class PasswordResetViewModel : ViewModel() {

    // UI state
    private val _uiState = MutableStateFlow(PasswordResetUiState())
    val uiState: StateFlow<PasswordResetUiState> = _uiState.asStateFlow()

    // Update password field
    fun updatePassword(newPassword: String) {
        _uiState.value = _uiState.value.copy(password = newPassword)
    }

    // Update confirm password field
    fun updateConfirmPassword(newConfirmPassword: String) {
        _uiState.value = _uiState.value.copy(confirmPassword = newConfirmPassword)
    }

    // Check if both passwords match
    fun passwordsMatch(): Boolean {
        return _uiState.value.password == _uiState.value.confirmPassword &&
                _uiState.value.password.isNotEmpty()
    }

    // Reset password API call
    fun resetPassword(email: String, onSuccess: () -> Unit) {
        if (!passwordsMatch()) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Passwords don't match"
            )
            return
        }

        _uiState.value = _uiState.value.copy(
            isLoading = true,
            errorMessage = null
        )

        viewModelScope.launch {
            try {
                val result = performPasswordReset(email, _uiState.value.password)
                _uiState.value = _uiState.value.copy(isLoading = false)

                if (result.isSuccess) {
                    onSuccess()
                } else {
                    _uiState.value = _uiState.value.copy(
                        errorMessage = result.errorMessage ?: "Unknown error occurred"
                    )
                }
            } catch (e: Exception) {
                Log.e("PasswordResetViewModel", "Error resetting password", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Network error: ${e.localizedMessage}"
                )
            }
        }
    }

    // Actual network call
    private suspend fun performPasswordReset(
        email: String,
        newPassword: String
    ): PasswordResetResult {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL("http://phishaware.runasp.net/api/auth/reset-password")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.doOutput = true

                // Create JSON payload
                val jsonPayload = JSONObject().apply {
                    put("email", email)
                    put("newPassword", newPassword)
                }

                // Write to the connection
                OutputStreamWriter(connection.outputStream).use { writer ->
                    writer.write(jsonPayload.toString())
                    writer.flush()
                }

                val responseCode = connection.responseCode

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Password reset successful
                    PasswordResetResult(true)
                } else {
                    // Handle error
                    val errorStream = connection.errorStream
                    val errorResponse =
                        errorStream?.bufferedReader()?.use { it.readText() } ?: "Unknown error"
                    PasswordResetResult(false, "Error $responseCode: $errorResponse")
                }
            } catch (e: Exception) {
                PasswordResetResult(false, e.localizedMessage)
            }
        }
    }

    // Reset error state
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}

// UI state class
data class PasswordResetUiState(
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

// Result class for API response
data class PasswordResetResult(
    val isSuccess: Boolean,
    val errorMessage: String? = null
)
