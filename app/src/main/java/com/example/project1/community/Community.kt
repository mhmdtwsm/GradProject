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
// Launcher لاختيار صورة جديدة أثناء تعديل بوست

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
            viewModel.setSelectedImage(null) // Clear URI since we have bitmap directly
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
                containerColor = Color(0xFF1976D2)
            ) {
                Text("↻", color = Color.White, fontSize = 24.sp)
            }
        },
        containerColor = Color(0xFF101F31)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF101F31))
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
                Image(
                    painter = painterResource(id = R.drawable.arrow),
                    contentDescription = "Back",
                    modifier = Modifier
                        .size(30.dp)
                        .clickable {
                            Log.d("Community", "Back button clicked")
                            navController.popBackStack()
                        }
                )
                Spacer(modifier = Modifier.weight(0.69f))
                Text(
                    "PhishAware Community",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(20.dp))
            Divider(color = Color.Gray.copy(alpha = 0.5f))

            // Welcome Card
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1C2431))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Welcome to PhishAware Community! 👋", color = Color.White)
                    Text("Share your experience and help others stay protected.", color = Color(0xFF90A4AE), fontSize = 13.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tab Row
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color(0xFF1C2431),
                modifier = Modifier.fillMaxWidth()
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = {
                        Log.d("Community", "All posts tab selected")
                        viewModel.selectTab(0)
                    },
                    text = { Text("All Posts", color = Color.White) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = {
                        Log.d("Community", "My posts tab selected")
                        viewModel.selectTab(1)
                    },
                    text = { Text("My Posts", color = Color.White) }
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
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1C2B3A))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            OutlinedTextField(
                                value = newPostContent,
                                onValueChange = { newPostContent = it },
                                placeholder = { Text("Write your post...", color = Color.Gray) },
                                modifier = Modifier.fillMaxWidth(),
                                textStyle = LocalTextStyle.current.copy(color = Color.White),
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    focusedBorderColor = Color(0xFF1976D2),
                                    unfocusedBorderColor = Color.Gray
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
                                                tint = Color.White,
                                                modifier = Modifier
                                                    .background(
                                                        Color.Black.copy(alpha = 0.5f),
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
                                        contentColor = Color(0xFF1976D2)
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
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)),
                                    enabled = !isCreatingPost && newPostContent.isNotBlank()
                                ) {
                                    if (isCreatingPost) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(16.dp),
                                            color = Color.White
                                        )
                                    } else {
                                        Text("Post", color = Color.White)
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
                            CircularProgressIndicator(color = Color(0xFF1976D2))
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
                            editImagePickerLauncher.launch("image/*") // ✅ دا اللي هيفتح الجاليري
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
                                color = Color.Gray,
                                fontSize = 16.sp
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
            title = { Text("Select Image") },
            text = { Text("Choose how you want to add an image to your post") },
            confirmButton = {
                TextButton(
                    onClick = {
                        Log.d("Community", "Gallery option selected")
                        galleryLauncher.launch("image/*")
                    }
                ) {
                    Text("Gallery")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        Log.d("Community", "Camera option selected")
                        // Check camera permission
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
                    Text("Camera")
                }
            }
        )
    }

    // Comment Dialog
    if (showCommentDialog) {
        AlertDialog(
            onDismissRequest = {
                showCommentDialog = false
                commentContent = ""
            },
            title = { Text("Add Comment") },
            text = {
                OutlinedTextField(
                    value = commentContent,
                    onValueChange = { commentContent = it },
                    placeholder = { Text("Write your comment...") },
                    modifier = Modifier.fillMaxWidth()
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
                    Text("Post Comment")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showCommentDialog = false
                    commentContent = ""
                }) {
                    Text("Cancel")
                }
            }
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
    onPickEditImage: () -> Unit // ⬅️ أضف ده
) {
    var showComments by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1C2B3A)),
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
                            .background(Color(0xFF37474F)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = Color.White)
                    }
                }

                Column {
                    Text(
                        text = if (post.userId == currentUsername) "You" else post.userName,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF90CAF9)
                    )
                    Text(
                        text = formatDate(post.createdAt),
                        color = Color(0xFFB0BEC5),
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Post Content
            Text(post.content, color = Color.White, fontSize = 15.sp)

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
                    text = "👍 ${post.likeCount}",
                    color = Color(0xFFBBDEFB),
                    fontSize = 14.sp,
                    modifier = Modifier.clickable { onLikeClick() }
                )
                Text(
                    text = "💬 ${post.comments.size} Comments",
                    color = Color(0xFFB0BEC5),
                    fontSize = 14.sp,
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
                            color = Color.White,
                            fontSize = 14.sp
                        )
                        Text(
                            text = formatDate(comment.createdAt),
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    }
                }
            }


            // 🔥 Delete button if current user is the owner
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
                        viewModel.clearSelectedImage() // نفضي الصورة الحالية عشان التعديل يبدأ fresh
                        showEditDialog = true
                    }) {
                        Text("Edit", color = Color.Yellow)
                    }

                    TextButton(onClick = {
                        viewModel.deletePost(post.id)
                    }) {
                        Text("Delete", color = Color.Red)
                    }
                }

                if (showEditDialog) {
                    AlertDialog(
                        onDismissRequest = {
                            showEditDialog = false
                            editedContent = post.content
                            editedImageBitmap = null
                        },
                        title = { Text("Edit Post") },
                        text = {
                            Column {
                                OutlinedTextField(
                                    value = editedContent,
                                    onValueChange = { editedContent = it },
                                    label = { Text("Post Content") },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                // زرار تغيير الصورة
                                Button(onClick = {
                                    onPickEditImage() // يفتح الجاليري
                                }) {
                                    Text("Change Image")
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
                                Text("Save")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = {
                                showEditDialog = false
                                editedContent = post.content
                                editedImageBitmap = null
                            }) {
                                Text("Cancel")
                            }
                        }
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
