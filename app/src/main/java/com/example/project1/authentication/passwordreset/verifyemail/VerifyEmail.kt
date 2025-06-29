package com.example.project1.authentication.passwordreset.verifyemail

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.project1.authentication.CommonComponents.StandardTextField
import com.example.project1.ui.theme.Project1Theme
import com.example.project1.viewmodel.VerifyEmailUiState
import com.example.project1.viewmodel.VerifyEmailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerifyEmailScreen(
    navController: NavController,
    fromLogin: Boolean? = false,
) {
    val viewModel: VerifyEmailViewModel = viewModel()
    var email by remember { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Handle navigation after successful OTP sent
    LaunchedEffect(uiState) {
        if (uiState is VerifyEmailUiState.Success) {
            val successState = uiState as VerifyEmailUiState.Success
            val sharedPrefs = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
            sharedPrefs.edit()
                .putString("VERIFY_EMAIL", successState.email)
                .putString("VERIFY_TOKEN", successState.token)
                .apply()
            navController.navigate("verify_code/${fromLogin ?: false}")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (fromLogin == true) "Reset Password" else "Verify Email") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "Please enter your email ${if (fromLogin == true) "to reset the password" else "to verify your account"}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            StandardTextField(
                value = email,
                onValueChange = { email = it },
                hint = "Enter your email",
                leadingIcon = { Icon(imageVector = Icons.Default.Email, contentDescription = "Email") },
                keyboardType = KeyboardType.Email,
                enabled = uiState !is VerifyEmailUiState.Loading
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { viewModel.sendOtp(email, fromLogin, navController) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = uiState !is VerifyEmailUiState.Loading
            ) {
                if (uiState is VerifyEmailUiState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                } else {
                    Text("Send OTP")
                }
            }

            // Error message
            if (uiState is VerifyEmailUiState.Error) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = (uiState as VerifyEmailUiState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun VerifyEmailScreenPreview() {
    Project1Theme {
        VerifyEmailScreen(
            navController = rememberNavController(),
            fromLogin = true
        )
    }
}