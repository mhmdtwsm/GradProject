package com.example.project1.URL.network

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets

/**
 * Network service class for the URL scanning API
 */
class URLNetworkService {
    companion object {
        private const val TAG = "URLNetworkService"
        private const val API_URL = "http://82.29.179.253:8000/predict_url"
    }

    /**
     * Sends the URL to the API for prediction
     * @param urlToCheck The URL to be checked
     * @return Boolean indicating whether the URL is safe (true) or unsafe (false)
     * @throws Exception if there's any network or parsing error
     */
    suspend fun checkUrlSafety(urlToCheck: String): Boolean = withContext(Dispatchers.IO) {
        var connection: HttpURLConnection? = null
        try {
            // Create the connection
            val url = URL(API_URL)
            connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.setRequestProperty("Accept", "application/json")
            connection.doOutput = true
            connection.connectTimeout = 10000 // 10 seconds
            connection.readTimeout = 10000 // 10 seconds

            // Create the JSON payload
            val jsonPayload = JSONObject().apply {
                put("url", urlToCheck)
            }

            // Send the request
            connection.outputStream.use { os ->
                val input = jsonPayload.toString().toByteArray(StandardCharsets.UTF_8)
                os.write(input, 0, input.size)
            }

            // Check the response code
            val responseCode = connection.responseCode
            if (responseCode != HttpURLConnection.HTTP_OK) {
                Log.e(TAG, "HTTP error code: $responseCode")
                throw Exception("Server returned error code: $responseCode")
            }

            // Read and parse the response
            val response = BufferedReader(InputStreamReader(connection.inputStream)).use { reader ->
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
            val prediction = jsonResponse.getInt("prediction")

            // Return true if prediction is 1 (safe), false if -1 (unsafe)
            return@withContext prediction == 1

        } catch (e: Exception) {
            Log.e(TAG, "Error checking URL safety: ${e.message}", e)
            throw e
        } finally {
            connection?.disconnect()
        }
    }
}