package com.example.project1.authentication.passwordreset.OTP

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

// API response data class
data class VerifyOtpResponse(
    val success: Boolean,
    val message: String
)

class VerifyOtpApiService {
    private val client = OkHttpClient()
    private val baseUrl = "http://phishaware.runasp.net/api/auth"

    suspend fun verifyOtp(
        email: String,
        token: String,
        otpCode: String,
        fromLogin: Boolean? = false,
    ): VerifyOtpResponse {
        return withContext(Dispatchers.IO) {
            // i want here to add if it came from the Forgot Password page to use a link and if it came from Register Page to use another link
            val url = if (fromLogin == true) "$baseUrl/verify-otp" else "$baseUrl/verify-email-otp"

            // Create JSON payload
            val jsonObject = JSONObject().apply {
                put("email", email)
                put("token", token)
                put("otpCode", otpCode)
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
                    VerifyOtpResponse(
                        success = true,
                        message = "OTP verification successful"
                    )
                } else {
                    // Handle API error messages
                    val errorMessage = when (response.code) {
                        400 -> "Invalid request data"
                        401 -> "Incorrect OTP code"
                        404 -> "Email not found"
                        500 -> "Server error"
                        else -> "Verification failed: ${response.message}"
                    }
                    VerifyOtpResponse(success = false, message = errorMessage)
                }
            } catch (e: IOException) {
                VerifyOtpResponse(success = false, message = "Network error: ${e.message}")
            } catch (e: Exception) {
                VerifyOtpResponse(success = false, message = "Error: ${e.message}")
            }
        }
    }
}