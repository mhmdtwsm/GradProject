package com.example.project1.settings.profile

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.preference.PreferenceManager
import com.example.project1.DataStoreManager
import com.example.project1.R
import com.example.project1.authentication.CommonComponents.StandardTextField
import com.example.project1.viewmodel.EditProfileViewModel

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

        val lifecycle = lifecycle_context
        lifecycle.addObserver(lifecycleObserver)

        onDispose {
            lifecycle.removeObserver(lifecycleObserver)
        }
    }

    // Handle success and error messages
    LaunchedEffect(viewModel.successMessage, viewModel.errorMessage) {
        if (viewModel.successMessage.isNotEmpty()) {
            Toast.makeText(context, viewModel.successMessage, Toast.LENGTH_SHORT).show()
        }
        if (viewModel.errorMessage.isNotEmpty()) {
            Log.d("EditProfile", "Error message: ${viewModel.errorMessage}")
            Toast.makeText(context, viewModel.errorMessage, Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        contentColor = Color.White
    ) { innerPadding ->

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
                    "Edit Profile",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(20.dp))

            androidx.compose.material3.Divider(color = Color.Gray.copy(alpha = 0.5f))

            // Profile picture section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp), // Increased vertical padding
                contentAlignment = Alignment.Center
            ) {
                ProfilePictureComponent(
                    profilePictureUri = viewModel.profilePictureUri,
                    onProfilePictureChanged = { uri ->
                        viewModel.setProfilePicture(uri)
                    },
                    viewModel = viewModel
                )
            }

            // Form fields section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                // Name field
                FormField(
                    label = "Name",
                    value = viewModel.username,
                    onValueChange = { viewModel.username = it },
                    icon = Icons.Default.AccountCircle,
                    keyboardType = KeyboardType.Text
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Email field (read-only)
                FormField(
                    label = "Email",
                    value = emailauth ?: "",
                    onValueChange = {},
                    icon = Icons.Default.Email,
                    keyboardType = KeyboardType.Email,
                    enabled = false
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Password reset section
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Password",
                    modifier = Modifier
                        .padding(bottom = 12.dp),
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )

                Button(
                    onClick = onNavigateToPasswordChange,
                    modifier = Modifier
                        .width(160.dp)
                        .height(50.dp),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF28324C))
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.passwordgenerate),
                        contentDescription = "Reset Password",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Reset", color = Color.White)
                }
            }

            // Push the save button to the bottom with flexible space
            Spacer(modifier = Modifier.weight(1f))

            // Save button
            SaveButton(viewModel, context)

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun TopNavigationBar(navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Back button
        Image(
            painter = painterResource(id = R.drawable.arrow),
            contentDescription = "Back",
            modifier = Modifier
                .size(30.dp)
                .clickable { navController.navigate(Screen.Settings.route) }
        )

        // Title
        Text(
            "Edit Profile",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        // Empty spacer for alignment
        Spacer(modifier = Modifier.size(30.dp))
    }

    Divider(
        color = Color.Gray.copy(alpha = 0.5f),
        modifier = Modifier.padding(top = 8.dp)
    )
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
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            modifier = Modifier.padding(bottom = 8.dp),
            color = Color.White,
            fontWeight = FontWeight.Medium
        )

        StandardTextField(
            value = value,
            onValueChange = onValueChange,
            hint = label,
            leadingIcon = {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White
                )
            },
            keyboardType = keyboardType,
            enabled = enabled
        )
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
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF76808A),
            disabledContainerColor = Color(0xFF76808A).copy(alpha = 0.5f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp), // Increased height for better touch target
        enabled = isButtonEnabled
    ) {
        if (viewModel.isLoading) {
            CircularProgressIndicator(
                color = Color.White,
                modifier = Modifier.size(24.dp)
            )
        } else {
            Text(
                text = if (isButtonEnabled) "Save changes" else "No changes to save",
                fontSize = 16.sp,
                color = if (isButtonEnabled) Color.White else Color.White.copy(alpha = 0.7f),
            )
        }
    }
}