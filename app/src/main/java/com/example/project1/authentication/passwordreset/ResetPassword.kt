package com.example.project1.authentication.passwordreset

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.project1.R
import com.example.project1.authentication.CommonComponents.*
import com.example.project1.viewmodel.PasswordResetViewModel

@Composable
fun ResetPasswordScreen(
    navController: NavController,
    onBackClick: () -> Unit,
    email: String,
    viewModel: PasswordResetViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var isPasswordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearError()
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
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                AppHeader(
                    title = "Reset Password",
                    onBackClick = {navController.popBackStack()}
                )

                Spacer(modifier = Modifier.height(40.dp))

                Text(
                    text = "Create a new password. Ensure it differs from previous ones for security.\nEmail: $email",
                    color = Color.White,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                StandardTextField(
                    value = uiState.password,
                    onValueChange = { viewModel.updatePassword(it) },
                    hint = "Password",
                    isPassword = if (isPasswordVisible) false else true,
                    keyboardType = KeyboardType.Password,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Password",
                            tint = Color.White
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                            Icon(
                                painterResource(id = if (isPasswordVisible) R.drawable.eyenorm else R.drawable.eyeslash),
                                contentDescription = if (isPasswordVisible) "Hide password" else "Show password",
                                tint = Color.White
                            )
                        }

                    },
                )

                Spacer(modifier = Modifier.height(16.dp))

                StandardTextField(
                    value = uiState.confirmPassword,
                    onValueChange = { viewModel.updateConfirmPassword(it) },
                    hint = "Confirm Password",
                    isPassword = if (isPasswordVisible) false else true,
                    keyboardType = KeyboardType.Password,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Password",
                            tint = Color.White
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                            Icon(
                                painterResource(id = if (isPasswordVisible) R.drawable.eyenorm else R.drawable.eyeslash),
                                contentDescription = if (isPasswordVisible) "Hide password" else "Show password",
                                tint = Color.White
                            )
                        }

                    },
                )

                Spacer(modifier = Modifier.height(24.dp))

                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        color = Color.White
                    )
                } else {
                    PrimaryButton(
                        text = "Update Password",
                        onClick = {
                            viewModel.resetPassword(email) {
                                Toast.makeText(
                                    context,
                                    "Password has been reset successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                                navController.navigate("login") {
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                        },
                        enabled = viewModel.passwordsMatch()
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ResetPasswordScreenPreview() {
    ResetPasswordScreen(
        onBackClick = { /* TODO */ },
        navController = rememberNavController(),
        email = "user@example.com"
    )
}