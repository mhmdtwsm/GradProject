package com.example.project1.authentication.passwordreset.verifyemail

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

// API response data class
data class SendOtpResponse(
    val success: Boolean,
    val message: String,
    val token: String? = null
)

class SendOtpApiService {
    private val client = OkHttpClient()
    private val baseUrl = "http://phishaware.runasp.net/api/auth"

    suspend fun sendOtp(email: String, fromLogin: Boolean? = false): SendOtpResponse {
        return withContext(Dispatchers.IO) {
            val url = "$baseUrl/Send-OTP"

            // Create JSON payload
            val jsonObject = JSONObject().apply {
                put("email", email)
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
                    val message = jsonResponse.optString("message", "OTP sent successfully")
                    val token = jsonResponse.optString("token", "")

                    SendOtpResponse(
                        success = true,
                        message = message,
                        token = token
                    )
                } else {
                    // Handle API error messages
                    val errorMessage = when (response.code) {
                        400 -> "Invalid email"
                        404 -> "Email not found"
                        500 -> "Server error"
                        else -> "Failed to send OTP: ${response.message}"
                    }
                    SendOtpResponse(success = false, message = errorMessage)
                }
            } catch (e: IOException) {
                SendOtpResponse(success = false, message = "Network error: ${e.message}")
            } catch (e: Exception) {
                SendOtpResponse(success = false, message = "Error: ${e.message}")
            }
        }
    }
}