package com.example.project1.SMS.network

import android.util.Log
import com.example.project1.SMS.SMSData.SMSHistoryItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets

/**
 * Network service class for the SMS scanning API
 */
class SMSNetworkService {
    companion object {
        private const val TAG = "SMSNetworkService"
        private const val API_URL = "http://phishaware.runasp.net/api/Prediction/sms"
        private const val HISTORY_API_URL = "http://phishaware.runasp.net/api/Prediction/all_sms"
    }

    /**
     * Sends the SMS message to the API for prediction
     * @param message The SMS message to be checked
     * @param authToken The authentication token for the API
     * @return Boolean indicating whether the SMS is safe (true) or unsafe (false)
     * @throws Exception if there's any network or parsing error
     */
    suspend fun checkSMSSafety(message: String, authToken: String): Boolean =
        withContext(Dispatchers.IO) {
            var connection: HttpURLConnection? = null
            try {
                // Create the connection
                val url = URL(API_URL)
                connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.setRequestProperty("Accept", "application/json")
                connection.setRequestProperty(
                    "Authorization",
                    "Bearer $authToken"
                )
                connection.doOutput = true
                connection.connectTimeout = 30000
                connection.readTimeout = 30000

                // Create the JSON payload
                val jsonPayload = JSONObject().apply {
                    put("message", message)
                }

                // Send the request
                connection.outputStream.use { os ->
                    val input = jsonPayload.toString().toByteArray(StandardCharsets.UTF_8)
                    os.write(input, 0, input.size)
                }

                // Check the response code
                val responseCode = connection.responseCode
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    Log.e(TAG, "Error SMS: HTTP error code: $responseCode")
                    throw Exception("Server returned error code: $responseCode")
                }

                // Read and parse the response
                val response =
                    BufferedReader(InputStreamReader(connection.inputStream)).use { reader ->
                        val response = StringBuilder()
                        var line: String?
                        while (reader.readLine().also { line = it } != null) {
                            response.append(line)
                        }
                        response.toString()
                    }

                // Parse the JSON response
                Log.d(TAG, "API Response: $response")
                val jsonResponse = JSONObject(response)

                // Get the prediction value (1 = safe, -1 = unsafe)
                val prediction = jsonResponse.getInt("final_result")

                // Return true if prediction is 1 (safe), false if -1 (unsafe)
                return@withContext prediction == 1

            } catch (e: Exception) {
                Log.e(TAG, "Error checking SMS safety: ${e.message}", e)
                throw e
            } finally {
                connection?.disconnect()
            }
        }

    /**
     * Fetches the SMS history from the server
     * @param authToken The authentication token for the API
     * @return List of SMSHistoryItem objects
     * @throws Exception if there's any network or parsing error
     */
    suspend fun fetchSMSHistory(authToken: String): List<SMSHistoryItem> =
        withContext(Dispatchers.IO) {
            var connection: HttpURLConnection? = null
            try {
                // Create the connection
                val url = URL(HISTORY_API_URL)
                connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.setRequestProperty("Accept", "application/json")
                connection.setRequestProperty(
                    "Authorization",
                    "Bearer $authToken"
                )
                connection.connectTimeout = 30000
                connection.readTimeout = 30000

                // Check the response code
                val responseCode = connection.responseCode
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    Log.e(TAG, "Error fetching SMS history: HTTP error code: $responseCode")
                    throw Exception("Server returned error code: $responseCode")
                }

                // Read and parse the response
                val response =
                    BufferedReader(InputStreamReader(connection.inputStream)).use { reader ->
                        val response = StringBuilder()
                        var line: String?
                        while (reader.readLine().also { line = it } != null) {
                            response.append(line)
                        }
                        response.toString()
                    }

                // Parse the JSON response
                Log.d(TAG, "History API Response: $response")
                val jsonArray = JSONArray(response)
                val historyItems = mutableListOf<SMSHistoryItem>()

                // Process each SMS history item
                for (i in 0 until jsonArray.length()) {
                    val item = jsonArray.getJSONObject(i)

                    val id = item.optLong("smS_ID", -1L)
                    val message = item.optString("smS_text", "")
                    val scanResult = item.optString("scanResult", "")
                    val timestamp = try {
                        // Parse ISO date string to timestamp
                        val dateStr = item.optString("checkDate", "")
                        if (dateStr.isNotEmpty()) {
                            // Simple parsing to get timestamp from date string
                            // This is a basic implementation - you might want to use a proper date parser
                            java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                                .parse(dateStr.substringBefore(".")).time
                        } else {
                            System.currentTimeMillis()
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error parsing date: ${e.message}")
                        System.currentTimeMillis()
                    }

                    // Convert scan result string to boolean
                    val isSafe = scanResult.equals("Safe", ignoreCase = true)

                    historyItems.add(
                        SMSHistoryItem(
                            id = id,
                            message = message,
                            isSafe = isSafe,
                            timestamp = timestamp
                        )
                    )
                }

                return@withContext historyItems

            } catch (e: Exception) {
                Log.e(TAG, "Error fetching SMS history: ${e.message}", e)
                throw e
            } finally {
                connection?.disconnect()
            }
        }
}