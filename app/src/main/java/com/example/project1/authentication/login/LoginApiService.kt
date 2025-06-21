package com.example.project1.authentication.register

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

// API response data class
data class LoginResponse(
    val success: Boolean,
    val message: String = "",
    val token: String? = null
)

class LoginApiService {
    private val client = OkHttpClient()
    private val baseUrl = "http://phishaware.runasp.net/api/auth"

    suspend fun login(email: String, password: String): LoginResponse {
        return withContext(Dispatchers.IO) {
            val url = "$baseUrl/login"

            // Create JSON payload
            val jsonObject = JSONObject().apply {
                put("email", email)
                put("password", password)
            }

            val requestBody = jsonObject.toString()
                .toRequestBody("application/json".toMediaTypeOrNull())

            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .addHeader("Content-Type", "application/json")
                .build()

            try {
                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()

                if (response.isSuccessful && responseBody != null) {
                    // Parse the response JSON
                    val jsonResponse = JSONObject(responseBody)

                    // Check if token exists in the response
                    if (jsonResponse.has("token")) {
                        val token = jsonResponse.getString("token")
                        // Save token to SharedPreferences
                        return@withContext LoginResponse(
                            success = true,
                            token = token
                        )
                    } else if (jsonResponse.has("message")) {
                        // Handle error message from API
                        return@withContext LoginResponse(
                            success = false,
                            message = jsonResponse.getString("message")
                        )
                    } else {
                        return@withContext LoginResponse(
                            success = false,
                            message = "Unknown response format"
                        )
                    }
                } else {
                    // Handle API error messages
                    val errorMessage = when (response.code) {
                        400 -> "Invalid request data"
                        401 -> "Invalid credentials"
                        500 -> "Server error"
                        else -> "Login failed: ${response.message}"
                    }
                    return@withContext LoginResponse(success = false, message = errorMessage)
                }
            } catch (e: IOException) {
                return@withContext LoginResponse(
                    success = false,
                    message = "Network error: ${e.message}"
                )
            } catch (e: Exception) {
                return@withContext LoginResponse(success = false, message = "Error: ${e.message}")
            }
        }
    }
}
