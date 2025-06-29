package com.example.project1.authentication.passwordreset

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.project1.authentication.CommonComponents.*

@Composable
fun ForgotPasswordScreen(
    fromLogin: Boolean = false,
    onResetPasswordClick: (String) -> Unit,
    navController: NavController,
) {
    var email by remember { mutableStateOf("") }
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
            ) {
                AppHeader(
                    title = "Forgot password",
                    onBackClick = onBackClick
                )

                Spacer(modifier = Modifier.height(40.dp))

                Text(
                    text = "Please enter your email to reset the password!",
                    color = Color.White,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                EmailTextField(
                    value = email,
                    onValueChange = { email = it }
                )

                Spacer(modifier = Modifier.height(24.dp))

                PrimaryButton(
                    text = "Reset Password",
                    onClick = { onResetPasswordClick(email) },
                    color = Color(0xFF4FC3F7),
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ForgotPasswordScreenPreview() {
    ForgotPasswordScreen(
        onResetPasswordClick = { /* TODO */ },
        navController = rememberNavController(),
    )
}