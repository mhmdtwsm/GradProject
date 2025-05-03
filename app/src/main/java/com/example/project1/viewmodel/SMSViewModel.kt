package com.example.project1.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.project1.SMS.SMSData.SMSHistoryItem
import com.example.project1.SMS.SMSData.SMSRepository
import com.example.project1.SMS.SMSNetworkRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SMSViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = SMSRepository(application)
    private val networkRepository = SMSNetworkRepository()

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

            networkRepository.checkSMSSafety(message).fold(
                onSuccess = { isSafe ->
                    repository.saveSMS(message, isSafe)
                    Log.d("SMSViewModel", "SMS check result: ${if (isSafe) "Safe" else "Unsafe"}")
                },
                onFailure = { error ->
                    repository.saveSMS(message, null)
                    val errorMsg = error.message ?: "Unknown network error"
                    Log.e("SMSViewModel", "Failed to check SMS: $errorMsg")
                    // Optionally show error to user
                }
            )

            // Reload messages after saving
            loadRecentSMS()

            _isLoading.value = false
        }
    }

    fun pasteFromClipboard(text: String) {
        _smsText.value = text
    }
}