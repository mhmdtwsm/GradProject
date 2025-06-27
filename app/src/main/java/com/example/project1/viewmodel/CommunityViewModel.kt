package com.example.project1.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.preference.PreferenceManager
import com.example.project1.home.network.CommunityNetworkService
import com.example.project1.home.network.PostDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.project1.home.network.UserInfoDto
import android.graphics.Bitmap
import android.net.Uri
import android.graphics.BitmapFactory
import android.content.Context

class CommunityViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        private const val TAG = "CommunityViewModel"
    }

    private val networkService = CommunityNetworkService()
    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(application)

    // State flows for UI
    private val _posts = MutableStateFlow<List<PostDto>>(emptyList())
    val posts: StateFlow<List<PostDto>> = _posts

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _isCreatingPost = MutableStateFlow(false)
    val isCreatingPost: StateFlow<Boolean> = _isCreatingPost

    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab

    // User info
    private val _currentUserId = MutableStateFlow("")
    val currentUserId: StateFlow<String> = _currentUserId

    private val _currentUsername = MutableStateFlow("")
    val currentUsername: StateFlow<String> = _currentUsername

    // Image handling
    private val _selectedImageUri = MutableStateFlow<Uri?>(null)
    val selectedImageUri: StateFlow<Uri?> = _selectedImageUri

    val _selectedImageBitmap = MutableStateFlow<Bitmap?>(null)
    val selectedImageBitmap: StateFlow<Bitmap?> = _selectedImageBitmap

    init {
        Log.d(TAG, "CommunityViewModel initialized")
        loadUserInfo()
    }

    /**
     * Load user information from the server
     */
    private fun loadUserInfo() {
        Log.d(TAG, "Loading user info from server...")

        viewModelScope.launch {
            try {
                val authToken = getAuthToken()
                val userInfo = networkService.getUserInfo(authToken)

                _currentUserId.value = userInfo.id
                _currentUsername.value = userInfo.userName

                Log.d(TAG, "Loaded user info - ID: ${userInfo.id}, Username: ${userInfo.userName}, Email: ${userInfo.email}")

                // After loading user info, load posts
                loadPosts()

            } catch (e: Exception) {
                Log.e(TAG, "Error loading user info from server: ${e.message}", e)
                // Fallback to SharedPreferences if server call fails
                try {
                    val userId = sharedPreferences.getString("USER_ID", "") ?: ""
                    val username = sharedPreferences.getString("USERNAME", "") ?: ""

                    _currentUserId.value = userId
                    _currentUsername.value = username

                    Log.d(TAG, "Fallback to SharedPreferences - ID: $userId, Username: $username")
                    loadPosts()

                } catch (fallbackError: Exception) {
                    Log.e(TAG, "Error loading user info from SharedPreferences: ${fallbackError.message}", fallbackError)
                    _error.value = "Failed to load user information"
                }
            }
        }
    }

    /**
     * Load all posts from the server
     */
    fun loadPosts() {
        Log.d(TAG, "Starting to load posts...")

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val authToken = getAuthToken()
                Log.d(TAG, "Using auth token for posts request")

                val fetchedPosts = if (_selectedTab.value == 0) {
                    // Load all posts
                    Log.d(TAG, "Loading all posts")
                    networkService.getAllPosts(authToken)
                } else {
                    // Load user's posts only
                    Log.d(TAG, "Loading user posts for: ${_currentUserId.value}")
                    networkService.getUserPosts(authToken, _currentUserId.value)
                }

                _posts.value = fetchedPosts
                Log.d(TAG, "Successfully loaded ${fetchedPosts.size} posts")

            } catch (e: Exception) {
                Log.e(TAG, "Error loading posts: ${e.message}", e)
                _error.value = "Failed to load posts: ${e.message}"
            } finally {
                _isLoading.value = false
                Log.d(TAG, "Finished loading posts")
            }
        }
    }

    /**
     * Create a new post with optional image
     */
    fun createPost(content: String) {
        if (content.isBlank()) {
            Log.w(TAG, "Attempted to create post with empty content")
            _error.value = "Post content cannot be empty"
            return
        }

        Log.d(TAG, "Creating new post with content: $content")

        viewModelScope.launch {
            _isCreatingPost.value = true
            _error.value = null

            try {
                val authToken = getAuthToken()
                Log.d(TAG, "Using auth token for post creation")

                // Convert image to base64 if present
                val imageBase64 = _selectedImageBitmap.value?.let { bitmap ->
                    Log.d(TAG, "Converting image to base64 for upload")
                    networkService.bitmapToBase64(bitmap)
                } ?: ""

                val success = networkService.createPost(authToken, content, imageBase64)

                if (success) {
                    Log.d(TAG, "Post created successfully, clearing image and reloading posts...")
                    clearSelectedImage() // Clear image after successful post
                    loadPosts() // Reload posts to get the new post
                } else {
                    Log.e(TAG, "Post creation failed")
                    _error.value = "Failed to create post"
                }

            } catch (e: Exception) {
                Log.e(TAG, "Error creating post: ${e.message}", e)
                _error.value = "Failed to create post: ${e.message}"
            } finally {
                _isCreatingPost.value = false
                Log.d(TAG, "Finished creating post")
            }
        }
    }

    /**
     * Like a post
     */
    fun likePost(postId: Int) {
        Log.d(TAG, "Liking post with ID: $postId")

        viewModelScope.launch {
            try {
                val authToken = getAuthToken()
                Log.d(TAG, "Using auth token for like request")

                val success = networkService.likePost(authToken, postId)

                if (success) {
                    Log.d(TAG, "Post liked successfully, reloading posts...")
                    loadPosts() // Reload posts to get updated like count
                } else {
                    Log.e(TAG, "Post like failed")
                    _error.value = "Failed to like post"
                }

            } catch (e: Exception) {
                Log.e(TAG, "Error liking post: ${e.message}", e)
                _error.value = "Failed to like post: ${e.message}"
            }
        }
    }

    /**
     * Create a comment on a post
     */
    fun createComment(postId: Int, content: String) {
        if (content.isBlank()) {
            Log.w(TAG, "Attempted to create comment with empty content")
            _error.value = "Comment content cannot be empty"
            return
        }

        Log.d(TAG, "Creating comment on post $postId with content: $content")

        viewModelScope.launch {
            try {
                val authToken = getAuthToken()
                Log.d(TAG, "Using auth token for comment creation")

                val success = networkService.createComment(authToken, postId, content)

                if (success) {
                    Log.d(TAG, "Comment created successfully, reloading posts...")
                    loadPosts() // Reload posts to get the new comment
                } else {
                    Log.e(TAG, "Comment creation failed")
                    _error.value = "Failed to create comment"
                }

            } catch (e: Exception) {
                Log.e(TAG, "Error creating comment: ${e.message}", e)
                _error.value = "Failed to create comment: ${e.message}"
            }
        }
    }

    /**
     * Switch between tabs (All Posts / My Posts)
     */
    fun selectTab(tabIndex: Int) {
        Log.d(TAG, "Switching to tab: $tabIndex")

        if (_selectedTab.value != tabIndex) {
            _selectedTab.value = tabIndex
            loadPosts() // Reload posts for the selected tab
        }
    }

    /**
     * Clear any error messages
     */
    fun clearError() {
        Log.d(TAG, "Clearing error message")
        _error.value = null
    }

    /**
     * Refresh posts (pull to refresh)
     */
    fun refreshPosts() {
        Log.d(TAG, "Refreshing posts...")
        loadPosts()
    }

    /**
     * Get authentication token from shared preferences
     */
    private fun getAuthToken(): String {
        val token = sharedPreferences.getString("AUTH_TOKEN", null)

        if (token.isNullOrEmpty()) {
            Log.e(TAG, "No authentication token found")
            throw Exception("Not authenticated - please log in again")
        }

        Log.d(TAG, "Retrieved auth token successfully")
        return token
    }

    /**
     * Get filtered posts based on selected tab
     */
    fun getFilteredPosts(): List<PostDto> {
        return if (_selectedTab.value == 0) {
            _posts.value
        } else {
            _posts.value.filter { it.userId == _currentUserId.value }
        }
    }

    /**
     * Set selected image URI and convert to bitmap
     */
    fun setSelectedImage(uri: Uri?) {
        Log.d(TAG, "Setting selected image URI: $uri")
        _selectedImageUri.value = uri

        if (uri != null) {
            viewModelScope.launch {
                try {
                    val context = getApplication<Application>().applicationContext
                    val inputStream = context.contentResolver.openInputStream(uri)
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    inputStream?.close()

                    // Resize bitmap if too large
                    val resizedBitmap = resizeBitmap(bitmap, 800, 600)
                    _selectedImageBitmap.value = resizedBitmap

                    Log.d(TAG, "Image converted to bitmap successfully")
                } catch (e: Exception) {
                    Log.e(TAG, "Error converting image to bitmap: ${e.message}", e)
                    _error.value = "Failed to process selected image"
                }
            }
        } else {
            _selectedImageBitmap.value = null
        }
    }

    /**
     * Clear selected image
     */
    fun clearSelectedImage() {
        Log.d(TAG, "Clearing selected image")
        _selectedImageUri.value = null
        _selectedImageBitmap.value = null
    }

    /**
     * Resize bitmap to fit within max dimensions
     */
    private fun resizeBitmap(bitmap: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        if (width <= maxWidth && height <= maxHeight) {
            return bitmap
        }

        val aspectRatio = width.toFloat() / height.toFloat()
        val newWidth: Int
        val newHeight: Int

        if (aspectRatio > 1) {
            newWidth = maxWidth
            newHeight = (maxWidth / aspectRatio).toInt()
        } else {
            newHeight = maxHeight
            newWidth = (maxHeight * aspectRatio).toInt()
        }

        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "CommunityViewModel cleared")
    }
}
