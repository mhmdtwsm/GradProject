package com.example.project1.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.project1.SMS.SMSData.SMSHistoryItem
import com.example.project1.SMS.SMSData.SMSRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException

class SMSViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = SMSRepository(application)

    private val _smsText = MutableStateFlow("")
    val smsText: StateFlow<String> = _smsText

    private val _smsHistory = MutableStateFlow<List<SMSHistoryItem>>(emptyList())
    val smsHistory: StateFlow<List<SMSHistoryItem>> = _smsHistory

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        loadRecentSMS()
    }

    fun onSMSTextChange(text: String) {
        _smsText.value = text
    }

    fun loadRecentSMS() {
        viewModelScope.launch {
            _isLoading.value = true
            val messages = repository.getRecentSMS()
            _smsHistory.value = messages
            _isLoading.value = false
        }
    }

    fun checkSMS() {
        val message = _smsText.value.trim()
        if (message.isEmpty()) return

        viewModelScope.launch {
            _isLoading.value = true

            // Clear the input field
            _smsText.value = ""

            try {
                // Simulate server request
                // In a real app, you would make an API call to your server here
                val serverResponse = checkWithServer(message)

                // Save to database with server response
                repository.saveSMS(message, serverResponse)
            } catch (e: IOException) {
                // Handle connection error - save with null safety status
                // We'll use a third state (null) to represent "No connection"
                repository.saveSMS(message, null)
            }

            // Reload messages
            loadRecentSMS()

            _isLoading.value = false
        }
    }

    fun pasteFromClipboard(text: String) {
        _smsText.value = text
    }

    // This is just a simulation - replace with actual API call
    private suspend fun checkWithServer(message: String): Boolean {
        kotlinx.coroutines.delay(500) // Simulate network delay

        // Simulate network error sometimes
        if (Math.random() < 0.2) {
            throw IOException("Network error")
        }

        // Simple simulation: messages with "safe" are considered safe
        return message.contains("safe", ignoreCase = true)
    }
}