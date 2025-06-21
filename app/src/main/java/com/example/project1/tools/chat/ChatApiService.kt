package com.example.project1.chat

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

class ChatApiService {
    // Configure OkHttpClient with increased timeouts
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)  // Increase connection timeout to 30 seconds
        .readTimeout(60, TimeUnit.SECONDS)     // Increase read timeout to 60 seconds
        .writeTimeout(30, TimeUnit.SECONDS)    // Increase write timeout to 30 seconds
        .build()

    // Updated to use the new endpoint
    private val baseUrl = "https://chatbot-79880788701.us-central1.run.app"
    private val TAG = "ChatApiService" // Tag for logging

    sealed class ChatResult {
        data class Success(val response: String) : ChatResult()
        data class Error(val message: String) : ChatResult()
    }

    suspend fun sendMessage(
        question: String,
        history: List<ChatMessage> = emptyList() // Keep for compatibility, but won't be used
    ): ChatResult {
        return withContext(Dispatchers.IO) {
            try {
                // Create request body with the new structure
                val jsonObject = JSONObject().apply {
                    put("text", question)
                }

                val jsonPayload = jsonObject.toString()

                // Log the request payload
                Log.d(TAG, "Sending request with payload: $jsonPayload")

                val request = Request.Builder()
                    .url("$baseUrl/ask")
                    .addHeader("Accept", "application/json")
                    .addHeader("Content-Type", "application/json")
                    .post(jsonPayload.toRequestBody("application/json".toMediaTypeOrNull()))
                    .build()

                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()

                Log.d(TAG, "Response code: ${response.code}")
                Log.d(TAG, "Response body: $responseBody")

                return@withContext when {
                    !response.isSuccessful -> {
                        Log.e(TAG, "HTTP ${response.code}: $responseBody")
                        ChatResult.Error("HTTP ${response.code}")
                    }

                    responseBody == null -> {
                        Log.e(TAG, "Empty response body")
                        ChatResult.Error("Empty response")
                    }

                    else -> {
                        try {
                            // Parse the JSON response to extract the "answer" field
                            val jsonObject = JSONObject(responseBody)
                            if (jsonObject.has("answer")) {
                                val answer = jsonObject.getString("answer")
                                Log.d(TAG, "Successfully extracted answer: $answer")
                                ChatResult.Success(answer)
                            } else {
                                Log.w(
                                    TAG,
                                    "No 'answer' field found in response, returning raw response"
                                )
                                ChatResult.Success(responseBody)
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "JSON parsing failed: ${e.message}, returning raw response")
                            // If JSON parsing fails, return the raw string
                            ChatResult.Success(responseBody)
                        }
                    }
                }
            } catch (e: IOException) {
                Log.e(TAG, "Network error: ${e.message}", e)
                ChatResult.Error("Network error: ${e.message}")
            } catch (e: Exception) {
                Log.e(TAG, "Unexpected error: ${e.message}", e)
                ChatResult.Error("Unexpected error: ${e.message}")
            }
        }
    }
}

// Data class to represent a chat message (keeping for compatibility with existing UI)
data class ChatMessage(
    val role: String, // "user" or "assistant"
    val content: String
)