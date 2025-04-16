package com.example.project1.authentication.passwordreset

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
import com.example.project1.authentication.CommonComponents.*
import com.example.project1.viewmodel.VerifyEmailUiState
import com.example.project1.viewmodel.VerifyEmailViewModel

@Composable
fun VerifyEmailScreen(
    navController: NavController,
    viewModel: VerifyEmailViewModel = viewModel(),
    fromLogin: Boolean? = false,
) {
    var email by remember { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val onBackClick: () -> Unit = { navController.popBackStack() }

    // Handle navigation after successful OTP sent
    LaunchedEffect(uiState) {
        when (uiState) {
            is VerifyEmailUiState.Success -> {
                val successState = uiState as VerifyEmailUiState.Success

                // Log the values for debugging
                android.util.Log.d(
                    "VerifyEmailScreen ${fromLogin}",
                    "Success! Email: ${successState.email}, Token: ${successState.token}"
                )

                // Store email and token in shared preferences for persistence
                val sharedPrefs = androidx.preference.PreferenceManager.getDefaultSharedPreferences(
                    context
                )
                sharedPrefs.edit()
                    .putString("VERIFY_EMAIL", successState.email)
                    .putString("VERIFY_TOKEN", successState.token)
                    .apply()

                // Navigate to OTP verification screen
                navController.navigate("verify_code/${fromLogin?:false}")
            }

            else -> {}
        }
    }

    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkNavyBlue)
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                AppHeader(
                    title = if (fromLogin == true) "Reset Password" else "Verify Email",
                    onBackClick = onBackClick
                )

                Spacer(modifier = Modifier.height(40.dp))

                Text(
                    text = "Please enter your email ${if (fromLogin == true) "to reset the password" else "to verify your account"}",
                    color = Color.White,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                EmailTextField(
                    value = email,
                    onValueChange = { email = it },
                    isEnabled = uiState !is VerifyEmailUiState.Loading
                )

                Spacer(modifier = Modifier.height(24.dp))

                PrimaryButton(
                    text = "Send OTP",
                    onClick = {
                        viewModel.sendOtp(email, fromLogin, navController)
                    },
                    enabled = uiState !is VerifyEmailUiState.Loading,
                )

                // Error message
                if (uiState is VerifyEmailUiState.Error) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = (uiState as VerifyEmailUiState.Error).message,
                        color = Color.Red,
                        fontSize = 14.sp
                    )
                }
            }

            // Loading indicator
            if (uiState is VerifyEmailUiState.Loading) {
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
fun VerifyEmailScreenPreview() {
    VerifyEmailScreen(
        navController = rememberNavController(),
        fromLogin = true
    )
}
