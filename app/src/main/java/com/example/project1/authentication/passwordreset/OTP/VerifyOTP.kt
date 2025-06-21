package com.example.project1.authentication.passwordreset.OTP

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.preference.PreferenceManager
import com.example.project1.authentication.CommonComponents.*
import com.example.project1.viewmodel.VerifyCodeUiState
import com.example.project1.viewmodel.VerifyCodeViewModel
import kotlinx.coroutines.delay

@Composable
fun VerifyCodeScreen(
    navController: NavController,
    email: String = "",
    token: String = "",
    fromLogin: Boolean,
    viewModel: VerifyCodeViewModel = viewModel(),

) {
    val uiState by viewModel.uiState.collectAsState()
    val otpValue by viewModel.otpValue.collectAsState()

    // Get the context for shared preferences
    val context = LocalContext.current

    // Get email and token from shared preferences if not provided directly
    LaunchedEffect(Unit) {
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)
        val savedEmail = sharedPrefs.getString("VERIFY_EMAIL", "") ?: ""
        val savedToken = sharedPrefs.getString("VERIFY_TOKEN", "") ?: ""

        android.util.Log.d("VerifyCodeScreen", "From SharedPrefs: email=$savedEmail, token=$savedToken")

        // Use parameters if provided, otherwise use shared preferences
        val finalEmail = if (email.isNotEmpty()) email else savedEmail
        val finalToken = if (token.isNotEmpty()) token else savedToken

        if (finalEmail.isNotEmpty() && finalToken.isNotEmpty()) {
            android.util.Log.d("VerifyCodeScreen", "Setting in ViewModel: email=$finalEmail, token=$finalToken")
            viewModel.setEmailAndToken(finalEmail, finalToken)
        }
    }

    // Handle UI state changes
    LaunchedEffect(uiState) {
        when (uiState) {
            is VerifyCodeUiState.Success -> {
                // Navigate to the next screen (Reset Password or Home)
                delay(500) // Brief delay for better UX

                if(!fromLogin) {

                    navController.navigate("login") {
                        // Clear the back stack to prevent going back to OTP screen
                        popUpTo("verify_code") { inclusive = true }
                    }
                }
            }

            is VerifyCodeUiState.ResendSuccess -> {
                // Show success message briefly
                delay(3000)
                viewModel.resetUiState()
            }

            else -> {}
        }
    }

    val onBackClick: () -> Unit = { navController.popBackStack() }

    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(android.graphics.Color.parseColor("#101F31")))
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                AppHeader(
                    title = "Check your email",
                    onBackClick = onBackClick
                )

                Spacer(modifier = Modifier.height(40.dp))

                // Display debug info in UI during development
                if (viewModel.getEmail().isEmpty()) {
                    Text(
                        text = "DEBUG - Email not set in ViewModel!",
                        color = Color.Red,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Params: email=$email, token=$token",
                        color = Color.Yellow,
                        fontSize = 12.sp
                    )

                    // Show shared preferences values
                    val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)
                    val savedEmail = sharedPrefs.getString("VERIFY_EMAIL", "") ?: ""
                    val savedToken = sharedPrefs.getString("VERIFY_TOKEN", "") ?: ""

                    Text(
                        text = "SharedPrefs: email=$savedEmail, token=$savedToken",
                        color = Color.Yellow,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Get email from ViewModel or SharedPreferences as fallback
                val displayEmail = viewModel.getEmail().ifEmpty {
                    PreferenceManager.getDefaultSharedPreferences(context)
                        .getString("VERIFY_EMAIL", "") ?: ""
                }

                Text(
                    text = "We sent an OTP to $displayEmail",
                    color = Color.White,
                    fontSize = 16.sp
                )

                Text(
                    text = "Enter 6 digit code mentioned in the email.",
                    color = Color.White,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                // Simple OTP Input field
                SimpleOtpInputField(
                    otpValue = otpValue,
                    onOtpValueChange = { viewModel.updateOtpValue(it) },
                    isEnabled = uiState !is VerifyCodeUiState.Loading &&
                            uiState !is VerifyCodeUiState.ResendLoading
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Modified verify button to use stored values if ViewModel values are empty
                PrimaryButton(
                    text = "Verify Code",
                    onClick = {
                        // If email in ViewModel is empty, set it from SharedPreferences before verifying
                        if (viewModel.getEmail().isEmpty()) {
                            val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)
                            val savedEmail = sharedPrefs.getString("VERIFY_EMAIL", "") ?: ""
                            val savedToken = sharedPrefs.getString("VERIFY_TOKEN", "") ?: ""

                            if (savedEmail.isNotEmpty() && savedToken.isNotEmpty()) {
                                viewModel.setEmailAndToken(savedEmail, savedToken)
                                android.util.Log.d("VerifyCodeScreen", "Setting email from SharedPrefs before verify: $savedEmail")
                            }
                        }
                        viewModel.verifyOtp(navController, context, fromLogin)
                    },
                    enabled = uiState !is VerifyCodeUiState.Loading &&
                            uiState !is VerifyCodeUiState.ResendLoading &&
                            otpValue.length == 6
                )

                Spacer(modifier = Modifier.height(16.dp))

                TextWithLink(
                    regularText = "Haven't got the email yet?",
                    linkText = "Resend email",
                    onLinkClick = {
                        // If email in ViewModel is empty, set it from SharedPreferences before resending
                        if (viewModel.getEmail().isEmpty()) {
                            val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)
                            val savedEmail = sharedPrefs.getString("VERIFY_EMAIL", "") ?: ""
                            val savedToken = sharedPrefs.getString("VERIFY_TOKEN", "") ?: ""

                            if (savedEmail.isNotEmpty()) {
                                viewModel.setEmailAndToken(savedEmail, savedToken)
                            }
                        }
                        viewModel.resendOtp()
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    isEnabled = uiState !is VerifyCodeUiState.Loading &&
                            uiState !is VerifyCodeUiState.ResendLoading
                )

                // Error message
                if (uiState is VerifyCodeUiState.Error) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = (uiState as VerifyCodeUiState.Error).message,
                        color = Color.Red,
                        fontSize = 14.sp,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }

                // Success message after resending
                if (uiState is VerifyCodeUiState.ResendSuccess) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "OTP resent successfully!",
                        color = Color.Green,
                        fontSize = 14.sp,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }

            // Loading indicators
            if (uiState is VerifyCodeUiState.Loading || uiState is VerifyCodeUiState.ResendLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun VerifyCodeScreenPreview() {
    VerifyCodeScreen(
        email = "user@example.com",
        token = "sample_token",
        navController = rememberNavController(),
        fromLogin = false
    )
}
