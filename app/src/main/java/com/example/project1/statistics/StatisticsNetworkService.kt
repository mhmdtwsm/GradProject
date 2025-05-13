package com.example.project1.statistics

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

/**
 * Network service for fetching user statistics from the server
 */
class StatisticsNetworkService {
    companion object {
        private const val TAG = "StatisticsNetworkService"
        private const val API_URL = "http://phishaware.runasp.net/api/statistics/me"
    }

    /**
     * Fetches user statistics from the server
     * @param authToken The authentication token for the API
     * @return UserStatistics object with the statistics data
     * @throws Exception if there's any network or parsing error
     */
    suspend fun fetchUserStatistics(authToken: String): UserStatistics =
        withContext(Dispatchers.IO) {
            var connection: HttpURLConnection? = null
            try {
                Log.d(TAG, "Fetching user statistics with token: ${authToken.take(5)}...")

                // Create the connection
                val url = URL(API_URL)
                connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.setRequestProperty("Accept", "application/json")
                connection.setRequestProperty(
                    "Authorization",
                    "Bearer $authToken"
                )
                connection.connectTimeout = 30000 // 30 seconds
                connection.readTimeout = 30000 // 30 seconds

                // Check the response code
                val responseCode = connection.responseCode
                Log.d(TAG, "Statistics API response code: $responseCode")

                if (responseCode != HttpURLConnection.HTTP_OK) {
                    val errorStream = connection.errorStream
                    val errorResponse = errorStream?.let {
                        BufferedReader(InputStreamReader(it)).use { reader ->
                            val response = StringBuilder()
                            var line: String?
                            while (reader.readLine().also { line = it } != null) {
                                response.append(line)
                            }
                            response.toString()
                        }
                    } ?: "No error details available"

                    Log.e(TAG, "HTTP error code: $responseCode, Error: $errorResponse")
                    throw Exception("Server returned error code: $responseCode - $errorResponse")
                }

                // Read the response
                val response =
                    BufferedReader(InputStreamReader(connection.inputStream)).use { reader ->
                        val response = StringBuilder()
                        var line: String?
                        while (reader.readLine().also { line = it } != null) {
                            response.append(line)
                        }
                        response.toString()
                    }

                Log.d(TAG, "Statistics API Response: $response")

                if (response.isBlank()) {
                    Log.e(TAG, "Empty response from statistics API")
                    throw Exception("Empty response from server")
                }

                // Parse JSON response
                try {
                    val jsonObject = JSONObject(response)

                    // Log all keys in the response for debugging
                    val keys = jsonObject.keys()
                    val keyList = mutableListOf<String>()
                    while (keys.hasNext()) {
                        keyList.add(keys.next())
                    }
                    Log.d(TAG, "Response keys: $keyList")

                    // Create statistics object with default values of 0 if fields are missing
                    val statistics = UserStatistics(
                        totalUrls = jsonObject.optInt("total_URI", 0),
                        safeUrls = jsonObject.optInt("safe_Urls", 0),
                        unsafeUrls = jsonObject.optInt("unsafeURLs", 0),
                        totalSms = jsonObject.optInt("total_SMS", 0),
                        safeSms = jsonObject.optInt("safe_SMS", 0),
                        unsafeSms = jsonObject.optInt("unsafe_SMS", 0)
                    )

                    Log.d(TAG, "Parsed statistics: $statistics")
                    return@withContext statistics
                } catch (e: Exception) {
                    Log.e(TAG, "Error parsing JSON response: ${e.message}", e)
                    throw Exception("Failed to parse statistics data: ${e.message}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching statistics: ${e.message}", e)
                // Create default statistics object with zeros when API fails
                // This ensures we never return null
                return@withContext UserStatistics(
                    totalUrls = 0,
                    safeUrls = 0,
                    unsafeUrls = 0,
                    totalSms = 0,
                    safeSms = 0,
                    unsafeSms = 0
                )
            } finally {
                connection?.disconnect()
            }
        }
}

/**
 * Data class for user statistics
 */
data class UserStatistics(
    val totalUrls: Int,
    val safeUrls: Int,
    val unsafeUrls: Int,
    val totalSms: Int,
    val safeSms: Int,
    val unsafeSms: Int
) {
    override fun toString(): String {
        return "UserStatistics(totalUrls=$totalUrls, safeUrls=$safeUrls, unsafeUrls=$unsafeUrls, " +
                "totalSms=$totalSms, safeSms=$safeSms, unsafeSms=$unsafeSms)"
    }
}