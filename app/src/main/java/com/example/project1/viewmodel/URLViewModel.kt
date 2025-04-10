package com.example.project1.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.project1.URL.URLData.URLHistoryItem
import com.example.project1.URL.URLData.URLRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class URLViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = URLRepository(application.applicationContext)

    private val _urlHistory = MutableStateFlow<List<URLHistoryItem>>(emptyList())
    val urlHistory: StateFlow<List<URLHistoryItem>> = _urlHistory

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

