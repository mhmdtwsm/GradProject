package com.example.project1.authentication.resetpassword

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.project1.R
import com.example.project1.authentication.CommonComponents.StandardTextField
import com.example.project1.ui.theme.Project1Theme
import com.example.project1.ui.theme.customColors
import com.example.project1.viewmodel.ChangePasswordViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreen(
    navController: NavController,
    viewModel: ChangePasswordViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState = remember { viewModel }

    Project1Theme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Change Password") },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface,
                        navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            },
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.Start
            ) {
                item { Spacer(modifier = Modifier.height(24.dp)) }

                // Old Password
                item {
                    Text(
                        text = "Enter Old Password",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    StandardTextField(
                        value = uiState.oldPassword,
                        onValueChange = { uiState.oldPassword = it },
                        hint = "Password",
                        leadingIcon = {
                            Icon(
                                Icons.Default.Lock,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { uiState.oldPasswordVisible = !uiState.oldPasswordVisible }) {
                                Icon(
                                    painterResource(id = if (uiState.oldPasswordVisible) R.drawable.eyeslash else R.drawable.eyenorm),
                                    contentDescription = if (uiState.oldPasswordVisible) "Hide password" else "Show password",
                                    tint = MaterialTheme.colorScheme.primary // Use a primary color for interactive icons
                                )
                            }
                        },
                        isPassword = !uiState.oldPasswordVisible,
                        keyboardType = KeyboardType.Password
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }

                // New Password
                item {
                    Text(
                        text = "Enter New Password",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    StandardTextField(
                        value = uiState.newPassword,
                        onValueChange = { uiState.newPassword = it },
                        hint = "Password",
                        leadingIcon = {
                            Icon(
                                Icons.Default.Lock,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { uiState.newPasswordVisible = !uiState.newPasswordVisible }) {
                                Icon(
                                    painterResource(id = if (uiState.newPasswordVisible) R.drawable.eyeslash else R.drawable.eyenorm),
                                    contentDescription = if (uiState.newPasswordVisible) "Hide password" else "Show password",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        },
                        isPassword = !uiState.newPasswordVisible,
                        keyboardType = KeyboardType.Password
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }

                // Confirm New Password
                item {
                    Text(
                        text = "Confirm New Password",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    StandardTextField(
                        value = uiState.confirmPassword,
                        onValueChange = { uiState.confirmPassword = it },
                        hint = "Password",
                        leadingIcon = {
                            Icon(
                                Icons.Default.Lock,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { uiState.confirmPasswordVisible = !uiState.confirmPasswordVisible }) {
                                Icon(
                                    painterResource(id = if (uiState.confirmPasswordVisible) R.drawable.eyeslash else R.drawable.eyenorm),
                                    contentDescription = if (uiState.confirmPasswordVisible) "Hide password" else "Show password",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        },
                        isPassword = !uiState.confirmPasswordVisible,
                        keyboardType = KeyboardType.Password
                    )
                }

                // Error and Success Messages
                item {
                    if (uiState.errorMessage.isNotEmpty()) {
                        Text(
                            text = uiState.errorMessage,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                    if (uiState.successMessage.isNotEmpty()) {
                        Text(
                            text = uiState.successMessage,
                            color = MaterialTheme.customColors.success,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }

                item { Spacer(modifier = Modifier.height(32.dp)) }

                // Update Password Button
                item {
                    Button(
                        onClick = {
                            viewModel.changePassword(
                                context = context,
                                onSuccess = {
                                    Toast.makeText(context, "Password updated successfully", Toast.LENGTH_SHORT).show()
                                    navController.popBackStack()
                                },
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        enabled = !uiState.isLoading,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "Update Password",
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChangePasswordScreenPreview() {
    Project1Theme {
        ChangePasswordScreen(navController = rememberNavController())
    }
}