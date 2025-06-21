package com.example.project1.authentication.register

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.project1.DataStoreManager
import com.example.project1.R
import com.example.project1.authentication.CommonComponents.StandardTextField
import com.example.project1.viewmodel.RegisterViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(
    modifier: Modifier = Modifier,
    onNavigateToLogin: () -> Unit,
    onNavigateToVerifyOTP: () -> Unit, // Changed back to no parameters since we store email in SharedPreferences
    viewModel: RegisterViewModel = viewModel()
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Clear error message when user starts interacting
    LaunchedEffect(viewModel.name, viewModel.email, viewModel.password, viewModel.confirmPassword) {
        if (viewModel.errorMessage.isNotEmpty()) {
            viewModel.clearError()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(android.graphics.Color.parseColor("#101F30")))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.SpaceAround
        ) {
            // Header with illustration
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.height(50.dp))
                Image(
                    painter = painterResource(id = R.drawable.register),
                    contentDescription = "Register illustration",
                    modifier = Modifier
                        .size(230.dp)
                        .padding(top = 16.dp)
                )
            }

            Spacer(modifier = Modifier.height(0.dp))

            // Name TextField
            Text("Name", color = Color.White, fontSize = 16.sp)
            StandardTextField(
                value = viewModel.name,
                onValueChange = { viewModel.name = it },
                hint = "Name",
                leadingIcon = {
                    Icon(Icons.Default.AccountCircle, contentDescription = null, tint = Color.White)
                },
                keyboardType = KeyboardType.Text
            )

            // Email TextField
            Spacer(modifier = Modifier.height(15.dp))
            Text("Email", color = Color.White, fontSize = 16.sp)
            StandardTextField(
                value = viewModel.email,
                onValueChange = { viewModel.email = it },
                hint = "Email",
                leadingIcon = {
                    Icon(Icons.Default.Email, contentDescription = null, tint = Color.White)
                },
                keyboardType = KeyboardType.Email
            )

            // Password TextField
            Spacer(modifier = Modifier.height(15.dp))
            Text("Password", color = Color.White, fontSize = 16.sp)
            StandardTextField(
                value = viewModel.password,
                onValueChange = { viewModel.password = it },
                hint = "Password",
                leadingIcon = {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = Color.White)
                },
                trailingIcon = {
                    IconButton(onClick = { viewModel.passwordVisible = !viewModel.passwordVisible }) {
                        Icon(
                            painterResource(id = if (viewModel.passwordVisible) R.drawable.eyeslash else R.drawable.eyenorm),
                            contentDescription = if (viewModel.passwordVisible) "Hide password" else "Show password",
                            tint = Color.White
                        )
                    }
                },
                isPassword = !viewModel.passwordVisible,
                keyboardType = KeyboardType.Password
            )

            // Password validation hints
            val passwordHints = viewModel.getPasswordValidationHints()
            if (passwordHints.isNotEmpty() && viewModel.password.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF334155))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Password requirements:",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        passwordHints.forEach { hint ->
                            Text(text = hint, color = Color(0xFFEF4444), fontSize = 11.sp)
                        }
                    }
                }
            }

            // Confirm Password TextField
            Spacer(modifier = Modifier.height(15.dp))
            Text("Confirm Password", color = Color.White, fontSize = 16.sp)
            StandardTextField(
                value = viewModel.confirmPassword,
                onValueChange = { viewModel.confirmPassword = it },
                hint = "Confirm Password",
                leadingIcon = {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = Color.White)
                },
                trailingIcon = {
                    IconButton(onClick = { viewModel.confirmPasswordVisible = !viewModel.confirmPasswordVisible }) {
                        Icon(
                            painterResource(id = if (viewModel.confirmPasswordVisible) R.drawable.eyeslash else R.drawable.eyenorm),
                            contentDescription = if (viewModel.confirmPasswordVisible) "Hide password" else "Show password",
                            tint = Color.White
                        )
                    }
                },
                isPassword = !viewModel.confirmPasswordVisible,
                keyboardType = KeyboardType.Password
            )

            // Password mismatch warning
            if (viewModel.confirmPassword.isNotEmpty() &&
                viewModel.password.isNotEmpty() &&
                viewModel.password != viewModel.confirmPassword
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Passwords do not match",
                    color = Color(0xFFEF4444),
                    fontSize = 12.sp
                )
            }

            // Error message display
            if (viewModel.errorMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(15.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFDC2626).copy(alpha = 0.1f))
                ) {
                    Text(
                        text = viewModel.errorMessage,
                        color = Color(0xFFAB3333),
                        fontSize = 14.sp,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // Register Button - Updated with enhanced functionality
            Button(
                onClick = {
                    viewModel.register(
                        context = context, // Pass context for SharedPreferences
                        onSuccess = {
                            // Navigate directly to OTP verification screen
                            // Email and token are already stored in SharedPreferences by ViewModel
                            onNavigateToVerifyOTP()
                        },
                        saveUserData = { username ->
                            CoroutineScope(Dispatchers.IO).launch {
                                DataStoreManager.saveUsername(context, username)
                            }
                        }
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E3B4E)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = !viewModel.isLoading
            ) {
                if (viewModel.isLoading) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Creating account...",
                            fontSize = 14.sp,
                            color = Color.White
                        )
                    }
                } else {
                    Text(text = "Register", fontSize = 16.sp, color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // Login link
            Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Text(text = "Have an account? ", fontSize = 14.sp, color = Color.White)
                Text(
                    text = "Login",
                    fontSize = 14.sp,
                    color = Color(0xFF4FC3F7),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onNavigateToLogin() }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Preview
@Composable
fun RegisterScreenPreview() {
    RegisterScreen(
        onNavigateToLogin = {},
        onNavigateToVerifyOTP = {}
    )
}