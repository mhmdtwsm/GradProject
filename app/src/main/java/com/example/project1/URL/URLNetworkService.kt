package com.example.project1.URL.network

import android.util.Log
import com.example.project1.URL.URLData.URLHistoryItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Network service class for the URL scanning API
 */
class URLNetworkService {
    companion object {
        private const val TAG = "URLNetworkService"
        private const val API_URL_SCAN = "http://phishaware.runasp.net/api/Prediction/url"
        private const val API_URL_HISTORY = "http://phishaware.runasp.net/api/Prediction/all_urls"
    }

    suspend fun checkUrlSafety(urlToCheck: String, authToken: String): Boolean =
        withContext(Dispatchers.IO) {
            var connection: HttpURLConnection? = null

            try {
                // Create the connection
                val url = URL(API_URL_SCAN)
                connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.setRequestProperty("Accept", "application/json")
                connection.setRequestProperty(
                    "Authorization",
                    "Bearer $authToken"
                ) // Add auth header
                connection.doOutput = true
                connection.connectTimeout = 20000 // 20 seconds
                connection.readTimeout = 20000 // 20 seconds

                val jsonPayload = JSONObject().apply {
                    put("url", urlToCheck)
                }

                connection.outputStream.use { os ->
                    val input = jsonPayload.toString().toByteArray(StandardCharsets.UTF_8)
                    os.write(input, 0, input.size)
                }

                val responseCode = connection.responseCode
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    Log.e(TAG, "HTTP error code: $responseCode")
                    throw Exception("Server returned error code: $responseCode")
                }

                val response =
                    BufferedReader(InputStreamReader(connection.inputStream)).use { reader ->
                        val response = StringBuilder()
                        var line: String?
                        while (reader.readLine().also { line = it } != null) {
                            response.append(line)
                        }
                        response.toString()
                    }

                Log.d(TAG, "API Response: $response")
                val jsonResponse = JSONObject(response)
                val prediction = jsonResponse.getInt("prediction")

                return@withContext prediction == 1

            } catch (e: Exception) {
                Log.e(TAG, "Error checking URL safety: ${e.message}", e)
                throw e
            } finally {
                connection?.disconnect()
            }
        }

    /**
     * Fetch URL history from the server
     *
     * @param authToken The authentication token for the API
     * @return List of URLHistoryItem objects representing the URL history
     */
    suspend fun fetchUrlHistory(authToken: String): List<URLHistoryItem> =
        withContext(Dispatchers.IO) {
            var connection: HttpURLConnection? = null

            try {
                // Create the connection
                val url = URL(API_URL_HISTORY)
                connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.setRequestProperty("Accept", "application/json")
                connection.setRequestProperty(
                    "Authorization",
                    "Bearer $authToken"
                )
                connection.connectTimeout = 20000 // 20 seconds
                connection.readTimeout = 20000 // 20 seconds

                val responseCode = connection.responseCode
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    Log.e(TAG, "HTTP error code: $responseCode")
                    throw Exception("Server returned error code: $responseCode")
                }

                val response =
                    BufferedReader(InputStreamReader(connection.inputStream)).use { reader ->
                        val response = StringBuilder()
                        var line: String?
                        while (reader.readLine().also { line = it } != null) {
                            response.append(line)
                        }
                        response.toString()
                    }

                Log.d(TAG, "History API Response: $response")

                // Parse JSON response
                val historyItems = mutableListOf<URLHistoryItem>()
                val jsonArray = JSONArray(response)

                for (i in 0 until jsonArray.length()) {
                    val item = jsonArray.getJSONObject(i)

                    // Extract data from JSON
                    val url = item.getString("urL_string")
                    val scanResult = item.getString("scanResult")
                    val isSafe = scanResult.equals("Safe", ignoreCase = true)

                    // Extract timestamp
                    val dateString = item.getString("checkDate")
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
                    val date = try {
                        dateFormat.parse(dateString.substringBefore('.'))
                    } catch (e: Exception) {
                        Log.w(TAG, "Error parsing date: $dateString", e)
                        null
                    }

                    val timestamp = date?.time ?: System.currentTimeMillis()

                    // Create URLHistoryItem
                    historyItems.add(
                        URLHistoryItem(
                            id = item.optLong("url_ID", i.toLong()),
                            url = url,
                            isSafe = isSafe,
                            timestamp = timestamp
                        )
                    )
                }

                return@withContext historyItems

            } catch (e: Exception) {
                Log.e(TAG, "Error fetching URL history: ${e.message}", e)
                throw e
            } finally {
                connection?.disconnect()
            }
        }
}