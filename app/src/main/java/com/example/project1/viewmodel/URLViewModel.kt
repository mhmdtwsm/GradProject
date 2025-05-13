package com.example.project1.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.preference.PreferenceManager
import com.example.project1.URL.URLData.URLHistoryItem
import com.example.project1.URL.URLData.URLRepository
import com.example.project1.URL.network.URLNetworkRepository
import com.example.project1.statistics.StatisticsManager
import com.example.project1.statistics.StatisticsNetworkService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileWriter
import org.json.JSONArray
import org.json.JSONObject
import java.io.FileReader

class URLViewModel(application: Application) : AndroidViewModel(application) {
    // Toggle between SQLite (false) and JSON (true)
    companion object {
        private const val USE_JSON_HISTORY = true
        private const val HISTORY_JSON_FILE = "url_history.json"
        private const val TAG = "URLViewModel"
    }

    // SQLite Repository (Used when USE_JSON_HISTORY = false)
    private val sqliteRepository = URLRepository(application.applicationContext)

    // Network Repositories
    private val networkRepository = URLNetworkRepository()
    private val statisticsService = StatisticsNetworkService()
    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(application)

    // Statistics Manager - shared across ViewModels
    private val statisticsManager = StatisticsManager.getInstance(application)

    // State flows to expose data to UI
    private val _urlHistory = MutableStateFlow<List<URLHistoryItem>>(emptyList())
    val urlHistory: StateFlow<List<URLHistoryItem>> = _urlHistory

    private val _scanResult = MutableStateFlow<ScanResult?>(null)
    val scanResult: StateFlow<ScanResult?> = _scanResult

    // Loading state for statistics
    private val _isStatisticsLoading = MutableStateFlow(false)
    val isStatisticsLoading: StateFlow<Boolean> = _isStatisticsLoading

    // Use the statistics from the StatisticsManager instead of maintaining our own
    val statistics = statisticsManager.statistics

    init {
        loadUrlHistory()
    }

    fun scanUrl(url: String) {
        _scanResult.value = ScanResult.Loading

        viewModelScope.launch {
            try {
                val authToken = sharedPreferences.getString("AUTH_TOKEN", null)
                    ?: throw Exception("Not authenticated")

                val result = networkRepository.checkUrlSafety(url, authToken)

                result.fold(
                    onSuccess = { isSafe ->
                        _scanResult.value = if (isSafe) ScanResult.Safe else ScanResult.Unsafe

                        if (USE_JSON_HISTORY) {
                            // JSON IMPLEMENTATION: After successful scan, fetch updated history from server
                            fetchAndUpdateHistory(authToken)
                        } else {
                            // SQLITE IMPLEMENTATION: Save URL to SQLite database
                            saveUrlToHistory(url, isSafe)
                        }

                        // Always fetch updated statistics after a scan
                        // This needs to happen regardless of storage type
                        fetchAndUpdateStatistics(authToken)
                    },
                    onFailure = { exception ->
                        _scanResult.value = ScanResult.Error(exception.message ?: "Unknown error")
                    }
                )
            } catch (e: Exception) {
                Log.e(TAG, "Error scanning URL: ${e.message}", e)
                _scanResult.value = ScanResult.Error(e.message ?: "Unknown error")
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
                Log.d(TAG, "Fetching statistics from server...")
                try {
                    val stats = statisticsService.fetchUserStatistics(authToken)
                    // Update the shared statistics manager
                    statisticsManager.updateStatistics(stats)
                    Log.d(TAG, "Updated statistics successfully: $stats")
                } catch (exception: Exception) {
                    Log.e(TAG, "Error fetching statistics: ${exception.message}", exception)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception in fetchAndUpdateStatistics: ${e.message}", e)
            } finally {
                _isStatisticsLoading.value = false
            }
        }
    }

    // JSON IMPLEMENTATION METHODS
    // ==========================

    /**
     * Fetches URL history from server and saves it to a JSON file
     */
    private suspend fun fetchAndUpdateHistory(authToken: String) {
        try {
            // Fetch history from server
            val historyResult = networkRepository.fetchUrlHistory(authToken)

            historyResult.fold(
                onSuccess = { historyItems ->
                    // Save to local JSON file
                    saveHistoryToJsonFile(historyItems)

                    // Update the UI
                    _urlHistory.value = historyItems
                },
                onFailure = { exception ->
                    Log.e(TAG, "Error fetching history: ${exception.message}", exception)
                }
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error in fetchAndUpdateHistory: ${e.message}", e)
        }
    }

    /**
     * Saves URL history list to a JSON file
     */
    private fun saveHistoryToJsonFile(historyItems: List<URLHistoryItem>) {
        try {
            val jsonArray = JSONArray()

            // Convert history items to JSON objects
            historyItems.forEach { item ->
                val jsonObject = JSONObject().apply {
                    put("url", item.url)
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
     * Loads URL history from JSON file
     */
    private fun loadHistoryFromJsonFile(): List<URLHistoryItem> {
        try {
            val file = File(getApplication<Application>().filesDir, HISTORY_JSON_FILE)

            if (!file.exists()) {
                Log.d(TAG, "History file doesn't exist yet")
                return emptyList()
            }

            FileReader(file).use { reader ->
                val jsonString = reader.readText()
                val jsonArray = JSONArray(jsonString)
                val historyItems = mutableListOf<URLHistoryItem>()

                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    historyItems.add(
                        URLHistoryItem(
                            id = i.toLong(),
                            url = jsonObject.getString("url"),
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

    // SQLITE METHODS (Used when USE_JSON_HISTORY = false)
    // ==================================================

    /**
     * Saves a URL to the SQLite history database
     */
    fun saveUrlToHistory(url: String, isSafe: Boolean) {
        viewModelScope.launch {
            if (!USE_JSON_HISTORY) {
                sqliteRepository.saveUrl(url, isSafe)
                loadUrlHistory()
            }
        }
    }

    /**
     * Loads URL history from appropriate source based on USE_JSON_HISTORY
     */
    fun loadUrlHistory() {
        viewModelScope.launch {
            try {
                _urlHistory.value = if (USE_JSON_HISTORY) {
                    // Load from JSON file
                    loadHistoryFromJsonFile()
                } else {
                    // Load from SQLite database
                    sqliteRepository.getRecentUrls()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _urlHistory.value = emptyList()
            }
        }
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

    override fun onCleared() {
        super.onCleared()
    }
}

sealed class ScanResult {
    object Loading : ScanResult()
    object Safe : ScanResult()
    object Unsafe : ScanResult()
    data class Error(val message: String) : ScanResult()
}