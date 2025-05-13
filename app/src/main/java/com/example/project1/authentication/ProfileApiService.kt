package com.example.project1.authentication.profile

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.preference.PreferenceManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class ProfileApiService {
    private val client = OkHttpClient()
    private val baseUrl = "http://phishaware.runasp.net/api/user"

    // Data class for username response
    data class UsernameResponse(
        val username: String,
        val success: Boolean,
        val message: String = ""
    )

    // Get the username from API
    suspend fun fetchUsername(context: Context): UsernameResponse {
        return withContext(Dispatchers.IO) {
            val url = "$baseUrl/username"

            // Get the auth token from SharedPreferences
            val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)
            val authToken = sharedPrefs.getString("AUTH_TOKEN", "") ?: ""

            if (authToken.isBlank()) {
                return@withContext UsernameResponse("", false, "No auth token found")
            }

            val request = Request.Builder()
                .url(url)
                .get()
                .addHeader("Authorization", "Bearer $authToken")
                .build()

            try {
                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()

                if (response.isSuccessful && responseBody != null) {
                    val jsonResponse = JSONObject(responseBody)
                    val username = jsonResponse.getString("username")
                    return@withContext UsernameResponse(username, true)
                } else {
                    val errorMessage = when (response.code) {
                        401 -> "Unauthorized: Invalid or expired token"
                        404 -> "Username not found"
                        500 -> "Server error"
                        else -> "Failed to fetch username: ${response.message}"
                    }
                    return@withContext UsernameResponse("", false, errorMessage)
                }
            } catch (e: IOException) {
                return@withContext UsernameResponse("", false, "Network error: ${e.message}")
            } catch (e: Exception) {
                return@withContext UsernameResponse("", false, "Error: ${e.message}")
            }
        }
    }

    // Fetch and save profile picture
    suspend fun fetchAndSaveProfilePicture(context: Context): Boolean {
        return withContext(Dispatchers.IO) {
            val url = "$baseUrl/profile-picture"

            // Get the auth token from SharedPreferences
            val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)
            val authToken = sharedPrefs.getString("AUTH_TOKEN", "") ?: ""

            if (authToken.isBlank()) {
                Log.e("ProfileApiService", "No auth token found")
                return@withContext false
            }

            val request = Request.Builder()
                .url(url)
                .get()
                .addHeader("Authorization", "Bearer $authToken")
                .build()

            try {
                val response = client.newCall(request).execute()

                if (response.isSuccessful) {
                    // Get the image data
                    val imageBytes = response.body?.bytes() ?: return@withContext false

                    // Create bitmap from bytes
                    val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                    if (bitmap == null) {
                        Log.e("ProfileApiService", "Failed to decode image")
                        return@withContext false
                    }

                    // Save to internal storage
                    val file = File(context.filesDir, "profile_picture.jpg")
                    val outputStream = FileOutputStream(file)
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
                    outputStream.close()

                    // Save the URI to SharedPreferences
                    val uri = Uri.fromFile(file)
                    sharedPrefs.edit()
                        .putString("PROFILE_PICTURE_URI", uri.toString())
                        .putLong("PROFILE_PICTURE_TIMESTAMP", System.currentTimeMillis())
                        .apply()

                    Log.d("ProfileApiService", "Profile picture saved: ${uri.toString()}")
                    return@withContext true
                } else {
                    Log.e("ProfileApiService", "Failed to fetch profile picture: ${response.code}")
                    return@withContext false
                }
            } catch (e: IOException) {
                Log.e("ProfileApiService", "Network error: ${e.message}")
                return@withContext false
            } catch (e: Exception) {
                Log.e("ProfileApiService", "Error: ${e.message}")
                return@withContext false
            }
        }
    }
}