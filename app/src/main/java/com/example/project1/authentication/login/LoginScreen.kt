package com.example.project1.authentication.login

import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.project1.R
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
    val fromLogin = true

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

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
            .background(Color(android.graphics.Color.parseColor("#101F31")))
            .padding(horizontal = 16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(50.dp))

            // ✅ صورة تسجيل الدخول
            Image(
                painter = painterResource(id = R.drawable.login),
                contentDescription = "Login illustration",
                modifier = Modifier
                    .size(250.dp)
                    .padding(top = 25.dp)
            )



            Spacer(modifier = Modifier.height(40.dp))

            // Email field
            Text("Email", color = Color.White, fontSize = 16.sp, modifier = Modifier.fillMaxWidth())
            StandardTextField(
                value = email,
                onValueChange = {
                    email = it
                    emailError = null
                },
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
            emailError?.let {
                Text(text = it, color = Color.Red, fontSize = 12.sp, modifier = Modifier.fillMaxWidth())
            }

            Spacer(modifier = Modifier.height(15.dp))

            // Password field
            Text("Password", color = Color.White, fontSize = 16.sp, modifier = Modifier.fillMaxWidth())
            StandardTextField(
                value = password,
                onValueChange = {
                    password = it
                    passwordError = null
                },
                hint = "Password",
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Password",
                        tint = Color.White
                    )
                },
                trailingIcon = {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = "Toggle password visibility",
                        tint = Color.White,
                        modifier = Modifier.clickable { passwordVisible = !passwordVisible }
                    )
                },
                isPassword = !passwordVisible,
                keyboardType = KeyboardType.Password
            )
            passwordError?.let {
                Text(text = it, color = Color.Red, fontSize = 12.sp, modifier = Modifier.fillMaxWidth())
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Forgot Password
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { navController.navigate("verifyemail/$fromLogin") },
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "Forgot Password?",
                    color = Color.White,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            // Error message from ViewModel
            if (uiState is LoginUiState.Error) {
                Text(
                    text = (uiState as LoginUiState.Error).message,
                    color = Color.Red,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Login button
            Button(
                onClick = {
                    // Validation
                    var valid = true
                    if (email.isEmpty()) {
                        emailError = "Email is required"
                        valid = false
                    } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        emailError = "Enter a valid email"
                        valid = false
                    }
                    if (password.isEmpty()) {
                        passwordError = "Password is required"
                        valid = false
                    }

                    if (valid) {
                        viewModel.login(
                            email = email,
                            password = password,
                            context = context,
                            onSuccess = { /* Already handled by LaunchedEffect */ }
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E3B4E)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = uiState !is LoginUiState.Loading
            ) {
                if (uiState is LoginUiState.Loading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(text = "Login", fontSize = 16.sp, color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // Sign up prompt
            Row {
                Text(text = "Don't have an account? ", fontSize = 14.sp, color = Color.White)
                Text(
                    text = "Register",
                    fontSize = 14.sp,
                    color = Color(0xFF4FC3F7), // لون أزرق سماوي فاتح يليق بالخلفية                             fontWeight = FontWeight.Bold,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onNavigateToRegister() }
                )
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
