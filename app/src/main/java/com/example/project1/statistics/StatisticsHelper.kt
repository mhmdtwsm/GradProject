package com.example.project1.statistics

import android.content.Context
import androidx.preference.PreferenceManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Helper class to simplify statistics operations in UI components
 */
class StatisticsHelper(private val context: Context) {
    private val statisticsManager = StatisticsManager.getInstance(context)
    private val statisticsService = StatisticsNetworkService()

    /**
     * Refreshes statistics data from the server
     * @return Flow that emits true when refresh is successful, false otherwise
     */
    fun refreshStatistics(): Flow<Boolean> = flow {
        try {
            val authToken = PreferenceManager.getDefaultSharedPreferences(context)
                .getString("AUTH_TOKEN", null)

            if (authToken != null) {
                val statistics = withContext(Dispatchers.IO) {
                    statisticsService.fetchUserStatistics(authToken)
                }
                statisticsManager.updateStatistics(statistics)
                emit(true)
            } else {
                emit(false)
            }
        } catch (e: Exception) {
            emit(false)
        }
    }

    /**
     * Gets the current statistics
     * @return Flow of UserStatistics
     */
    fun getStatistics() = statisticsManager.statistics

    /**
     * Gets the URL safety ratio as a formatted string
     * @return String in format "X/Y"
     */
    fun getUrlSafetyRatio(): String {
        val stats = statisticsManager.statistics.value
        return if (stats != null) {
            "${stats.safeUrls}/${stats.totalUrls}"
        } else {
            "0/0"
        }
    }

    /**
     * Gets the SMS safety ratio as a formatted string
     * @return String in format "X/Y"
     */
    fun getSmsSafetyRatio(): String {
        val stats = statisticsManager.statistics.value
        return if (stats != null) {
            "${stats.safeSms}/${stats.totalSms}"
        } else {
            "0/0"
        }
    }

    /**
     * Gets the URL safety percentage
     * @return Float between 0 and 100
     */
    fun getUrlSafetyPercentage(): Float {
        val stats = statisticsManager.statistics.value
        return if (stats != null && stats.totalUrls > 0) {
            (stats.safeUrls.toFloat() / stats.totalUrls) * 100f
        } else {
            0f
        }
    }

    /**
     * Gets the SMS safety percentage
     * @return Float between 0 and 100
     */
    fun getSmsSafetyPercentage(): Float {
        val stats = statisticsManager.statistics.value
        return if (stats != null && stats.totalSms > 0) {
            (stats.safeSms.toFloat() / stats.totalSms) * 100f
        } else {
            0f
        }
    }

    /**
     * Refreshes statistics in the background
     */
    fun refreshStatisticsInBackground() {
        CoroutineScope(Dispatchers.IO).launch {
            refreshStatistics().collect { /* No action needed */ }
        }
    }
}
