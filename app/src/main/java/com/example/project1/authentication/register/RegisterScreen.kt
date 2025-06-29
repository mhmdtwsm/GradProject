package com.example.project1.authentication.register

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.project1.DataStoreManager
import com.example.project1.authentication.CommonComponents.StandardTextField
import com.example.project1.ui.theme.Project1Theme
import com.example.project1.viewmodel.RegisterViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToVerifyOTP: () -> Unit,
    viewModel: RegisterViewModel = viewModel()
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    LaunchedEffect(viewModel.name, viewModel.email, viewModel.password, viewModel.confirmPassword) {
        if (viewModel.errorMessage.isNotEmpty()) {
            viewModel.clearError()
        }
    }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(80.dp))

            // --- REPLACEMENT: Register Title ---
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Create Account",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Join us and stay protected",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            // --- END REPLACEMENT ---

            Spacer(modifier = Modifier.height(48.dp))

            // Form Fields
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StandardTextField(
                    value = viewModel.name,
                    onValueChange = { viewModel.name = it },
                    hint = "Name",
                    leadingIcon = { Icon(Icons.Default.AccountCircle, contentDescription = "Name") },
                    keyboardType = KeyboardType.Text
                )
                StandardTextField(
                    value = viewModel.email,
                    onValueChange = { viewModel.email = it },
                    hint = "Email",
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email") },
                    keyboardType = KeyboardType.Email
                )
                StandardTextField(
                    value = viewModel.password,
                    onValueChange = { viewModel.password = it },
                    hint = "Password",
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password") },
                    trailingIcon = {
                        IconButton(onClick = { viewModel.passwordVisible = !viewModel.passwordVisible }) {
                            Icon(
                                imageVector = if (viewModel.passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (viewModel.passwordVisible) "Hide password" else "Show password"
                            )
                        }
                    },
                    isPassword = !viewModel.passwordVisible,
                    keyboardType = KeyboardType.Password
                )

                PasswordValidationHints(viewModel)

                StandardTextField(
                    value = viewModel.confirmPassword,
                    onValueChange = { viewModel.confirmPassword = it },
                    hint = "Confirm Password",
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Confirm Password") },
                    isPassword = !viewModel.confirmPasswordVisible,
                    keyboardType = KeyboardType.Password
                )

                if (viewModel.confirmPassword.isNotEmpty() && viewModel.password != viewModel.confirmPassword) {
                    Text(
                        text = "Passwords do not match",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            }

            // Error message display
            if (viewModel.errorMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                ) {
                    Text(
                        text = viewModel.errorMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Register Button
            Button(
                onClick = {
                    viewModel.register(
                        context = context,
                        onSuccess = { onNavigateToVerifyOTP() },
                        saveUserData = { username ->
                            CoroutineScope(Dispatchers.IO).launch {
                                DataStoreManager.saveUsername(context, username)
                            }
                        }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = !viewModel.isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                if (viewModel.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Register", style = MaterialTheme.typography.titleSmall)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Login link
            Row {
                Text(
                    text = "Have an account? ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Login",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onNavigateToLogin() }
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun PasswordValidationHints(viewModel: RegisterViewModel) {
    val passwordHints = viewModel.getPasswordValidationHints()
    if (passwordHints.isNotEmpty() && viewModel.password.isNotEmpty()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                Text(
                    text = "Password requirements:",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                passwordHints.forEach { hint ->
                    Text(
                        // Using a simple bullet point for clarity
                        text = "• $hint",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    Project1Theme {
        RegisterScreen(
            onNavigateToLogin = {},
            onNavigateToVerifyOTP = {}
        )
    }
}