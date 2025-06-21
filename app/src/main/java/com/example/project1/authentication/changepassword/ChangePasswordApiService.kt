package com.example.project1.authentication.resetpassword

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

// API response data class
data class ChangePasswordResponse(
    val success: Boolean,
    val message: String
)

class ChangePasswordApiService {
    private val client = OkHttpClient()
    private val baseUrl = "http://phishaware.runasp.net/api"

    suspend fun changePassword(
        oldPassword: String,
        newPassword: String,
        confirmPassword: String,
        authToken: String
    ): ChangePasswordResponse {
        return withContext(Dispatchers.IO) {
            val url = "$baseUrl/user/change-password"

            // Create JSON payload - fix the JSON structure
            val jsonObject = JSONObject()
            jsonObject.put("oldPassword", oldPassword)
            jsonObject.put("newPassword", newPassword)
            jsonObject.put("confirmPassword", confirmPassword)

            val requestBody = jsonObject.toString()
                .toRequestBody("application/json".toMediaTypeOrNull())

            val request = Request.Builder()
                .url(url)
                .put(requestBody) // Using PUT method for changing password
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer $authToken") // Add the auth token
                .build()

            try {
                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()

                if (response.isSuccessful && responseBody != null) {
                    // Parse the response carefully
                    try {
                        val jsonResponse = JSONObject(responseBody)
                        val message =
                            jsonResponse.optString("message", "Password changed successfully")

                        ChangePasswordResponse(
                            success = true,
                            message = message
                        )
                    } catch (e: Exception) {
                        // If response is not valid JSON but request was successful
                        ChangePasswordResponse(
                            success = true,
                            message = responseBody.takeIf { it.isNotBlank() }
                                ?: "Password changed successfully"
                        )
                    }
                } else {
                    // Handle API error messages
                    val errorMessage = if (responseBody != null) {
                        try {
                            val jsonError = JSONObject(responseBody)
                            jsonError.optString("message", "Password change failed")
                        } catch (e: Exception) {
                            // If error response is not valid JSON
                            responseBody.takeIf { it.isNotBlank() }
                                ?: "Password change failed: ${response.message}"
                        }
                    } else {
                        when (response.code) {
                            400 -> "Invalid request data"
                            401 -> "Old password is incorrect"
                            403 -> "Unauthorized access"
                            500 -> "Server error"
                            else -> "Password change failed: ${response.message}"
                        }
                    }

                    ChangePasswordResponse(success = false, message = errorMessage)
                }
            } catch (e: IOException) {
                ChangePasswordResponse(success = false, message = "Network error: ${e.message}")
            } catch (e: Exception) {
                ChangePasswordResponse(success = false, message = "Error: ${e.message}")
            }
        }
    }
}
