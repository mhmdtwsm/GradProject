package com.example.project1.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.project1.authentication.passwordreset.OTP.VerifyOtpApiService
import com.example.project1.authentication.passwordreset.verifyemail.SendOtpApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Import the Screen object that contains route definitions
// If it's in a different package, make sure to import it correctly
import Screen
import android.content.Context
import com.example.project1.DataStoreManager

class VerifyCodeViewModel : ViewModel() {

    private val otpApiService = VerifyOtpApiService()
    private val sendOtpApiService = SendOtpApiService()

    private val _uiState = MutableStateFlow<VerifyCodeUiState>(VerifyCodeUiState.Initial)
    val uiState: StateFlow<VerifyCodeUiState> = _uiState

    // OTP input state as a single string
    private val _otpValue = MutableStateFlow("")
    val otpValue: StateFlow<String> = _otpValue

    // Email and token received from previous screen
    private var email: String = ""
    private var otpToken: String = ""

    // Setter for email and token
    fun setEmailAndToken(email: String, token: String) {
        this.email = email
        this.otpToken = token
    }

    // Getter for email
    fun getEmail(): String = email

    // Update OTP value
    fun updateOtpValue(value: String) {
        _otpValue.value = value
    }

    // Reset UI state to initial
    fun resetUiState() {
        _uiState.value = VerifyCodeUiState.Initial
    }

    // Verify OTP code
    fun verifyOtp(navController: NavController, context: Context, fromLogin: Boolean = false) {
        val otpCode = _otpValue.value

        // Debug logging to help troubleshoot
        android.util.Log.d(
            "OTPVerification",
            "Verifying OTP: Email=$email, Token=$otpToken, OTP=$otpCode"
        )

        // Validate input data
        if (email.isEmpty()) {
            _uiState.value = VerifyCodeUiState.Error("Email not provided")
            android.util.Log.e("OTPVerification", "ERROR: Email is empty when verifying OTP")
            return
        }

        if (otpToken.isEmpty()) {
            _uiState.value = VerifyCodeUiState.Error("Please request a new OTP first")
            android.util.Log.e("OTPVerification", "ERROR: Token is empty when verifying OTP")
            return
        }

        // Validate OTP code length
        if (otpCode.length != 6 || otpCode.any { !it.isDigit() }) {
            _uiState.value = VerifyCodeUiState.Error("Please enter a valid 6-digit OTP code")
            return
        }

        _uiState.value = VerifyCodeUiState.Loading

        viewModelScope.launch {
            try {
                android.util.Log.d(
                    "OTPVerification",
                    "Making API call with Email=$email, Token=$otpToken, OTP=$otpCode"
                )
                val response = otpApiService.verifyOtp(email, otpToken, otpCode, fromLogin)
                if (response.success) {
                    _uiState.value = VerifyCodeUiState.Success

                    // Navigate to login screen
                    if (fromLogin) {
                        // Coming from forgot password flow - go to reset password
                        navController.navigate("resetPassword/$email") {
                            popUpTo("verify_code") { inclusive = true }
                        }
                    } else {
                        // Coming from registration flow - go to login
                        navController.navigate(Screen.Login.route) {
                            popUpTo("verify_code") { inclusive = true }
                        }
                    }
                } else {
                    _uiState.value = VerifyCodeUiState.Error(response.message)
                }
            } catch (e: Exception) {
                _uiState.value = VerifyCodeUiState.Error("Error: ${e.message}")
            }
        }
    }

    // Resend OTP
    fun resendOtp() {
        if (email.isEmpty()) {
            _uiState.value = VerifyCodeUiState.Error("Email not provided")
            return
        }

        _uiState.value = VerifyCodeUiState.ResendLoading

        viewModelScope.launch {
            try {
                val response = sendOtpApiService.sendOtp(email)
                if (response.success) {
                    // Store the token from the response
                    response.token?.let {
                        otpToken = it
                    }

                    _uiState.value = VerifyCodeUiState.ResendSuccess
                    // Reset OTP value
                    _otpValue.value = ""
                } else {
                    _uiState.value = VerifyCodeUiState.Error(response.message)
                }
            } catch (e: Exception) {
                _uiState.value = VerifyCodeUiState.Error("Error: ${e.message}")
            }
        }
    }

    // Add this function to help with debugging
    fun dumpState() {
        android.util.Log.d("VerifyOTPViewModel", "Current state: email=$email, token=$otpToken")
    }
}

// UI states for OTP verification screen
sealed class VerifyCodeUiState {
    object Initial : VerifyCodeUiState()
    object Loading : VerifyCodeUiState()
    object Success : VerifyCodeUiState()
    object ResendLoading : VerifyCodeUiState()
    object ResendSuccess : VerifyCodeUiState()
    data class Error(val message: String) : VerifyCodeUiState()
}
