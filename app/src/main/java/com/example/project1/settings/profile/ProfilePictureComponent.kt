package com.example.project1.settings.profile

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.preference.PreferenceManager
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.project1.ui.theme.Project1Theme
import com.example.project1.viewmodel.EditProfileViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.*

@Composable
fun ProfilePictureComponent(
    profilePictureUri: Uri?,
    onProfilePictureChanged: (Uri?) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EditProfileViewModel? = null // Add view model parameter
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var showImagePickerDialog by remember { mutableStateOf(false) }

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            hasCameraPermission = isGranted
            if (!isGranted) {
                Toast.makeText(
                    context,
                    "Camera permission is required to take photos",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    )

    var imageKey by remember { mutableStateOf(UUID.randomUUID().toString()) }
    var tempUri by remember { mutableStateOf<Uri?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            tempUri?.let { uri ->
                coroutineScope.launch {
                    try {
                        val savedUri = saveImageToInternalStorage(context, uri)
                        saveProfilePictureUriToPrefs(context, savedUri.toString())
                        onProfilePictureChanged(savedUri)
                        viewModel?.updateProfilePicture(context, savedUri)
                        imageKey = UUID.randomUUID().toString()
                        Log.d("ProfilePicture", "Saved camera image: $savedUri")
                    } catch (e: Exception) {
                        Log.e("ProfilePicture", "Failed to save camera image", e)
                        Toast.makeText(context, "Failed to save image: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            coroutineScope.launch {
                try {
                    val savedUri = saveImageToInternalStorage(context, it)
                    saveProfilePictureUriToPrefs(context, savedUri.toString())
                    onProfilePictureChanged(savedUri)
                    viewModel?.updateProfilePicture(context, savedUri)
                    imageKey = UUID.randomUUID().toString()
                    Log.d("ProfilePicture", "Saved gallery image: $savedUri")
                } catch (e: Exception) {
                    Log.e("ProfilePicture", "Failed to save gallery image", e)
                    Toast.makeText(context, "Failed to save image: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    var isImageLoading by remember { mutableStateOf(false) }
    val isUploading = viewModel?.isUploadingImage ?: false

    Box(
        modifier = modifier
            .size(120.dp)
            .clickable { showImagePickerDialog = true }
    ) {
        val imageModifier = Modifier
            .fillMaxSize()
            .clip(CircleShape)
            // Use a theme color for the border
            .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)

        if (profilePictureUri != null) {
            val painter = rememberAsyncImagePainter(
                ImageRequest.Builder(context)
                    .data(profilePictureUri)
                    .setParameter("key", imageKey)
                    .diskCachePolicy(CachePolicy.DISABLED)
                    .memoryCachePolicy(CachePolicy.DISABLED)
                    .crossfade(true)
                    .build()
            )
            isImageLoading = painter.state is AsyncImagePainter.State.Loading

            Image(
                painter = painter,
                contentDescription = "Profile Picture",
                modifier = imageModifier,
                contentScale = ContentScale.Crop
            )

            if (isImageLoading || isUploading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        // Use the theme's scrim color for overlays
                        .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        // Use a color that contrasts with the scrim
                        color = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
        } else {
            // Default placeholder using theme colors
            Box(
                modifier = imageModifier.background(MaterialTheme.colorScheme.secondaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Default Profile",
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.size(60.dp)
                )
            }
        }
    }

    if (showImagePickerDialog) {
        Project1Theme { // Ensure the dialog is themed
            Dialog(onDismissRequest = { showImagePickerDialog = false }) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Change Profile Picture",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            DialogOption(
                                label = "Camera",
                                icon = Icons.Default.PhotoCamera,
                                onClick = {
                                    if (hasCameraPermission) {
                                        coroutineScope.launch {
                                            try {
                                                tempUri = createTempImageFileUri(context)
                                                tempUri?.let { uri -> cameraLauncher.launch(uri) }
                                                showImagePickerDialog = false
                                            } catch (e: Exception) {
                                                Log.e("ProfilePicture", "Failed to launch camera", e)
                                                Toast.makeText(context, "Failed to launch camera: ${e.message}", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    } else {
                                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                    }
                                }
                            )
                            DialogOption(
                                label = "Gallery",
                                icon = Icons.Default.PhotoLibrary,
                                onClick = {
                                    galleryLauncher.launch("image/*")
                                    showImagePickerDialog = false
                                }
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        TextButton(
                            onClick = { showImagePickerDialog = false },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("Cancel")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DialogOption(label: String, icon: ImageVector, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(30.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}


// Helper functions (no changes needed)
private suspend fun createTempImageFileUri(context: Context): Uri = withContext(Dispatchers.IO) {
    //... (implementation unchanged)
    val tempFile = File(context.cacheDir, "temp_camera_image_${System.currentTimeMillis()}.jpg")
    tempFile.createNewFile()
    FileProvider.getUriForFile(
        context,
        "com.example.project1.fileprovider",
        tempFile
    )
}

private suspend fun saveImageToInternalStorage(context: Context, uri: Uri): Uri = withContext(Dispatchers.IO) {
    //... (implementation unchanged)
    try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val fileName = "profile_picture.jpg"
        val outputFile = File(context.filesDir, fileName)
        inputStream?.use { input ->
            FileOutputStream(outputFile).use { output ->
                input.copyTo(output)
            }
        }
        clearImageCaches(context)
        FileProvider.getUriForFile(
            context,
            "com.example.project1.fileprovider",
            outputFile
        )
    } catch (e: Exception) {
        Log.e("ProfilePicture", "Error saving image", e)
        throw e
    }
}

private fun saveProfilePictureUriToPrefs(context: Context, uriString: String) {
    //... (implementation unchanged)
    val prefs = PreferenceManager.getDefaultSharedPreferences(context)
    prefs.edit()
        .putString("PROFILE_PICTURE_URI", uriString)
        .putLong("PROFILE_PICTURE_TIMESTAMP", System.currentTimeMillis())
        .apply()
    Log.d("ProfilePicture", "Saved URI to SharedPreferences: $uriString")
}

private fun clearImageCaches(context: Context) {
    //... (implementation unchanged)
    try {
        val cacheDir = File(context.cacheDir, "image_cache")
        if (cacheDir.exists()) {
            cacheDir.deleteRecursively()
        }
    } catch (e: Exception) {
        Log.e("ProfilePicture", "Error clearing image caches", e)
    }
}