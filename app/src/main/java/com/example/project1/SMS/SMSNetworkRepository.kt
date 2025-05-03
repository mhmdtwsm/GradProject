package com.example.project1.SMS

import android.util.Log
import com.example.project1.SMS.network.SMSNetworkService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SMSNetworkRepository {
    private val networkService = SMSNetworkService()

    /**
     * Check if an SMS message is safe by calling the API
     *
     * @param message The SMS message to check
     * @return A Result object containing either success with boolean (true=safe) or failure with exception
     */
    suspend fun checkSMSSafety(message: String): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val isSafe = networkService.checkSMSSafety(message)
            Result.success(isSafe)
        } catch (e: Exception) {
            Log.e("SMSNetworkRepository", "Error checking SMS safety: ${e.message}", e)
            Result.failure(e)
        }
    }
}