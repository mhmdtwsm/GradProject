package com.example.project1.URL.network

import android.util.Log
import com.example.project1.URL.URLData.URLHistoryItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class URLNetworkRepository {
    private val networkService = URLNetworkService()

    /**
     * Check if a URL is safe by calling the API
     *
     * @param url The URL to check
     * @param authToken The authentication token for the API
     * @return A Result object containing either success with boolean (true=safe) or failure with exception
     */
    suspend fun checkUrlSafety(url: String, authToken: String): Result<Boolean> =
        withContext(Dispatchers.IO) {
            try {
                val isSafe = networkService.checkUrlSafety(url, authToken)
                Result.success(isSafe)
            } catch (e: Exception) {
                Log.e("URLNetworkRepository", "Error checking URL safety: ${e.message}", e)
                Result.failure(e)
            }
        }

    /**
     * Fetch URL history from the server
     *
     * @param authToken The authentication token for the API
     * @return A Result object containing either success with a list of URL history items or failure with exception
     */
    suspend fun fetchUrlHistory(authToken: String): Result<List<URLHistoryItem>> =
        withContext(Dispatchers.IO) {
            try {
                val historyItems = networkService.fetchUrlHistory(authToken)
                Result.success(historyItems)
            } catch (e: Exception) {
                Log.e("URLNetworkRepository", "Error fetching URL history: ${e.message}", e)
                Result.failure(e)
            }
        }
}