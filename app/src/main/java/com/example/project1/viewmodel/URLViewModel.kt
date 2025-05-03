package com.example.project1.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.project1.URL.URLData.URLHistoryItem
import com.example.project1.URL.URLData.URLRepository
import com.example.project1.URL.network.URLNetworkRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class URLViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = URLRepository(application.applicationContext)
    private val networkRepository = URLNetworkRepository()

    private val _urlHistory = MutableStateFlow<List<URLHistoryItem>>(emptyList())
    val urlHistory: StateFlow<List<URLHistoryItem>> = _urlHistory

    private val _scanResult = MutableStateFlow<ScanResult?>(null)
    val scanResult: StateFlow<ScanResult?> = _scanResult

    /**
     * Scans a URL using the API and saves it to history
     */
    fun scanUrl(url: String) {
        _scanResult.value = ScanResult.Loading

        viewModelScope.launch {
            try {
                val result = networkRepository.checkUrlSafety(url)

                result.fold(
                    onSuccess = { isSafe ->
                        _scanResult.value = if (isSafe) ScanResult.Safe else ScanResult.Unsafe
                        saveUrlToHistory(url, isSafe)
                    },
                    onFailure = { exception ->
                        _scanResult.value = ScanResult.Error(exception.message ?: "Unknown error")
                    }
                )
            } catch (e: Exception) {
                _scanResult.value = ScanResult.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun saveUrlToHistory(url: String, isSafe: Boolean) {
        viewModelScope.launch {
            repository.saveUrl(url, isSafe)
            // Reload history after saving
            loadUrlHistory()
        }
    }

    fun loadUrlHistory() {
        viewModelScope.launch {
            try {
                _urlHistory.value = repository.getRecentUrls()
            } catch (e: Exception) {
                // Handle error - at minimum log it
                e.printStackTrace()
                // Set empty list to avoid UI crashes
                _urlHistory.value = emptyList()
            }
        }
    }

    // Make sure to close the database when ViewModel is cleared
    override fun onCleared() {
        super.onCleared()
        // No need to close the database here as it's managed by the singleton
    }
}

// Sealed class to represent the different states of the URL scan
sealed class ScanResult {
    object Loading : ScanResult()
    object Safe : ScanResult()
    object Unsafe : ScanResult()
    data class Error(val message: String) : ScanResult()
}