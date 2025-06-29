package com.example.project1.home

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.project1.R
import Screen
import com.example.project1.home.BottomNavigationBar
import com.example.project1.viewmodel.CommunityViewModel
import com.example.project1.ui.theme.customColors
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Community(
    navController: NavController,
    viewModel: CommunityViewModel = viewModel()
) {
    val editImagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.setSelectedImage(it)
            Log.d("Community", "Selected image for editing: $uri")
        }
    }

    val onPickEditImage: () -> Unit = {
        editImagePickerLauncher.launch("image/*")
    }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Collect state from ViewModel
    val posts by viewModel.posts.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val isCreatingPost by viewModel.isCreatingPost.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()
    val currentUsername by viewModel.currentUsername.collectAsState()
    val currentUserId by viewModel.currentUserId.collectAsState()

    val selectedImageUri by viewModel.selectedImageUri.collectAsState()
    val selectedImageBitmap by viewModel.selectedImageBitmap.collectAsState()

    // Local UI state
    var newPostContent by remember { mutableStateOf("") }
    var showCommentDialog by remember { mutableStateOf(false) }
    var selectedPostId by remember { mutableStateOf(0) }
    var commentContent by remember { mutableStateOf("") }
    var showImagePickerDialog by remember { mutableStateOf(false) }

    // Permission launcher for camera
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Log.d("Community", "Camera permission granted")
        } else {
            Log.d("Community", "Camera permission denied")
        }
    }

    // Image picker launchers
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            Log.d("Community", "Image selected from gallery: $uri")
            viewModel.setSelectedImage(it)
            showImagePickerDialog = false
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        bitmap?.let {
            Log.d("Community", "Photo taken from camera")
            viewModel.setSelectedImage(null)
            viewModel._selectedImageBitmap.value = it
            showImagePickerDialog = false
        }
    }

    // Show error as snackbar
    error?.let { errorMessage ->
        LaunchedEffect(errorMessage) {
            Log.e("Community", "Displaying error: $errorMessage")
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    Log.d("Community", "FAB clicked - refreshing posts")
                    viewModel.refreshPosts()
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Text(
                    "🔄",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 24.sp
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar with Back Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        Log.d("Community", "Back button clicked")
                        navController.popBackStack()
                    },
                    modifier = Modifier.size(30.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.weight(0.69f))
                Text(
                    "PhishAware Community",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))

            // Welcome Card
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.customColors.communityCard
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Welcome to PhishAware Community! 🛡️",
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        "Share your experience and help others stay protected.",
                        color = MaterialTheme.customColors.secondaryText,
                        fontSize = 13.sp,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tab Row
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth(),
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = {
                        Log.d("Community", "All posts tab selected")
                        viewModel.selectTab(0)
                    },
                    text = {
                        Text(
                            "All Posts",
                            color = if (selectedTab == 0)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurface
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = {
                        Log.d("Community", "My posts tab selected")
                        viewModel.selectTab(1)
                    },
                    text = {
                        Text(
                            "My Posts",
                            color = if (selectedTab == 1)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurface
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Content in LazyColumn
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                // Create Post Card
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.customColors.cardBackground
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            OutlinedTextField(
                                value = newPostContent,
                                onValueChange = { newPostContent = it },
                                placeholder = {
                                    Text(
                                        "Write your post...",
                                        color = MaterialTheme.customColors.hintText
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                textStyle = LocalTextStyle.current.copy(
                                    color = MaterialTheme.colorScheme.onSurface
                                ),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                    cursorColor = MaterialTheme.colorScheme.primary
                                )
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Selected Image Preview
                            selectedImageBitmap?.let { bitmap ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Box {
                                        Image(
                                            bitmap = bitmap.asImageBitmap(),
                                            contentDescription = "Selected Image",
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                        IconButton(
                                            onClick = { viewModel.clearSelectedImage() },
                                            modifier = Modifier.align(Alignment.TopEnd)
                                        ) {
                                            Icon(
                                                Icons.Default.Close,
                                                contentDescription = "Remove Image",
                                                tint = MaterialTheme.colorScheme.onPrimary,
                                                modifier = Modifier
                                                    .background(
                                                        MaterialTheme.colorScheme.scrim.copy(alpha = 0.5f),
                                                        CircleShape
                                                    )
                                                    .padding(4.dp)
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                            }

                            // Action Buttons Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                // Add Image Button
                                OutlinedButton(
                                    onClick = { showImagePickerDialog = true },
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = MaterialTheme.colorScheme.primary
                                    )
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = "Add Image")
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Photo")
                                }

                                // Post Button
                                Button(
                                    onClick = {
                                        if (newPostContent.isNotBlank()) {
                                            Log.d("Community", "Creating post with content: $newPostContent")
                                            viewModel.createPost(newPostContent)
                                            newPostContent = ""
                                        } else {
                                            Log.w("Community", "Attempted to create empty post")
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary
                                    ),
                                    enabled = !isCreatingPost && newPostContent.isNotBlank()
                                ) {
                                    if (isCreatingPost) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(16.dp),
                                            color = MaterialTheme.colorScheme.onPrimary
                                        )
                                    } else {
                                        Text(
                                            "Post",
                                            color = MaterialTheme.colorScheme.onPrimary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Loading indicator
                if (isLoading) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                // Posts List
                val filteredPosts = viewModel.getFilteredPosts()

                items(filteredPosts) { post ->
                    PostItem(
                        post = post,
                        currentUsername = currentUsername,
                        currentUserId = currentUserId,
                        onLikeClick = { viewModel.likePost(post.id) },
                        onCommentClick = {
                            selectedPostId = post.id
                            showCommentDialog = true
                        },
                        onPickEditImage = {
                            editImagePickerLauncher.launch("image/*")
                        }
                    )
                }

                // Empty state
                if (!isLoading && filteredPosts.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (selectedTab == 0) "No posts available" else "You haven't created any posts yet",
                                color = MaterialTheme.customColors.secondaryText,
                                fontSize = 16.sp,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }

    // Image Picker Dialog
    if (showImagePickerDialog) {
        AlertDialog(
            onDismissRequest = { showImagePickerDialog = false },
            title = {
                Text(
                    "Select Image",
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Text(
                    "Choose how you want to add an image to your post",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        Log.d("Community", "Gallery option selected")
                        galleryLauncher.launch("image/*")
                    }
                ) {
                    Text(
                        "Gallery",
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        Log.d("Community", "Camera option selected")
                        when (PackageManager.PERMISSION_GRANTED) {
                            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) -> {
                                cameraLauncher.launch(null)
                            }
                            else -> {
                                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                        }
                        showImagePickerDialog = false
                    }
                ) {
                    Text(
                        "Camera",
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            },
            containerColor = MaterialTheme.colorScheme.surface
        )
    }

    // Comment Dialog
    if (showCommentDialog) {
        AlertDialog(
            onDismissRequest = {
                showCommentDialog = false
                commentContent = ""
            },
            title = {
                Text(
                    "Add Comment",
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                OutlinedTextField(
                    value = commentContent,
                    onValueChange = { commentContent = it },
                    placeholder = {
                        Text(
                            "Write your comment...",
                            color = MaterialTheme.customColors.hintText
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        cursorColor = MaterialTheme.colorScheme.primary
                    )
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (commentContent.isNotBlank()) {
                            Log.d("Community", "Creating comment for post $selectedPostId: $commentContent")
                            viewModel.createComment(selectedPostId, commentContent)
                            showCommentDialog = false
                            commentContent = ""
                        }
                    }
                ) {
                    Text(
                        "Post Comment",
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showCommentDialog = false
                    commentContent = ""
                }) {
                    Text(
                        "Cancel",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            containerColor = MaterialTheme.colorScheme.surface
        )
    }
}

@Composable
private fun PostItem(
    post: com.example.project1.home.network.PostDto,
    currentUsername: String,
    currentUserId: String,
    onLikeClick: () -> Unit,
    onCommentClick: () -> Unit,
    onPickEditImage: () -> Unit
) {
    var showComments by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.customColors.cardBackground
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Post Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (!post.profilePicture.isNullOrEmpty()) {
                    val imageBytes = Base64.decode(post.profilePicture, Base64.DEFAULT)
                    val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Column {
                    Text(
                        text = if (post.userId == currentUsername) "You" else post.userName,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        text = formatDate(post.createdAt),
                        color = MaterialTheme.customColors.secondaryText,
                        fontSize = 12.sp,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Post Content
            Text(
                post.content,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 15.sp,
                style = MaterialTheme.typography.bodyMedium
            )

            // Post Image (if exists)
            if (post.image.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    val imageBytes = Base64.decode(post.image, Base64.DEFAULT)
                    val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

                    if (bitmap != null) {
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "Post Image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons
            Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                Text(
                    text = "❤️ ${post.likeCount}",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 14.sp,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.clickable { onLikeClick() }
                )
                Text(
                    text = "💬 ${post.comments.size} Comments",
                    color = MaterialTheme.customColors.secondaryText,
                    fontSize = 14.sp,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.clickable {
                        showComments = !showComments
                        if (!showComments) onCommentClick()
                    }
                )
            }

            // Comments Section
            if (showComments && post.comments.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                post.comments.forEach { comment ->
                    Column(modifier = Modifier.padding(start = 16.dp, top = 4.dp)) {
                        Text(
                            text = "💭 ${comment.content}",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 14.sp,
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = formatDate(comment.createdAt),
                            color = MaterialTheme.customColors.hintText,
                            fontSize = 12.sp,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }

            // Edit/Delete buttons if current user is the owner
            if (post.userId == currentUserId) {
                val viewModel: CommunityViewModel = viewModel()
                var showEditDialog by remember { mutableStateOf(false) }
                var editedContent by remember { mutableStateOf(post.content) }
                var editedImageBitmap by remember { mutableStateOf<Bitmap?>(null) }

                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = {
                        editedImageBitmap = null
                        viewModel.clearSelectedImage()
                        showEditDialog = true
                    }) {
                        Text(
                            "Edit",
                            color = MaterialTheme.customColors.warning
                        )
                    }

                    TextButton(onClick = {
                        viewModel.deletePost(post.id)
                    }) {
                        Text(
                            "Delete",
                            color = MaterialTheme.customColors.danger
                        )
                    }
                }

                if (showEditDialog) {
                    AlertDialog(
                        onDismissRequest = {
                            showEditDialog = false
                            editedContent = post.content
                            editedImageBitmap = null
                        },
                        title = {
                            Text(
                                "Edit Post",
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        },
                        text = {
                            Column {
                                OutlinedTextField(
                                    value = editedContent,
                                    onValueChange = { editedContent = it },
                                    label = {
                                        Text(
                                            "Post Content",
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                        cursorColor = MaterialTheme.colorScheme.primary
                                    )
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Button(
                                    onClick = { onPickEditImage() },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.secondary
                                    )
                                ) {
                                    Text(
                                        "Change Image",
                                        color = MaterialTheme.colorScheme.onSecondary
                                    )
                                }

                                // Preview image if exists
                                val selectedImageBitmap by viewModel.selectedImageBitmap.collectAsState()
                                val bitmap = editedImageBitmap ?: selectedImageBitmap
                                bitmap?.let {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Image(
                                        bitmap = it.asImageBitmap(),
                                        contentDescription = "Edited Image",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(150.dp)
                                            .clip(RoundedCornerShape(8.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = {
                                val imageToSend = editedImageBitmap ?: viewModel.selectedImageBitmap.value
                                viewModel.editPost(post.id, editedContent, imageToSend)
                                showEditDialog = false
                            }) {
                                Text(
                                    "Save",
                                    color = MaterialTheme.customColors.success
                                )
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = {
                                showEditDialog = false
                                editedContent = post.content
                                editedImageBitmap = null
                            }) {
                                Text(
                                    "Cancel",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        },
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                }
            }
        }
    }
}

private fun formatDate(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
        val date = inputFormat.parse(dateString.substringBefore('.'))
        outputFormat.format(date ?: Date())
    } catch (e: Exception) {
        "Recently"
    }
}
