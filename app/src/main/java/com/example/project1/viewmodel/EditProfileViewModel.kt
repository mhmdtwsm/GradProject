package com.example.project1.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.preference.PreferenceManager
import com.example.project1.DataStoreManager
import com.example.project1.settings.profile.ProfileApiService
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

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

    fun setProfilePicture(uri: Uri?) {
        profilePictureUri = uri
    }
}