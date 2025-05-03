// EditProfileScreen.kt
package com.example.project1.settings.profile

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
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

    // Load user data when the screen is first shown
    LaunchedEffect(Unit) {
        viewModel.loadUserData(context)
        viewModel.loadProfilePicture(context)
    }

    DisposableEffect(Unit) {
        val lifecycleObserver = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                // Refresh the profile picture when the screen is resumed
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
            Toast.makeText(context, viewModel.errorMessage, Toast.LENGTH_SHORT).show()
        }
    }

    // Image picker
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        viewModel.setProfilePicture(uri)
    }

    Scaffold(
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

            Divider(color = Color.Gray.copy(alpha = 0.5f))

            Spacer(modifier = Modifier.height(20.dp))
            Spacer(modifier = Modifier.height(30.dp))

            // Profile Picture
            ProfilePictureComponent(
                profilePictureUri = viewModel.profilePictureUri,
                onProfilePictureChanged = { uri ->
                    viewModel.setProfilePicture(uri)
                },
                modifier = Modifier.padding(vertical = 16.dp)
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Name TextField
            Text(
                text = "Name",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                color = Color.White
            )
            StandardTextField(
                value = viewModel.username,
                onValueChange = { viewModel.username = it },
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

            Spacer(modifier = Modifier.height(30.dp))

            // Password Reset Button
            Text(
                text = "Password",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                color = Color.White
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
                    painter = painterResource(id = R.drawable.passwordgenerate), // Use your lock icon resource
                    contentDescription = "Reset Password",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Reset", color = Color.White)
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Save Button
            SaveButton(viewModel, context)

            Spacer(modifier = Modifier.height(20.dp))
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
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF76808A)),
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        enabled = isButtonEnabled
    ) {
        if (viewModel.isLoading) {
            CircularProgressIndicator(
                color = Color.White,
                modifier = Modifier.size(24.dp)
            )
        } else {
            Text(
                text = if (isButtonEnabled) "Save changes" else "",
                fontSize = 16.sp,
                color = Color.White,
            )
        }
    }
}

@Preview
@Composable
fun EditProfileScreenPreview() {
    EditProfileScreen(
        onNavigateToPasswordChange = {},
        modifier = TODO(),
        navController = TODO(),
        viewModel = TODO()
    )
}