package com.example.project1.register

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.text2.input.TextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.project1.R


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E293B))
    ) {
        // TextField States
        val emailState = TextFieldState()
        val passwordState = TextFieldState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .background(Color(0xFF1E293B)),
            verticalArrangement = Arrangement.SpaceAround,

            ) {
            Button(
                onClick = {
                    onNavigateToHome()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF76808A)),
            ) {
                Text(
                    text = "Continue as Guest",
                    color = Color.White
                )
            }
            Column {
                Image(
                    painter = painterResource(id = R.drawable.register),
                    contentDescription = "",
                    contentScale = ContentScale.FillHeight,
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxWidth(0.25f)
                        .size(200.dp)
                )

                Spacer(modifier = Modifier.height(30.dp))
                Text(
                    text = "Login",
                    fontWeight = FontWeight.Bold,
                    fontSize = 40.sp,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(15.dp))

                MyTextField(
                    textFieldState = emailState,
                    hint = "Email",
                    leadingIcon = Icons.Default.Email,
                    keyboardType = KeyboardType.Email
                )

                Spacer(modifier = Modifier.height(15.dp))
                MyTextField(
                    textFieldState = passwordState,
                    hint = "Password",
                    leadingIcon = Icons.Default.Lock,
                    trailingText = "Forgot Password?",
                    isPassword = true,
                    keyboardType = KeyboardType.Password,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(30.dp))

                val context = LocalContext.current
                Button(
                    onClick = {
                        val email = emailState.text
                        val password = passwordState.text

                        // The Authentication logic here


                        // Navigate to Home Screen
                        onNavigateToHome()
                        Toast.makeText(
                            context,
                            "Login Successful, welcome $email, your password is $password",
                            Toast.LENGTH_LONG
                        ).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF76808A)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                ) {
                    Text(
                        text = "Login",
                        fontSize = 16.sp,
                        color = Color.White,
                    )
                }
                Spacer(modifier = Modifier.height(30.dp))
                Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                    Text(
                        text = "Don't have account? ",
                        fontSize = 14.sp,
                        color = Color.White,

                        )
                    Text(
                        text = "Register",
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
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen(onNavigateToRegister = {}, onNavigateToHome = {})
}