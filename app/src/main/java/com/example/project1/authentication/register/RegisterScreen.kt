package com.example.project1.authentication.register

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
    onNavigateToVerifyOTP: () -> Unit,
    viewModel: RegisterViewModel = viewModel()
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E293B))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .background(Color(0xFF1E293B))
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.SpaceAround,
        ) {
            Column {
                Spacer(modifier = Modifier.height(96.dp))
                Text(
                    text = "Sign Up",
                    fontWeight = FontWeight.Bold,
                    fontSize = 40.sp,
                    color = Color.White,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                )

                Spacer(modifier = Modifier.height(48.dp))

                // Name TextField
                Text("Name", color = Color.White, fontSize = 16.sp)
                StandardTextField(
                    value = viewModel.name,
                    onValueChange = { viewModel.name = it },
                    hint = "Name",
                    leadingIcon = {
                        Icon(
                            Icons.Default.AccountCircle,
                            contentDescription = null,
                            tint = Color.White
                        )
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
                        Icon(
                            Icons.Default.Email,
                            contentDescription = null,
                            tint = Color.White
                        )
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
                        Icon(
                            Icons.Default.Lock,
                            contentDescription = null,
                            tint = Color.White
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = {
                            viewModel.passwordVisible = !viewModel.passwordVisible
                        }) {
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
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF334155)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Text(
                                text = "Password requirements:",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            passwordHints.forEach { hint ->
                                Text(
                                    text = hint,
                                    color = Color(0xFFEF4444),
                                    fontSize = 11.sp
                                )
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
                        Icon(
                            Icons.Default.Lock,
                            contentDescription = null,
                            tint = Color.White
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = {
                            viewModel.confirmPasswordVisible = !viewModel.confirmPasswordVisible
                        }) {
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

                // Display error message if there is one
                if (viewModel.errorMessage.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(15.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFDC2626).copy(alpha = 0.1f)
                        )
                    ) {
                        Text(
                            text = viewModel.errorMessage,
                            color = Color(0xFFEF4444),
                            fontSize = 14.sp,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))

                Button(
                    onClick = {
                        viewModel.register(
                            onSuccess = {
                                onNavigateToVerifyOTP()
                            },
                            saveUserData = { username ->
                                CoroutineScope(Dispatchers.IO).launch {
                                    DataStoreManager.saveUsername(context, username)
                                }
                            }
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF76808A)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    enabled = !viewModel.isLoading
                ) {
                    if (viewModel.isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text(
                            text = "Sign up",
                            fontSize = 16.sp,
                            color = Color.White,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))
                Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                    Text(
                        text = "Have an account? ",
                        fontSize = 14.sp,
                        color = Color.White,
                    )
                    Text(
                        text = "Login",
                        fontSize = 14.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable {
                            onNavigateToLogin()
                        }
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Preview
@Composable
fun RegisterScreenPreview() {
    RegisterScreen(onNavigateToLogin = {}, onNavigateToVerifyOTP = {})
}