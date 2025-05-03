package com.example.project1.URL.network

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class URLNetworkRepository {
    private val networkService = URLNetworkService()

    /**
     * Check if a URL is safe by calling the API
     *
     * @param url The URL to check
     * @return A Result object containing either success with boolean (true=safe) or failure with exception
     */
    suspend fun checkUrlSafety(url: String): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val isSafe = networkService.checkUrlSafety(url)
            Result.success(isSafe)
        } catch (e: Exception) {
            Log.e("URLNetworkRepository", "Error checking URL safety: ${e.message}", e)
            Result.failure(e)
        }
    }
}