package com.example.passwordmanager.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.passwordmanager.components.CreateVaultDialog
import com.example.passwordmanager.components.PasswordDialog
import com.example.passwordmanager.components.VaultIcon
import com.example.passwordmanager.model.VaultIcon
import Screen
import com.example.passwordmanager.viewmodel.VaultViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavController,
    viewModel: VaultViewModel = viewModel()
) {
    val vaults by viewModel.vaults.collectAsState()
    var showCreateVaultDialog by remember { mutableStateOf(false) }
    var selectedVaultId by remember { mutableStateOf("") }
    var showPasswordDialog by remember { mutableStateOf(false) }

    if (showCreateVaultDialog) {
        CreateVaultDialog(
            onDismiss = { showCreateVaultDialog = false },
            onCreateVault = { name, password, icon ->
                viewModel.createVault(name, password, icon)
                showCreateVaultDialog = false
            }
        )
    }

    if (showPasswordDialog) {
        PasswordDialog(
            onDismiss = { showPasswordDialog = false },
            onPasswordEntered = { password ->
                if (viewModel.authenticateVault(selectedVaultId, password)) {
                    navController.navigate(Screen.Vault.route.replace("{vaultId}", selectedVaultId))
                }
                showPasswordDialog = false
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E293B))
    ) {
        TopAppBar(
            title = { Text("Vaults Home", color = Color.White) },
            navigationIcon = {
                IconButton(onClick = { /* Handle back navigation */ }) {
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
                text = "Your Vaults",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Manage Your Passwords In Isolated Vaults",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.padding(vertical = 4.dp)
            )

            Button(
                onClick = { showCreateVaultDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF9CA3AF)
                )
            ) {
                Text(
                    text = "Create a new vault",
                    color = Color.Black
                )
            }

            Divider(color = Color.White.copy(alpha = 0.2f))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(vaults) { vault ->
                    VaultItem(
                        vaultName = vault.name,
                        vaultIcon = vault.icon,
                        onClick = {
                            selectedVaultId = vault.id
                            showPasswordDialog = true
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun VaultItem(
    vaultName: String,
    vaultIcon: VaultIcon,
    onClick: () -> Unit
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
        VaultIcon(icon = vaultIcon, tint = Color.Black)

        Text(
            text = vaultName,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp)
        )

        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = "Open Vault",
            tint = Color.Black
        )
    }
}
