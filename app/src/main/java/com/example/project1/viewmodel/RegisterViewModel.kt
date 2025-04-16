package com.example.project1.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project1.authentication.register.RegisterApiService
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {
    private val apiService = RegisterApiService()

    // UI state
    var name by mutableStateOf("")
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var confirmPassword by mutableStateOf("")

    // UI state for visibility
    var passwordVisible by mutableStateOf(false)
    var confirmPasswordVisible by mutableStateOf(false)

    // UI state for loading and errors
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf("")

    val specialCharacters =
        listOf("!", "@", "#", "$", "%", "^", "&", "*", "(", ")", "_", "-", "+", "=")
    val numbers = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0")

    // Validation functions
    fun validateInputs(): Boolean {
        when {
            name.isBlank() || email.isBlank() || password.isBlank() || confirmPassword.isBlank() -> {
                errorMessage = "All fields are required"
                return false
            }

            !email.contains("@") || !email.contains(".") -> {
                errorMessage = "Please enter a valid email address"
                return false
            }

            !(password.length >= 8) || !(specialCharacters.any {
                password.contains(it)
            }) || !(numbers.any {
                password.contains(it)
            }) -> {
                errorMessage =
                    "Password must be at least 8 characters long and contain at least one special character and number"
                return false
            }

            password != confirmPassword -> {
                errorMessage = "Passwords do not match"
                return false
            }

            else -> {
                errorMessage = ""
                return true
            }
        }
    }

    fun register(
        onSuccess: () -> Unit,
        saveUserData: (String) -> Unit
    ) {
        if (!validateInputs()) return

        isLoading = true

        viewModelScope.launch {
            try {
                val response = apiService.registerUser(name, email, password)

                isLoading = false

                if (response.success) {
                    // Save user data locally
                    saveUserData(name)
                    onSuccess()
                } else {
                    errorMessage = response.message
                }
            } catch (e: Exception) {
                isLoading = false
                errorMessage = "Registration failed: ${e.message}"
            }
        }
    }
}