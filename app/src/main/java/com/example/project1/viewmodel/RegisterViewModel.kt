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

    // Enhanced validation functions
    private fun validatePassword(password: String): String? {
        return when {
            password.length < 6 -> "Password must be at least 6 characters long"
            password.length < 8 -> "Password is too short. Password must be at least 8 characters long"
            !password.any { it.isDigit() } -> "Password must have at least one number (0-9)"
            !password.any { it.isLowerCase() } -> "Password must have at least one lowercase letter (a-z)"
            !password.any { it.isUpperCase() } -> "Password must have at least one uppercase letter (A-Z)"
            !specialCharacters.any { password.contains(it) } -> "Password must contain at least one special character (!@#$%^&*()_-+=)"
            else -> null // Password is valid
        }
    }

    fun validateInputs(): Boolean {
        when {
            name.isBlank() -> {
                errorMessage = "Name is required"
                return false
            }

            email.isBlank() -> {
                errorMessage = "Email is required"
                return false
            }

            !email.contains("@") || !email.contains(".") -> {
                errorMessage = "Please enter a valid email address"
                return false
            }

            password.isBlank() -> {
                errorMessage = "Password is required"
                return false
            }

            confirmPassword.isBlank() -> {
                errorMessage = "Please confirm your password"
                return false
            }

            else -> {
                // Validate password with specific error messages
                val passwordError = validatePassword(password)
                if (passwordError != null) {
                    errorMessage = passwordError
                    return false
                }

                // Check if passwords match
                if (password != confirmPassword) {
                    errorMessage = "Passwords do not match"
                    return false
                }

                errorMessage = ""
                return true
            }
        }
    }

    // Real-time password validation for better UX
    fun getPasswordValidationHints(): List<String> {
        val hints = mutableListOf<String>()

        if (password.isNotEmpty()) {
            if (password.length < 6) {
                hints.add("• At least 6 characters")
            } else if (password.length < 8) {
                hints.add("• At least 8 characters (recommended)")
            }

            if (!password.any { it.isDigit() }) {
                hints.add("• At least one number (0-9)")
            }

            if (!password.any { it.isLowerCase() }) {
                hints.add("• At least one lowercase letter (a-z)")
            }

            if (!password.any { it.isUpperCase() }) {
                hints.add("• At least one uppercase letter (A-Z)")
            }

            if (!specialCharacters.any { password.contains(it) }) {
                hints.add("• At least one special character (!@#$%^&*()_-+=)")
            }
        }

        return hints
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