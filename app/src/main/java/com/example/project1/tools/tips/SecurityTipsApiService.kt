package com.example.project1.tools.tips

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.preference.PreferenceManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import java.io.IOException

// Updated API response data class
data class SecurityTip(
    val image: String // Base64 encoded image
)

// Result wrapper class
sealed class SecurityTipResult {
    data class Success(val tips: List<SecurityTip>) : SecurityTipResult()
    data class Error(val message: String) : SecurityTipResult()
}

class SecurityTipsApiService {
    private val client = OkHttpClient()
    private val baseUrl = "http://phishaware.runasp.net/api"

    suspend fun getSecurityTips(context: Context): SecurityTipResult {
        return withContext(Dispatchers.IO) {
            // Get auth token from preferences
            val authToken = PreferenceManager.getDefaultSharedPreferences(context)
                .getString("AUTH_TOKEN", null)

            if (authToken == null) {
                return@withContext SecurityTipResult.Error("Not authenticated. Please log in again.")
            }

            if (!isNetworkAvailable(context)) {
                return@withContext SecurityTipResult.Error("No internet connection. Please check your network settings.")
            }

            val url = "$baseUrl/securitytips"

            val request = Request.Builder()
                .url(url)
                .get()
                .addHeader("Authorization", "Bearer $authToken")
                .build()

            try {
                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()

                if (response.isSuccessful && responseBody != null) {
                    try {
                        // Parse the response JSON array
                        val jsonArray = JSONArray(responseBody)
                        val tips = mutableListOf<SecurityTip>()

                        for (i in 0 until jsonArray.length()) {
                            val jsonObject = jsonArray.getJSONObject(i)
                            val imageBase64 = jsonObject.getString("image")
                            tips.add(SecurityTip(image = imageBase64))
                        }

                        return@withContext SecurityTipResult.Success(tips)
                    } catch (e: Exception) {
                        return@withContext SecurityTipResult.Error("Failed to parse security tips data: ${e.message}")
                    }
                } else {
                    // Handle API error messages
                    val errorMessage = when (response.code) {
                        401 -> "Authentication failed. Please log in again."
                        404 -> "Security tips not found. The server may be updating content."
                        500 -> "Server error. Please try again later."
                        else -> "Failed to fetch security tips: ${response.message}"
                    }
                    return@withContext SecurityTipResult.Error(errorMessage)
                }
            } catch (e: IOException) {
                return@withContext SecurityTipResult.Error("Network error: Unable to connect to server. Please try again.")
            } catch (e: Exception) {
                return@withContext SecurityTipResult.Error("Unexpected error: ${e.message}")
            }
        }
    }

    // Helper method to check for network connectivity
    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
            return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        } else {
            val networkInfo = connectivityManager.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnected
        }
    }
}