package com.example.project1.settings.profile

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.preference.PreferenceManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.io.IOException

class ProfileApiService {
    private val client = OkHttpClient()
    private val baseUrl = "http://phishaware.runasp.net/api"

    sealed class ProfileResult {
        data class Success(val message: String) : ProfileResult()
        data class Error(val message: String) : ProfileResult()
    }

    suspend fun updateProfile(context: Context, username: String): ProfileResult {
        return withContext(Dispatchers.IO) {
            try {
                val authToken = PreferenceManager.getDefaultSharedPreferences(context)
                    .getString("AUTH_TOKEN", null)
                    ?: return@withContext ProfileResult.Error("Not authenticated")

                val jsonPayload = JSONObject().apply {
                    put("username", username)
                }.toString()

                val request = Request.Builder()
                    .url("$baseUrl/user/update-profile")
                    .put(jsonPayload.toRequestBody("application/json".toMediaTypeOrNull()))
                    .addHeader("Authorization", "Bearer $authToken")
                    .build()

                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()

                return@withContext when {
                    !response.isSuccessful -> ProfileResult.Error("HTTP ${response.code}")
                    responseBody == null -> ProfileResult.Error("Empty response")
                    responseBody.startsWith("{") -> {
                        try {
                            val json = JSONObject(responseBody)
                            ProfileResult.Success(json.optString("message", "Profile updated"))
                        } catch (e: Exception) {
                            ProfileResult.Error("Invalid JSON response")
                        }
                    }

                    else -> ProfileResult.Success(responseBody.trim())
                }
            } catch (e: IOException) {
                Log.e("ProfileApiService", "Network error: ${e.message}", e)
                ProfileResult.Error("Network error: ${e.message}")
            } catch (e: Exception) {
                ProfileResult.Error("Unexpected error: ${e.message}")
            }
        }
    }

    suspend fun uploadProfilePicture(context: Context, imageUri: Uri): ProfileResult {
        return withContext(Dispatchers.IO) {
            try {
                val authToken = PreferenceManager.getDefaultSharedPreferences(context)
                    .getString("AUTH_TOKEN", null)
                    ?: return@withContext ProfileResult.Error("Not authenticated")

                // Get the file from the URI
                val contentResolver = context.contentResolver
                val inputStream = contentResolver.openInputStream(imageUri)
                    ?: return@withContext ProfileResult.Error("Failed to open image file")

                // Create a temporary file to store the image
                val tempFile =
                    File(context.cacheDir, "temp_upload_${System.currentTimeMillis()}.jpg")
                tempFile.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
                inputStream.close()

                // Create multipart request body with the image file
                val requestBody = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart(
                        "Profile_Picture",
                        "profile_picture.jpg",
                        tempFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
                    )
                    .build()

                val request = Request.Builder()
                    .url("$baseUrl/user/change-profile-picture")
                    .put(requestBody)
                    .addHeader("Authorization", "Bearer $authToken")
                    .build()

                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()

                // Delete the temporary file
                tempFile.delete()

                return@withContext when {
                    !response.isSuccessful -> {
                        Log.e(
                            "ProfileApiService",
                            "Upload failed: HTTP ${response.code}, body: $responseBody"
                        )
                        ProfileResult.Error("HTTP ${response.code} - ${responseBody ?: "Unknown error"}")
                    }

                    responseBody == null -> {
                        ProfileResult.Success("Profile picture updated")
                    }

                    responseBody.startsWith("{") -> {
                        try {
                            val json = JSONObject(responseBody)
                            ProfileResult.Success(
                                json.optString(
                                    "message",
                                    "Profile picture updated"
                                )
                            )
                        } catch (e: Exception) {
                            ProfileResult.Error("Invalid JSON response")
                        }
                    }

                    else -> {
                        ProfileResult.Success(
                            responseBody.trim().ifEmpty { "Profile picture updated" })
                    }
                }
            } catch (e: IOException) {
                Log.e("ProfileApiService", "Network error: ${e.message}", e)
                ProfileResult.Error("Network error: ${e.message}")
            } catch (e: Exception) {
                Log.e("ProfileApiService", "Unexpected error: ${e.message}", e)
                ProfileResult.Error("Unexpected error: ${e.message}")
            }
        }
    }
}