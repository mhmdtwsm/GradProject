package com.example.project1.Register

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
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Done
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.project1.R


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RegisterScreen(

    modifier: Modifier = Modifier,
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: () -> Unit,
) {
    val nameState = TextFieldState()
    val emailState = TextFieldState()
    val passwordState = TextFieldState()
    val confirmPasswordState = TextFieldState()

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
                    painter = painterResource(id = R.drawable.login),
                    contentDescription = "",
                    contentScale = ContentScale.FillHeight,
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxWidth(0.25f)
                        .size(140.dp)
                )

                Spacer(modifier = Modifier.height(30.dp))
                Text(
                    text = "Register",
                    fontWeight = FontWeight.Bold,
                    fontSize = 40.sp,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(15.dp))

                MyTextField(
                    textFieldState = nameState,
                    hint = "Name",
                    leadingIcon = Icons.Default.AccountCircle,
                    keyboardType = KeyboardType.Email
                )

                // Email
                Spacer(modifier = Modifier.height(15.dp))
                MyTextField(
                    textFieldState = emailState,
                    hint = "Email",
                    leadingIcon = Icons.Default.Email,
                    keyboardType = KeyboardType.Email
                )

                // Password
                Spacer(modifier = Modifier.height(15.dp))
                MyTextField(
                    textFieldState = passwordState,
                    hint = "Password",
                    leadingIcon = Icons.Default.Lock,
                    isPassword = true,
                    keyboardType = KeyboardType.Password,
                    modifier = Modifier.fillMaxWidth()
                )

                // Confirm Password
                Spacer(modifier = Modifier.height(15.dp))
                MyTextField(
                    textFieldState = confirmPasswordState,
                    hint = "ConfirmPassword",
                    leadingIcon = Icons.Default.Lock,
                    isPassword = true,
                    keyboardType = KeyboardType.Password,
                    modifier = Modifier.fillMaxWidth()
                )

                val contex = LocalContext.current
                Spacer(modifier = Modifier.height(30.dp))

                Button(
                    onClick = {
                        // Authenticate the user
                        val name = nameState.text

                        onNavigateToHome()
                        Toast.makeText(
                            contex,
                            "Registration successful MR. ${name}",
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF76808A)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text(
                        text = "Register",
                        fontSize = 16.sp,
                        color = Color.White,
                    )
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
            }
        }
    }
}

@Preview
@Composable
fun RegisterScreenPreview() {
    RegisterScreen(onNavigateToLogin = {}, onNavigateToHome = {})
}