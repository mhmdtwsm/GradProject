package com.example.project1.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.preference.PreferenceManager
import com.example.project1.authentication.passwordreset.verifyemail.SendOtpApiService
import com.example.project1.authentication.register.RegisterApiService
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {
    private val apiService = RegisterApiService()
    private val sendOtpApiService = SendOtpApiService()

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

    private fun validateInputs(): Boolean {
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
        context: Context,
        onSuccess: () -> Unit,
        saveUserData: (String) -> Unit
    ) {
        if (!validateInputs()) return

        isLoading = true
        errorMessage = ""

        viewModelScope.launch {
            try {
                Log.d("RegisterViewModel", "Starting registration for email: $email")

                // Step 1: Register the user
                val registerResponse = apiService.registerUser(name, email, password)

                if (registerResponse.success) {
                    Log.d("RegisterViewModel", "Registration successful, sending OTP...")

                    // Step 2: Automatically send OTP to the registered email
                    val otpResponse = sendOtpApiService.sendOtp(email, fromLogin = false)

                    if (otpResponse.success && otpResponse.token != null) {
                        Log.d(
                            "RegisterViewModel",
                            "OTP sent successfully, token: ${otpResponse.token}"
                        )

                        // Step 3: Store email and token in SharedPreferences for OTP verification
                        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)
                        sharedPrefs.edit()
                            .putString("VERIFY_EMAIL", email)
                            .putString("VERIFY_TOKEN", otpResponse.token)
                            .apply()

                        Log.d("RegisterViewModel", "Email and token stored in SharedPreferences")

                        // Step 4: Save user data locally
                        saveUserData(name)

                        isLoading = false
                        errorMessage = ""

                        // Step 5: Navigate directly to OTP verification screen
                        onSuccess()

                    } else {
                        isLoading = false
                        errorMessage =
                            "Registration successful but failed to send verification code. Please try again."
                        Log.e("RegisterViewModel", "Failed to send OTP: ${otpResponse.message}")
                    }
                } else {
                    isLoading = false
                    errorMessage = registerResponse.message
                    Log.e("RegisterViewModel", "Registration failed: ${registerResponse.message}")
                }
            } catch (e: Exception) {
                isLoading = false
                errorMessage = "Registration failed: ${e.message}"
                Log.e("RegisterViewModel", "Registration exception: ${e.message}", e)
            }
        }
    }

    // Clear error message when user starts typing
    fun clearError() {
        errorMessage = ""
    }
}