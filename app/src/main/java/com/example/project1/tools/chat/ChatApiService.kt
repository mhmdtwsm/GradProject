package com.example.project1.chat

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

    private val baseUrl = "http://82.29.179.253:8000"

    sealed class ChatResult {
        data class Success(val response: String) : ChatResult()
        data class Error(val message: String) : ChatResult()
    }

    suspend fun sendQuestion(question: String): ChatResult {
        return withContext(Dispatchers.IO) {
            try {
                val jsonPayload = JSONObject().apply {
                    put("question", question)
                }.toString()

                val request = Request.Builder()
                    .url("$baseUrl/chat")
                    .post(jsonPayload.toRequestBody("application/json".toMediaTypeOrNull()))
                    .build()

                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()

                return@withContext when {
                    !response.isSuccessful -> ChatResult.Error("HTTP ${response.code}")
                    responseBody == null -> ChatResult.Error("Empty response")
                    responseBody.startsWith("{") -> {
                        try {
                            val json = JSONObject(responseBody)
                            ChatResult.Success(json.optString("response", "No response provided"))
                        } catch (e: Exception) {
                            ChatResult.Error("Invalid JSON response")
                        }
                    }

                    else -> ChatResult.Error("Unexpected response format")
                }
            } catch (e: IOException) {
                ChatResult.Error("Network error: ${e.message}")
            } catch (e: Exception) {
                ChatResult.Error("Unexpected error: ${e.message}")
            }
        }
    }
}