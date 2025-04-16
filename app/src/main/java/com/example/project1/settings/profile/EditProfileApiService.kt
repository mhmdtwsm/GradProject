package com.example.project1.settings.profile

import android.content.Context
import androidx.preference.PreferenceManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
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
                ProfileResult.Error("Network error: ${e.message}")
            } catch (e: Exception) {
                ProfileResult.Error("Unexpected error: ${e.message}")
            }
        }
    }
}