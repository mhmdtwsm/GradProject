package com.example.project1.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.preference.PreferenceManager
import com.example.project1.SMS.SMSData.SMSHistoryItem
import com.example.project1.SMS.SMSData.SMSRepository
import com.example.project1.SMS.SMSNetworkRepository
import com.example.project1.statistics.StatisticsManager
import com.example.project1.statistics.StatisticsNetworkService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileWriter
import java.io.FileReader
import org.json.JSONArray
import org.json.JSONObject

class SMSViewModel(application: Application) : AndroidViewModel(application) {
    // Toggle between SQLite (false) and JSON (true)
    companion object {
        private const val USE_JSON_HISTORY = true
        private const val HISTORY_JSON_FILE = "sms_history.json"
        private const val TAG = "SMSViewModel"
    }

    // SQLite Repository (Used when USE_JSON_HISTORY = false)
    private val sqliteRepository = SMSRepository(application)

    // Network Repositories
    private val networkRepository = SMSNetworkRepository()
    private val statisticsService = StatisticsNetworkService()
    private val context = application.applicationContext
    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(application)

    // Statistics Manager - shared across ViewModels
    private val statisticsManager = StatisticsManager.getInstance(application)

    // UI State
    private val _smsText = MutableStateFlow("")
    val smsText: StateFlow<String> = _smsText

    private val _smsHistory = MutableStateFlow<List<SMSHistoryItem>>(emptyList())
    val smsHistory: StateFlow<List<SMSHistoryItem>> = _smsHistory

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Loading state for statistics
    private val _isStatisticsLoading = MutableStateFlow(false)
    val isStatisticsLoading: StateFlow<Boolean> = _isStatisticsLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    // Use the statistics from the StatisticsManager instead of maintaining our own
    val statistics = statisticsManager.statistics

    init {
        loadRecentSMS()
    }

    fun onSMSTextChange(text: String) {
        _smsText.value = text
    }

    fun loadRecentSMS() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _smsHistory.value = if (USE_JSON_HISTORY) {
                    // Load from JSON file
                    loadHistoryFromJsonFile()
                } else {
                    // Load from SQLite database
                    sqliteRepository.getRecentSMS()
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load history: ${e.message}"
                Log.e(TAG, "Failed to load history: ${e.message}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun checkSMS() {
        val message = _smsText.value.trim()
        if (message.isEmpty()) {
            _errorMessage.value = "Please enter a message to check"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                // Get authentication token
                val authToken = sharedPreferences.getString("AUTH_TOKEN", null)
                    ?: throw Exception("Not authenticated")

                // Clear input field
                _smsText.value = ""

                // Make network request
                networkRepository.checkSMSSafety(message, authToken).fold(
                    onSuccess = { isSafe ->
                        Log.d(TAG, "SMS check result: ${if (isSafe) "Safe" else "Unsafe"}")

                        if (USE_JSON_HISTORY) {
                            // JSON IMPLEMENTATION: After successful scan, fetch updated history from server
                            fetchAndUpdateHistory(authToken)
                            // Fetch updated statistics after scan
                            fetchAndUpdateStatistics(authToken)
                        } else {
                            // SQLITE IMPLEMENTATION: Save SMS to SQLite database
                            sqliteRepository.saveSMS(message, isSafe)
                            loadRecentSMS()
                            // Still fetch statistics even when using SQLite
                            fetchAndUpdateStatistics(authToken)
                        }
                    },
                    onFailure = { error ->
                        if (!USE_JSON_HISTORY) {
                            sqliteRepository.saveSMS(message, null)
                        }
                        val errorMsg = error.message ?: "Failed to check SMS"
                        Log.e(TAG, errorMsg, error)
                        _errorMessage.value = errorMsg
                        loadRecentSMS()
                    }
                )
            } catch (e: Exception) {
                if (!USE_JSON_HISTORY) {
                    sqliteRepository.saveSMS(message, null)
                }
                val errorMsg = when (e.message) {
                    "Not authenticated" -> "Please login to use this feature"
                    else -> e.message ?: "Failed to check SMS"
                }
                Log.e(TAG, errorMsg, e)
                _errorMessage.value = errorMsg
                loadRecentSMS()
            } finally {
                _isLoading.value = false
            }
        }
    }

    // STATISTICS IMPLEMENTATION
    // ========================

    /**
     * Fetches user statistics from server and updates the shared statistics manager
     */
    fun fetchAndUpdateStatistics(authToken: String) {
        viewModelScope.launch {
            _isStatisticsLoading.value = true
            try {
                try {
                    val stats = statisticsService.fetchUserStatistics(authToken)
                    // Update the shared statistics manager
                    statisticsManager.updateStatistics(stats)
                    Log.d(TAG, "Updated statistics: $stats")
                } catch (exception: Exception) {
                    Log.e(TAG, "Error fetching statistics: ${exception.message}", exception)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error in fetchAndUpdateStatistics: ${e.message}", e)
            } finally {
                _isStatisticsLoading.value = false
            }
        }
    }

    // JSON IMPLEMENTATION METHODS
    // ==========================

    /**
     * Fetches SMS history from server and saves it to a JSON file
     */
    private suspend fun fetchAndUpdateHistory(authToken: String) {
        try {
            // Fetch history from server
            val historyResult = networkRepository.fetchSMSHistory(authToken)

            historyResult.fold(
                onSuccess = { historyItems ->
                    // Save to local JSON file
                    saveHistoryToJsonFile(historyItems)

                    // Update the UI
                    _smsHistory.value = historyItems
                },
                onFailure = { exception ->
                    Log.e(TAG, "Error fetching history: ${exception.message}", exception)
                    _errorMessage.value = "Failed to fetch history: ${exception.message}"
                    loadHistoryFromJsonFile() // Try to load from cache if server fetch fails
                }
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error in fetchAndUpdateHistory: ${e.message}", e)
            _errorMessage.value = "Error updating history: ${e.message}"
        }
    }

    /**
     * Saves SMS history list to a JSON file
     */
    private fun saveHistoryToJsonFile(historyItems: List<SMSHistoryItem>) {
        try {
            val jsonArray = JSONArray()

            // Convert history items to JSON objects
            historyItems.forEach { item ->
                val jsonObject = JSONObject().apply {
                    put("id", item.id)
                    put("message", item.message)
                    put("isSafe", item.isSafe)
                    put("timestamp", item.timestamp)
                }
                jsonArray.put(jsonObject)
            }

            // Write to file
            val file = File(getApplication<Application>().filesDir, HISTORY_JSON_FILE)
            FileWriter(file).use { writer ->
                writer.write(jsonArray.toString())
            }

            Log.d(TAG, "History saved to JSON file: ${file.absolutePath}")
        } catch (e: Exception) {
            Log.e(TAG, "Error saving history to JSON: ${e.message}", e)
        }
    }

    /**
     * Loads SMS history from JSON file
     */
    private fun loadHistoryFromJsonFile(): List<SMSHistoryItem> {
        try {
            val file = File(getApplication<Application>().filesDir, HISTORY_JSON_FILE)

            if (!file.exists()) {
                Log.d(TAG, "History file doesn't exist yet")
                return emptyList()
            }

            FileReader(file).use { reader ->
                val jsonString = reader.readText()
                val jsonArray = JSONArray(jsonString)
                val historyItems = mutableListOf<SMSHistoryItem>()

                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    historyItems.add(
                        SMSHistoryItem(
                            id = jsonObject.optLong("id", i.toLong()),
                            message = jsonObject.getString("message"),
                            isSafe = jsonObject.getBoolean("isSafe"),
                            timestamp = jsonObject.getLong("timestamp")
                        )
                    )
                }

                return historyItems
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading history from JSON: ${e.message}", e)
            return emptyList()
        }
    }

    fun pasteFromClipboard(text: String) {
        _smsText.value = text
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    /**
     * Gets statistics as a formatted string for display
     */
    fun getStatisticsAsString(): String {
        return statisticsManager.getStatisticsAsString()
    }

    /**
     * Manually refresh statistics - called from UI
     */
    fun refreshStatistics() {
        viewModelScope.launch {
            try {
                val authToken = sharedPreferences.getString("AUTH_TOKEN", null)
                    ?: throw Exception("Not authenticated")

                fetchAndUpdateStatistics(authToken)
            } catch (e: Exception) {
                Log.e(TAG, "Error refreshing statistics: ${e.message}", e)
            }
        }
    }
}