package com.example.project1.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.preference.PreferenceManager
import com.example.project1.DataStoreManager
import com.example.project1.settings.profile.ProfileApiService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.io.File

class EditProfileViewModel : ViewModel() {
    private val apiService = ProfileApiService()

    var username by mutableStateOf("")
    var email by mutableStateOf("")
    var profilePictureUri by mutableStateOf<Uri?>(null)
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf("")
    var successMessage by mutableStateOf("")

    fun loadUserData(context: Context) {
        viewModelScope.launch {
            try {
                // Load username from DataStore
                DataStoreManager.getUsername(context).collect {
                    username = it ?: ""
                }

                // Load email from SharedPreferences
                email = PreferenceManager.getDefaultSharedPreferences(context)
                    .getString("VERIFY_EMAIL", "") ?: ""
                Log.d("EditProfile", "Email: $email")
            } catch (e: Exception) {
                Log.e("EditProfile", "Load failed", e)
                errorMessage = "Failed to load profile"
            }
        }
    }

    fun updateProfile(context: Context) {
        if (username.isBlank()) {
            errorMessage = "Please enter a username"
            return
        }

        isLoading = true
        errorMessage = ""
        successMessage = ""

        viewModelScope.launch {
            when (val result = apiService.updateProfile(context, username)) {
                is ProfileApiService.ProfileResult.Success -> {
                    DataStoreManager.saveUsername(context, username)
                    successMessage = result.message
                    loadUserData(context) // Refresh data
                }

                is ProfileApiService.ProfileResult.Error -> {
                    errorMessage = result.message
                }
            }
            isLoading = false
        }
    }

    fun loadProfilePicture(context: Context) {
        viewModelScope.launch {
            try {
                // First check if we have a saved URI in SharedPreferences
                val prefs = PreferenceManager.getDefaultSharedPreferences(context)
                val savedUriString = prefs.getString("PROFILE_PICTURE_URI", null)
                val timestamp = prefs.getLong("PROFILE_PICTURE_TIMESTAMP", 0)

                if (savedUriString != null) {
                    // Try to use the saved URI
                    try {
                        // Add timestamp as query parameter to bust any caches
                        val uri = Uri.parse(savedUriString)
                        profilePictureUri = uri
                        Log.d(
                            "EditProfile",
                            "Loaded profile picture URI from prefs: $profilePictureUri"
                        )
                        return@launch
                    } catch (e: Exception) {
                        Log.e("EditProfile", "Failed to parse saved URI", e)
                        // Continue to fallback method
                    }
                }

                // Fallback: Check if the file exists and create a new URI
                val file = File(context.filesDir, "profile_picture.jpg")
                if (file.exists()) {
                    profilePictureUri = FileProvider.getUriForFile(
                        context,
                        "com.example.project1.fileprovider",
                        file
                    )

                    // Save this URI to preferences for next time
                    prefs.edit()
                        .putString("PROFILE_PICTURE_URI", profilePictureUri.toString())
                        .putLong("PROFILE_PICTURE_TIMESTAMP", System.currentTimeMillis())
                        .apply()

                    Log.d("EditProfile", "Loaded profile picture from file: $profilePictureUri")
                } else {
                    Log.d("EditProfile", "No profile picture found")
                    profilePictureUri = null
                }
            } catch (e: Exception) {
                Log.e("EditProfile", "Failed to load profile picture", e)
            }
        }
    }

    fun setProfilePicture(uri: Uri?) {
        profilePictureUri = uri
        Log.d("EditProfile", "Profile picture set to: $uri")
    }

    // Add a function to refresh the profile picture
    fun refreshProfilePicture(context: Context) {
        viewModelScope.launch {
            try {
                // Force reload the profile picture
                val file = File(context.filesDir, "profile_picture.jpg")
                if (file.exists()) {
                    val uri = FileProvider.getUriForFile(
                        context,
                        "com.example.project1.fileprovider",
                        file
                    )

                    // Add a timestamp to force cache busting
                    profilePictureUri = null // Set to null first to force recomposition
                    delay(50) // Small delay to ensure recomposition
                    profilePictureUri = uri

                    // Update the timestamp in preferences
                    val prefs = PreferenceManager.getDefaultSharedPreferences(context)
                    prefs.edit()
                        .putString("PROFILE_PICTURE_URI", uri.toString())
                        .putLong("PROFILE_PICTURE_TIMESTAMP", System.currentTimeMillis())
                        .apply()

                    Log.d("EditProfile", "Refreshed profile picture: $uri")
                }
            } catch (e: Exception) {
                Log.e("EditProfile", "Failed to refresh profile picture", e)
            }
        }
    }
}