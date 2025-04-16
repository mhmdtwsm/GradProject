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
data class RegisterResponse(
    val success: Boolean,
    val message: String
)

class RegisterApiService {
    private val client = OkHttpClient()
    private val baseUrl = "http://phishaware.runasp.net/api/auth"

    suspend fun registerUser(userName: String, email: String, password: String): RegisterResponse {
        return withContext(Dispatchers.IO) {
            val url = "$baseUrl/register"

            // Create JSON payload
            val jsonObject = JSONObject().apply {
                put("userName", userName)
                put("email", email)
                put("password", password)
                put("profile_Picture", "") // You can add profile picture handling later
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
                    RegisterResponse(
                        success = true,
                        message = "Registration successful"
                    )
                } else {
                    // Handle API error messages
                    val errorMessage = when (response.code) {
                        400 -> "Invalid request data"
                        409 -> "Email already in use"
                        500 -> "Server error"
                        else -> "Registration failed: ${response.message}"
                    }
                    RegisterResponse(success = false, message = errorMessage)
                }
            } catch (e: IOException) {
                RegisterResponse(success = false, message = "Network error: ${e.message}")
            } catch (e: Exception) {
                RegisterResponse(success = false, message = "Error: ${e.message}")
            }
        }
    }
}