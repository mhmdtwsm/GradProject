package com.example.project1.settings.profile

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.preference.PreferenceManager
import com.example.project1.DataStoreManager
import com.example.project1.viewmodel.EditProfileViewModel
import com.example.project1.ui.theme.Project1Theme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    onNavigateToPasswordChange: () -> Unit,
    viewModel: EditProfileViewModel = viewModel()
) {
    val context = LocalContext.current
    val lifecycle_context = LocalLifecycleOwner.current.lifecycle
    val emailauth =
        PreferenceManager.getDefaultSharedPreferences(context).getString("VERIFY_EMAIL", null)

    // Load user data when the screen is first shown
    LaunchedEffect(Unit) {
        viewModel.loadUserData(context)
        viewModel.loadProfilePicture(context)
    }

    // Handle screen lifecycle events
    DisposableEffect(Unit) {
        val lifecycleObserver = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.refreshProfilePicture(context)
            }
        }
        lifecycle_context.addObserver(lifecycleObserver)
        onDispose {
            lifecycle_context.removeObserver(lifecycleObserver)
        }
    }

    // Handle success and error messages
    LaunchedEffect(viewModel.successMessage, viewModel.errorMessage) {
        viewModel.successMessage.takeIf { it.isNotEmpty() }?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
        viewModel.errorMessage.takeIf { it.isNotEmpty() }?.let {
            Log.d("EditProfile", "Error message: $it")
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }

    Project1Theme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Edit Profile") },
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
            // The Save button is now in the bottomBar for a better UX
            bottomBar = {
                Box(modifier = Modifier.padding(16.dp)) {
                    SaveButton(viewModel, context)
                }
            }
        ) { innerPadding ->
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                // Profile picture section
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Box(contentAlignment = Alignment.Center) {
                        // This is your existing component, just wrapped for styling
                        ProfilePictureComponent(
                            profilePictureUri = viewModel.profilePictureUri,
                            onProfilePictureChanged = { uri -> viewModel.setProfilePicture(uri) },
                            viewModel = viewModel
                        )
                        // Add a small camera icon overlay to indicate it's editable
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .offset(x = (-8).dp, y = (-8).dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.secondary)
                                .padding(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Change Picture",
                                tint = MaterialTheme.colorScheme.onSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }

                // Form fields section
                item {
                    FormField(
                        label = "Name",
                        value = viewModel.username,
                        onValueChange = { viewModel.username = it },
                        icon = Icons.Default.AccountCircle,
                        keyboardType = KeyboardType.Text
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    FormField(
                        label = "Email",
                        value = emailauth ?: "",
                        onValueChange = {},
                        icon = Icons.Default.Email,
                        keyboardType = KeyboardType.Email,
                        enabled = false // Explicitly disable email field
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }

                // Password reset section
                item {
                    PasswordSection(onNavigateToPasswordChange)
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}


@Composable
private fun FormField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    keyboardType: KeyboardType,
    enabled: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label) },
        leadingIcon = {
            Icon(imageVector = icon, contentDescription = null)
        },
        enabled = enabled,
        shape = RoundedCornerShape(12.dp),
        // These colors are now drawn from the theme, fixing the light mode issue.
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
            unfocusedIndicatorColor = MaterialTheme.colorScheme.outline,
            disabledIndicatorColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
            cursorColor = MaterialTheme.colorScheme.primary
        )
    )
}

@Composable
private fun PasswordSection(onResetClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Password",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        OutlinedButton(onClick = onResetClick) {
            Text("Reset")
        }
    }
}


@Composable
fun SaveButton(viewModel: EditProfileViewModel, context: Context) {
    val savedUsername by DataStoreManager.getUsername(context).collectAsState(initial = "")

    val isButtonEnabled = !viewModel.isLoading &&
            viewModel.username.isNotBlank() &&
            viewModel.username != savedUsername

    Button(
        onClick = {
            viewModel.updateProfile(context)
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        enabled = isButtonEnabled,
        shape = RoundedCornerShape(12.dp)
        // Button colors are now handled automatically by the theme
    ) {
        if (viewModel.isLoading) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(24.dp),
                strokeWidth = 2.dp
            )
        } else {
            Text(
                text = "Save changes",
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}