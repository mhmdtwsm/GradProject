package com.example.project1.authentication.login

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.project1.authentication.CommonComponents.StandardTextField
import com.example.project1.viewmodel.LoginUiState
import com.example.project1.viewmodel.LoginViewModel

@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    viewModel: LoginViewModel = viewModel(),
    navController: NavController
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val fromLogin: Boolean = true

    // State for text fields
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }


    // Handle login success
    LaunchedEffect(uiState) {

        if (uiState is LoginUiState.Success) {
            Toast.makeText(context, "Login Successful!", Toast.LENGTH_SHORT).show()
            onNavigateToHome()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E293B))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .background(Color(0xFF1E293B)),
        ) {
            Column {
                Spacer(modifier = Modifier.height(180.dp))
                Text(
                    text = "Sign in",
                    fontWeight = FontWeight.Bold,
                    fontSize = 40.sp,
                    color = Color.White,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                )

                Spacer(modifier = Modifier.height(96.dp))
                Text("Email", color = Color.White, fontSize = 16.sp)
                // Email field using StandardTextField
                StandardTextField(
                    value = email,
                    onValueChange = { email = it },
                    hint = "Email",
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = "Email",
                            tint = Color.White
                        )
                    },
                    keyboardType = KeyboardType.Email
                )

                Spacer(modifier = Modifier.height(15.dp))

                // Password field using StandardTextField
                Text("Password", color = Color.White, fontSize = 16.sp)

                StandardTextField(
                    value = password,
                    onValueChange = { password = it },
                    hint = "Password",
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Password",
                            tint = Color.White
                        )
                    },
                    trailingIcon = {
                        Text(
                            text = "Forgot Password?",
                            color = Color.White,
                            fontSize = 12.sp,
                            modifier = Modifier
                                .clickable { navController.navigate("verifyemail/$fromLogin") }
                                .padding(end = 8.dp)
                        )
                    },
                    isPassword = true,
                    keyboardType = KeyboardType.Password
                )

                Spacer(modifier = Modifier.height(30.dp))

                // Error message
                if (uiState is LoginUiState.Error) {
                    Text(
                        text = (uiState as LoginUiState.Error).message,
                        color = Color.Red,
                        fontSize = 14.sp,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                Button(
                    onClick = {
                        // Call the login function from ViewModel
                        viewModel.login(
                            email = email,
                            password = password,
                            context = context,
                            onSuccess = { /* Navigation is handled by LaunchedEffect */ }
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF76808A)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    enabled = uiState !is LoginUiState.Loading
                ) {
                    if (uiState is LoginUiState.Loading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text(
                            text = "Sign in",
                            fontSize = 16.sp,
                            color = Color.White,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))
                Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                    Text(
                        text = "Don't have account? ",
                        fontSize = 14.sp,
                        color = Color.White,
                    )
                    Text(
                        text = "Sign up",
                        fontSize = 14.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable {
                            onNavigateToRegister()
                        }
                    )
                }
            }
        }

        // Loading overlay
        if (uiState is LoginUiState.Loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x80000000)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.White)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen(
        onNavigateToRegister = {},
        onNavigateToHome = {},
        onNavigateToForgotPassword = {},
        navController = rememberNavController()
    )
}