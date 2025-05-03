package com.example.project1.tools.tips

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.preference.PreferenceManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

// API response data class
data class SecurityTip(
    val imageUrl: String,
    val tipText: String
)

// Result wrapper class - Note: No Loading object in this sealed class
sealed class SecurityTipResult {
    data class Success(val tip: SecurityTip) : SecurityTipResult()
    data class Error(val message: String) : SecurityTipResult()
    // Loading is removed since it's causing an error
}

class SecurityTipsApiService {
    private val client = OkHttpClient()
    private val baseUrl = "http://phishaware.runasp.net/api"

    suspend fun getSecurityTip(context: Context): SecurityTipResult {
        return withContext(Dispatchers.IO) {
            // Get auth token from preferences
            val authToken = PreferenceManager.getDefaultSharedPreferences(context)
                .getString("AUTH_TOKEN", null)

            if (authToken == null) {
                return@withContext SecurityTipResult.Error("Not authenticated. Please log in again.")
            }


            val url = "$baseUrl/securitytips"

            val request = Request.Builder()
                .url(url)
                .get()
                // Try with "Authorization" header instead of "Authentication"
                .addHeader("Authorization", "Bearer $authToken")
                .build()

            try {
                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()

                if (response.isSuccessful && responseBody != null) {
                    try {
                        // Parse the response JSON
                        val jsonObject = org.json.JSONObject(responseBody)
                        val imageUrl = jsonObject.getString("imageUrl")
                        val tipText = jsonObject.getString("tipText")

                        return@withContext SecurityTipResult.Success(
                            SecurityTip(
                                imageUrl = imageUrl,
                                tipText = tipText
                            )
                        )
                    } catch (e: Exception) {
                        return@withContext SecurityTipResult.Error("Failed to parse security tip data: ${e.message}")
                    }
                } else {
                    // Handle API error messages
                    val errorMessage = when (response.code) {
                        401 -> "Authentication failed. Please log in again."
                        404 -> "Security tip not found. The server may be updating content."
                        500 -> "Server error. Please try again later."
                        else -> "Failed to fetch security tip: ${response.message}"
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

    // Add this helper method to check for network connectivity
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
