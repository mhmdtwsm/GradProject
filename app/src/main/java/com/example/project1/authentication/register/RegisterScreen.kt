package com.example.project1.authentication.register

import android.widget.Toast
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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

                // Name TextField
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

                // Confirm Password TextField
                Spacer(modifier = Modifier.height(15.dp))
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

                // Display error message if there is one
                if (viewModel.errorMessage.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = viewModel.errorMessage,
                        color = Color.Red,
                        fontSize = 14.sp
                    )
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
                            text = "Register",
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
            }
        }
    }
}

@Preview
@Composable
fun RegisterScreenPreview() {
    RegisterScreen(onNavigateToLogin = {}, onNavigateToVerifyOTP = {})
}