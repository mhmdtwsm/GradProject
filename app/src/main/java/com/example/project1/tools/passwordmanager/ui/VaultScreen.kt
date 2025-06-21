package com.example.passwordmanager.screens

import Screen
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.passwordmanager.components.CopyDialog
import com.example.passwordmanager.viewmodel.VaultViewModel
import com.example.project1.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VaultScreen(
    navController: NavController,
    vaultId: String,
    viewModel: VaultViewModel = viewModel()
) {
    val vault by viewModel.currentVault.collectAsState()
    val isAuthenticated by viewModel.isAuthenticated.collectAsState()
    var showCopyDialog by remember { mutableStateOf(false) }
    var selectedAccountId by remember { mutableStateOf("") }
    var selectedEmail by remember { mutableStateOf("") }
    var selectedPassword by remember { mutableStateOf("") }

    val clipboardManager = LocalClipboardManager.current

    LaunchedEffect(vaultId) {
        viewModel.loadVault(vaultId)
    }

    if (!isAuthenticated) {
        navController.popBackStack()
        return
    }

    if (showCopyDialog) {
        CopyDialog(
            email = selectedEmail,
            password = selectedPassword,
            onDismiss = { showCopyDialog = false },
            onCopyEmail = {
                clipboardManager.setText(AnnotatedString(selectedEmail))
                showCopyDialog = false
            },
            onCopyPassword = {
                clipboardManager.setText(AnnotatedString(selectedPassword))
                showCopyDialog = false
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E293B))
    ) {
        TopAppBar(
            title = { Text(vault?.name ?: "Vault", color = Color.White) },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color(0xFF1E293B),
                titleContentColor = Color.White
            )
        )

        Divider(color = Color.White.copy(alpha = 0.2f))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Your Vault",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Manage ${vault?.accounts?.size ?: 0} Saved Accounts",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.padding(vertical = 4.dp)
            )

            Divider(
                color = Color.White.copy(alpha = 0.2f),
                modifier = Modifier.padding(vertical = 8.dp)
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(vault?.accounts ?: emptyList()) { account ->
                    AccountItem(
                        title = account.title,
                        email = account.email,
                        onClick = {
                            navController.navigate(
                                Screen.AccountDetails.route
                                    .replace("{vaultId}", vaultId)
                                    .replace("{accountId}", account.id)
                            )
                        },
                        onCopyClick = {
                            selectedAccountId = account.id
                            selectedEmail = account.email
                            selectedPassword = account.password
                            showCopyDialog = true
                        }
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                FloatingActionButton(
                    onClick = {
                        navController.navigate(
                            Screen.AddAccount.route.replace("{vaultId}", vaultId)
                        )
                    },
                    containerColor = Color(0xFFD1D5DB),
                    contentColor = Color.Black,
                    shape = CircleShape
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Account"
                    )
                }
            }
        }
    }
}

@Composable
fun AccountItem(
    title: String,
    email: String,
    onClick: () -> Unit,
    onCopyClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFFD1D5DB))
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.copy),
                contentDescription = null,
                tint = Color.Black,
                modifier = Modifier.size(24.dp)
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Text(
                text = email,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.DarkGray
            )
        }

        IconButton(onClick = onCopyClick) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.copy),
                contentDescription = "Copy",
                tint = Color.Black
            )
        }
    }
}
