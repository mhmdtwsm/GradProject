package com.example.project1.statistics

import android.content.Context
import android.util.Log
import org.json.JSONObject
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Singleton manager for handling statistics across the application
 */
class StatisticsManager private constructor(private val context: Context) {
    companion object {
        private const val TAG = "StatisticsManager"
        private const val STATISTICS_JSON_FILE = "statistics.json"

        @Volatile
        private var instance: StatisticsManager? = null

        fun getInstance(context: Context): StatisticsManager {
            return instance ?: synchronized(this) {
                instance ?: StatisticsManager(context.applicationContext).also { instance = it }
            }
        }
    }

    // State flow to expose statistics data to UI
    private val _statistics = MutableStateFlow<UserStatistics?>(null)
    val statistics: StateFlow<UserStatistics?> = _statistics

    init {
        loadStatistics()
    }

    /**
     * Updates statistics with data from the server
     */
    fun updateStatistics(statistics: UserStatistics) {
        _statistics.value = statistics
        saveStatisticsToJsonFile(statistics)
        Log.d(TAG, "Updated statistics: $statistics")
    }

    /**
     * Saves statistics to a JSON file
     */
    private fun saveStatisticsToJsonFile(statistics: UserStatistics) {
        try {
            val jsonObject = JSONObject().apply {
                // Use the exact field names from the API response
                put("total_URI", statistics.totalUrls)
                put("safe_Urls", statistics.safeUrls)
                put("unsafeURLs", statistics.unsafeUrls)
                put("total_SMS", statistics.totalSms)
                put("safe_SMS", statistics.safeSms)
                put("unsafe_SMS", statistics.unsafeSms)
                put("lastUpdated", System.currentTimeMillis())
            }

            // Write to file
            val file = File(context.filesDir, STATISTICS_JSON_FILE)
            FileWriter(file).use { writer ->
                writer.write(jsonObject.toString())
            }

            Log.d(TAG, "Statistics saved to JSON file: ${file.absolutePath}")
            Log.d(TAG, "Statistics JSON content: ${jsonObject.toString()}")
        } catch (e: Exception) {
            Log.e(TAG, "Error saving statistics to JSON: ${e.message}", e)
            e.printStackTrace()
        }
    }

    /**
     * Loads statistics from JSON file
     */
    private fun loadStatistics() {
        try {
            val file = File(context.filesDir, STATISTICS_JSON_FILE)

            if (!file.exists()) {
                Log.d(TAG, "Statistics file doesn't exist yet")
                return
            }

            FileReader(file).use { reader ->
                val jsonString = reader.readText()
                val jsonObject = JSONObject(jsonString)

                _statistics.value = UserStatistics(
                    // Match the exact field names from the API response
                    totalUrls = jsonObject.optInt("total_URI", 0),
                    safeUrls = jsonObject.optInt("safe_Urls", 0),
                    unsafeUrls = jsonObject.optInt("unsafeURLs", 0),
                    totalSms = jsonObject.optInt("total_SMS", 0),
                    safeSms = jsonObject.optInt("safe_SMS", 0),
                    unsafeSms = jsonObject.optInt("unsafe_SMS", 0)
                )

                Log.d(TAG, "Loaded statistics: ${_statistics.value}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading statistics from JSON: ${e.message}", e)
        }
    }

    /**
     * Gets statistics as a formatted string for display
     */
    fun getStatisticsAsString(): String {
        val stats = _statistics.value

        return if (stats != null) {
            "URLs: ${stats.safeUrls}/${stats.totalUrls} safe | " +
                    "SMS: ${stats.safeSms}/${stats.totalSms} safe"
        } else {
            "No statistics available"
        }
    }
}