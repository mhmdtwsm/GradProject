package com.example.project1.authentication.resetpassword

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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
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
import androidx.navigation.NavController
import com.example.project1.R
import com.example.project1.authentication.CommonComponents.StandardTextField
import com.example.project1.home.BottomNavigationBar
import com.example.project1.viewmodel.ChangePasswordViewModel

@Composable
fun ChangePasswordScreen(
    navController: NavController,
    viewModel: ChangePasswordViewModel = viewModel()
) {

    Scaffold()
    { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1C2431))
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Top Bar with Back Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.arrow),
                    contentDescription = "Back",
                    modifier = Modifier
                        .size(30.dp)
                        .clickable { navController.navigate(Screen.Settings.route) }
                )
                Spacer(modifier = Modifier.weight(0.69f))
                androidx.compose.material.Text(
                    "Change Password",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(20.dp))

            Divider(color = Color.Gray.copy(alpha = 0.5f))

            Spacer(modifier = Modifier.height(20.dp))

            // Old Password Field
            Text(
                text = "Enter Old Password",
                color = Color.White,
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            StandardTextField(
                value = viewModel.oldPassword,
                onValueChange = { viewModel.oldPassword = it },
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
                        viewModel.oldPasswordVisible = !viewModel.oldPasswordVisible
                    }) {
                        Icon(
                            painterResource(id = if (viewModel.oldPasswordVisible) R.drawable.eyeslash else R.drawable.eyenorm),
                            contentDescription = if (viewModel.oldPasswordVisible) "Hide password" else "Show password",
                            tint = Color.White
                        )
                    }
                },
                isPassword = !viewModel.oldPasswordVisible,
                keyboardType = KeyboardType.Password
            )

            Spacer(modifier = Modifier.height(24.dp))

            // New Password Field
            Text(
                text = "Enter New Password",
                color = Color.White,
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            StandardTextField(
                value = viewModel.newPassword,
                onValueChange = { viewModel.newPassword = it },
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
                        viewModel.newPasswordVisible = !viewModel.newPasswordVisible
                    }) {
                        Icon(
                            painterResource(id = if (viewModel.newPasswordVisible) R.drawable.eyeslash else R.drawable.eyenorm),
                            contentDescription = if (viewModel.newPasswordVisible) "Hide password" else "Show password",
                            tint = Color.White
                        )
                    }
                },
                isPassword = !viewModel.newPasswordVisible,
                keyboardType = KeyboardType.Password
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Confirm New Password Field
            Text(
                text = "Confirm New Password",
                color = Color.White,
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            StandardTextField(
                value = viewModel.confirmPassword,
                onValueChange = { viewModel.confirmPassword = it },
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
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = viewModel.errorMessage,
                    color = Color.Red,
                    fontSize = 14.sp
                )
            }

            // Display success message if there is one
            if (viewModel.successMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = viewModel.successMessage,
                    color = Color.Green,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            val context = LocalContext.current
            // Update Password Button
            Button(
                onClick = {
                    viewModel.changePassword(
                        context = context,
                        onSuccess = {
                            navController.popBackStack()
                            Toast.makeText(
                                context,
                                "Password updated successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
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
                        text = "Update Password",
                        fontSize = 16.sp,
                        color = Color.White,
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun ChangePasswordScreenPreview() {
    ChangePasswordScreen(navController = NavController(LocalContext.current))
}
