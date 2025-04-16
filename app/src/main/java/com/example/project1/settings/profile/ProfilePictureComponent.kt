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
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.FileProvider
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import java.io.File
import java.io.FileOutputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.util.Log
import androidx.compose.material.icons.filled.Face
import androidx.compose.ui.res.painterResource
import androidx.preference.PreferenceManager
import com.example.project1.R
import java.util.UUID

@Composable
fun ProfilePictureComponent(
    profilePictureUri: Uri?,
    onProfilePictureChanged: (Uri?) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var showImagePickerDialog by remember { mutableStateOf(false) }

    // Use a key to force recomposition when the image changes
    var imageKey by remember { mutableStateOf(UUID.randomUUID().toString()) }

    // Temporary URI for camera capture
    var tempUri by remember { mutableStateOf<Uri?>(null) }

    // Camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            // tempUri is set before launching camera
            tempUri?.let { uri ->
                coroutineScope.launch {
                    try {
                        // Save the image to permanent storage
                        val savedUri = saveImageToInternalStorage(context, uri)

                        // Save the URI string to SharedPreferences for persistence
                        saveProfilePictureUriToPrefs(context, savedUri.toString())

                        // Update the ViewModel
                        onProfilePictureChanged(savedUri)

                        // Generate a new key to force image recomposition
                        imageKey = UUID.randomUUID().toString()

                        Log.d("ProfilePicture", "Saved camera image: $savedUri")
                    } catch (e: Exception) {
                        Log.e("ProfilePicture", "Failed to save camera image", e)
                        Toast.makeText(
                            context,
                            "Failed to save image: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    // Gallery launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            coroutineScope.launch {
                try {
                    // Save the image to permanent storage
                    val savedUri = saveImageToInternalStorage(context, it)

                    // Save the URI string to SharedPreferences for persistence
                    saveProfilePictureUriToPrefs(context, savedUri.toString())

                    // Update the ViewModel
                    onProfilePictureChanged(savedUri)

                    // Generate a new key to force image recomposition
                    imageKey = UUID.randomUUID().toString()

                    Log.d("ProfilePicture", "Saved gallery image: $savedUri")
                } catch (e: Exception) {
                    Log.e("ProfilePicture", "Failed to save gallery image", e)
                    Toast.makeText(
                        context,
                        "Failed to save image: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    // Track the image loading state
    var isImageLoading by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .size(120.dp)
            .clip(CircleShape)
            .border(2.dp, Color.White, CircleShape)
            .clickable { showImagePickerDialog = true }
    ) {
        // Display chosen image or default
        if (profilePictureUri != null) {
            // Create image painter with cache disabled to ensure fresh loading
            val painter = rememberAsyncImagePainter(
                ImageRequest.Builder(context)
                    .data(profilePictureUri)
                    .setParameter("key", imageKey) // Use setParameter instead of key
                    .diskCachePolicy(CachePolicy.DISABLED) // Disable disk cache
                    .memoryCachePolicy(CachePolicy.DISABLED) // Disable memory cache
                    .crossfade(true)
                    .build()
            )

            // Track loading state
            isImageLoading = painter.state is AsyncImagePainter.State.Loading

            Image(
                painter = painter,
                contentDescription = "Profile Picture",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Show loading indicator while image is loading
            if (isImageLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
        } else {
            // Default placeholder
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF8DFD9)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Default Profile",
                    tint = Color.Gray,
                    modifier = Modifier.size(60.dp)
                )
            }
        }

        // Camera button overlay - properly positioned
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(4.dp)
                .size(36.dp)
                .clip(CircleShape)
                .background(Color(0xFF28324C))
                .clickable { showImagePickerDialog = true },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.camera),
                contentDescription = "Change Photo",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
    }

    // Image picker dialog
    if (showImagePickerDialog) {
        Dialog(onDismissRequest = { showImagePickerDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Change Profile Picture",
                        style = MaterialTheme.typography.titleLarge
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // Camera option
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable {
                                coroutineScope.launch {
                                    try {
                                        // Create a temporary file and get a content:// URI using FileProvider
                                        tempUri = createTempImageFileUri(context)
                                        tempUri?.let { uri ->
                                            cameraLauncher.launch(uri)
                                        }
                                        showImagePickerDialog = false
                                    } catch (e: Exception) {
                                        Log.e("ProfilePicture", "Failed to launch camera", e)
                                        Toast.makeText(
                                            context,
                                            "Failed to launch camera: ${e.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF28324C)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.camera),
                                    contentDescription = "Camera",
                                    tint = Color.White,
                                    modifier = Modifier.size(30.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "Camera")
                        }

                        // Gallery option
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable {
                                galleryLauncher.launch("image/*")
                                showImagePickerDialog = false
                            }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF28324C)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Face,
                                    contentDescription = "Gallery",
                                    tint = Color.White,
                                    modifier = Modifier.size(30.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "Gallery")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { showImagePickerDialog = false },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF76808A))
                    ) {
                        Text("Cancel")
                    }
                }
            }
        }
    }
}

// Helper function to create a temporary file and get a content:// URI using FileProvider
private suspend fun createTempImageFileUri(context: Context): Uri = withContext(Dispatchers.IO) {
    val tempFile = File(context.cacheDir, "temp_camera_image_${System.currentTimeMillis()}.jpg")
    tempFile.createNewFile()

    // Use FileProvider to get a content:// URI
    FileProvider.getUriForFile(
        context,
        "com.example.project1.fileprovider",
        tempFile
    )
}

// Helper function to save image to internal storage
private suspend fun saveImageToInternalStorage(context: Context, uri: Uri): Uri =
    withContext(Dispatchers.IO) {
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val fileName = "profile_picture.jpg"
            val outputFile = File(context.filesDir, fileName)

            inputStream?.use { input ->
                FileOutputStream(outputFile).use { output ->
                    input.copyTo(output)
                }
            }

            // Clear any existing image caches
            clearImageCaches(context)

            // Use FileProvider to get a content:// URI for the saved file
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

// Helper function to save the profile picture URI to SharedPreferences
private fun saveProfilePictureUriToPrefs(context: Context, uriString: String) {
    val prefs = PreferenceManager.getDefaultSharedPreferences(context)
    prefs.edit()
        .putString("PROFILE_PICTURE_URI", uriString)
        .putLong(
            "PROFILE_PICTURE_TIMESTAMP",
            System.currentTimeMillis()
        ) // Add timestamp for cache busting
        .apply()
    Log.d("ProfilePicture", "Saved URI to SharedPreferences: $uriString")
}

// Helper function to clear image caches
private fun clearImageCaches(context: Context) {
    try {
        // Clear coil's disk cache directory if it exists
        val cacheDir = File(context.cacheDir, "image_cache")
        if (cacheDir.exists()) {
            cacheDir.deleteRecursively()
        }
    } catch (e: Exception) {
        Log.e("ProfilePicture", "Error clearing image caches", e)
    }
}