package com.example.project1.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.preference.PreferenceManager
import com.example.project1.authentication.resetpassword.ChangePasswordApiService
import kotlinx.coroutines.launch

class ChangePasswordViewModel : ViewModel() {
    private val apiService = ChangePasswordApiService()

    // UI state
    var oldPassword by mutableStateOf("")
    var newPassword by mutableStateOf("")
    var confirmPassword by mutableStateOf("")

    // UI state for visibility
    var oldPasswordVisible by mutableStateOf(false)
    var newPasswordVisible by mutableStateOf(false)
    var confirmPasswordVisible by mutableStateOf(false)

    // UI state for loading and errors
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf("")
    var successMessage by mutableStateOf("")

    // Password validation requirements
    private val specialCharacters =
        listOf("!", "@", "#", "$", "%", "^", "&", "*", "(", ")", "_", "-", "+", "=")
    private val numbers = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0")

    // Validation functions
    fun validateInputs(): Boolean {
        when {
            oldPassword.isBlank() || newPassword.isBlank() || confirmPassword.isBlank() -> {
                errorMessage = "All fields are required"
                return false
            }

            !(newPassword.length >= 8) || !(specialCharacters.any {
                newPassword.contains(it)
            }) || !(numbers.any {
                newPassword.contains(it)
            }) -> {
                errorMessage =
                    "Password must be at least 8 characters long and contain at least one special character and number"
                return false
            }

            newPassword != confirmPassword -> {
                errorMessage = "Passwords do not match"
                return false
            }

            oldPassword == newPassword -> {
                errorMessage = "New password cannot be the same as old password"
                return false
            }

            else -> {
                errorMessage = ""
                return true
            }
        }
    }

    fun changePassword(context: Context, onSuccess: () -> Unit) {
        if (!validateInputs()) return

        isLoading = true
        errorMessage = ""
        successMessage = ""

        viewModelScope.launch {
            try {
                // Get the auth token from SharedPreferences
                val authToken = PreferenceManager.getDefaultSharedPreferences(context)
                    .getString("AUTH_TOKEN", null)

                if (authToken == null) {
                    isLoading = false
                    errorMessage = "Not authenticated. Please login again."
                    return@launch
                }

                val response = apiService.changePassword(
                    oldPassword = oldPassword,
                    newPassword = newPassword,
                    confirmPassword = confirmPassword,
                    authToken = authToken
                )

                isLoading = false

                if (response.success) {
                    successMessage = response.message
                    onSuccess()
                } else {
                    errorMessage = response.message
                }
            } catch (e: Exception) {
                isLoading = false
                errorMessage = "Password change failed: ${e.message}"
            }
        }
    }
}
