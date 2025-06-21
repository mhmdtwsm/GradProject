package com.example.passwordmanager.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.passwordmanager.viewmodel.VaultViewModel
import com.example.project1.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountDetailsScreen(
    navController: NavController,
    vaultId: String,
    accountId: String,
    viewModel: VaultViewModel = viewModel()
) {
    val account by viewModel.currentAccount.collectAsState()
    var isEditing by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("") }
    var url by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    val clipboardManager = LocalClipboardManager.current

    LaunchedEffect(accountId) {
        viewModel.loadAccount(vaultId, accountId)
    }

    LaunchedEffect(account) {
        account?.let {
            title = it.title
            url = it.url
            email = it.email
            password = it.password
            notes = it.notes
        }
    }

    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text("Delete Account") },
            text = { Text("Are you sure you want to delete this account?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteAccount(vaultId, accountId)
                        showDeleteConfirmation = false
                        navController.popBackStack()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFB91C1C)
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDeleteConfirmation = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E293B))
            .padding(16.dp)
    ) {
        Text(
            text = account?.title ?: "Account Details",
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        Divider(color = Color.White.copy(alpha = 0.2f))

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "URL",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White
        )

        OutlinedTextField(
            value = url,
            onValueChange = { url = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            shape = RoundedCornerShape(8.dp),
            enabled = isEditing,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.White,
                unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                disabledBorderColor = Color.White.copy(alpha = 0.3f),
                disabledTextColor = Color.White
            )
        )

        Text(
            text = "Email",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            shape = RoundedCornerShape(8.dp),
            enabled = isEditing,
            trailingIcon = {
                if (!isEditing) {
                    IconButton(onClick = { clipboardManager.setText(AnnotatedString(email)) }) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.copy),
                            contentDescription = "Copy Email",
                            tint = Color.White
                        )
                    }
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.White,
                unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                disabledBorderColor = Color.White.copy(alpha = 0.3f),
                disabledTextColor = Color.White
            )
        )

        Text(
            text = "Password",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            shape = RoundedCornerShape(8.dp),
            enabled = isEditing,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                Row {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) ImageVector.vectorResource(R.drawable.eyenorm) else ImageVector.vectorResource(
                                R.drawable.eyeslash
                            ),
                            contentDescription = if (passwordVisible) "Hide password" else "Show password",
                            tint = Color.White
                        )
                    }

                    if (!isEditing) {
                        IconButton(onClick = { clipboardManager.setText(AnnotatedString(password)) }) {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.copy),
                                contentDescription = "Copy Password",
                                tint = Color.White
                            )
                        }
                    }
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.White,
                unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                disabledBorderColor = Color.White.copy(alpha = 0.3f),
                disabledTextColor = Color.White
            )
        )

        Text(
            text = "Notes",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White
        )

        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .padding(vertical = 8.dp),
            shape = RoundedCornerShape(8.dp),
            enabled = isEditing,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.White,
                unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                disabledBorderColor = Color.White.copy(alpha = 0.3f),
                disabledTextColor = Color.White
            )
        )

        Spacer(modifier = Modifier.weight(1f))

        if (isEditing) {
            Button(
                onClick = {
                    viewModel.updateAccount(
                        vaultId = vaultId,
                        accountId = accountId,
                        title = title,
                        url = url,
                        email = email,
                        password = password,
                        notes = notes
                    )
                    isEditing = false
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF9CA3AF)
                )
            ) {
                Text(
                    text = "Save Changes",
                    color = Color.Black
                )
            }

            OutlinedButton(
                onClick = {
                    account?.let {
                        title = it.title
                        url = it.url
                        email = it.email
                        password = it.password
                        notes = it.notes
                    }
                    isEditing = false
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.White
                )
            ) {
                Text("Cancel")
            }
        } else {
            Button(
                onClick = { isEditing = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF9CA3AF)
                )
            ) {
                Text(
                    text = "Edit account",
                    color = Color.Black
                )
            }

            Button(
                onClick = { showDeleteConfirmation = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFB91C1C)
                )
            ) {
                Text(
                    text = "Delete Account",
                    color = Color.White
                )
            }
        }
    }
}
