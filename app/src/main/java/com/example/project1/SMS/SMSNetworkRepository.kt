package com.example.project1.SMS

import android.util.Log
import com.example.project1.SMS.SMSData.SMSHistoryItem
import com.example.project1.SMS.network.SMSNetworkService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SMSNetworkRepository {
    private val networkService = SMSNetworkService()
    private val TAG = "SMSNetworkRepository"

    /**
     * Check if an SMS message is safe by calling the API
     *
     * @param message The SMS message to check
     * @param authToken The authentication token for the API
     * @return A Result object containing either success with boolean (true=safe) or failure with exception
     */
    suspend fun checkSMSSafety(message: String, authToken: String): Result<Boolean> =
        withContext(Dispatchers.IO) {
            try {
                val isSafe = networkService.checkSMSSafety(message, authToken)
                Result.success(isSafe)
            } catch (e: Exception) {
                Log.e(TAG, "Error checking SMS safety: ${e.message}", e)
                Result.failure(e)
            }
        }

    /**
     * Fetch SMS history from the server
     *
     * @param authToken The authentication token for the API
     * @return A Result object containing either success with list of SMS history items or failure with exception
     */
    suspend fun fetchSMSHistory(authToken: String): Result<List<SMSHistoryItem>> =
        withContext(Dispatchers.IO) {
            try {
                val historyItems = networkService.fetchSMSHistory(authToken)
                Result.success(historyItems)
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching SMS history: ${e.message}", e)
                Result.failure(e)
            }
        }
}