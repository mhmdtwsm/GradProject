package com.example.project1.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.project1.authentication.passwordreset.verifyemail.SendOtpApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import Screen

class VerifyEmailViewModel : ViewModel() {

    private val apiService = SendOtpApiService()

    private val _uiState = MutableStateFlow<VerifyEmailUiState>(VerifyEmailUiState.Initial)
    val uiState: StateFlow<VerifyEmailUiState> = _uiState

    fun sendOtp(email: String, fromLogin: Boolean? = false, navController: NavController) {
        if (email.isBlank()) {
            _uiState.value = VerifyEmailUiState.Error("Please enter your email")
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _uiState.value = VerifyEmailUiState.Error("Please enter a valid email")
            return
        }

        _uiState.value = VerifyEmailUiState.Loading

        viewModelScope.launch {
            try {
                val response = apiService.sendOtp(email, fromLogin ?: false)
                if (response.success && response.token != null) {
                    _uiState.value = VerifyEmailUiState.Success(email, response.token)

                    // URL encode the parameters
                    val encodedEmail = java.net.URLEncoder.encode(email, "UTF-8")
                    val encodedToken = java.net.URLEncoder.encode(response.token, "UTF-8")

//                    // Use the createRoute function
//                    val route = Screen.VerifyCode.createRoute(
//                        encodedEmail,
//                        encodedToken,
//                        fromLogin ?: false
//                    )
//
//                    android.util.Log.d("EmailVerification", "Navigation route: $route")

                    navController.navigate("verify_code/${fromLogin ?: false}")
                } else {
                    _uiState.value =
                        VerifyEmailUiState.Error(response.message ?: "Failed to send OTP")
                }
            } catch (e: Exception) {
                _uiState.value = VerifyEmailUiState.Error("Error: ${e.message}")
            }
        }
    }
}

// UI state for Verify Email screen
sealed class VerifyEmailUiState {
    object Initial : VerifyEmailUiState()
    object Loading : VerifyEmailUiState()
    data class Success(val email: String, val token: String) : VerifyEmailUiState()
    data class Error(val message: String) : VerifyEmailUiState()
}
