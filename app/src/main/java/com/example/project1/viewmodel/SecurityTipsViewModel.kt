package com.example.project1.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project1.tools.tips.SecurityTip
import com.example.project1.tools.tips.SecurityTipResult
import com.example.project1.tools.tips.SecurityTipsApiService
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class SecurityTipsViewModel : ViewModel() {
    // Create an instance of the API service
    private val apiService = SecurityTipsApiService()

    // UI state
    var securityTips by mutableStateOf<List<SecurityTip>>(emptyList())
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf("")
    var currentTipIndex by mutableStateOf(0)
    var isNearEnd by mutableStateOf(false)

    // Event to notify when new tips are loaded and we need to reset the pager
    private val _resetPagerEvent = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val resetPagerEvent = _resetPagerEvent.asSharedFlow()

    // Constants
    private val LOAD_THRESHOLD = 5 // Load more when reaching the 4th tip (out of 5)
    private val BATCH_SIZE = 5 // Expected number of tips per batch

    // Fetch security tips from API
    fun fetchSecurityTips(context: Context) {
        if (isLoading) return

        isLoading = true
        errorMessage = ""

        viewModelScope.launch {
            try {
                // Call the getSecurityTips method from the API service
                val result = apiService.getSecurityTips(context)

                when (result) {
                    is SecurityTipResult.Success -> {
                        // Update with the new tips
                        securityTips = result.tips
                        isLoading = false
                        errorMessage = ""
                        // Reset index when loading new batch
                        currentTipIndex = 0
                        isNearEnd = false

                        // Emit event to reset pager position
                        _resetPagerEvent.emit(Unit)
                    }

                    is SecurityTipResult.Error -> {
                        errorMessage = result.message
                        isLoading = false
                    }
                }
            } catch (e: Exception) {
                errorMessage = "Unexpected error: ${e.message}"
                isLoading = false
            }
        }
    }

    // Load more tips when approaching the end
    fun loadMoreIfNeeded(context: Context) {
        if (currentTipIndex >= LOAD_THRESHOLD && !isLoading && !isNearEnd) {
            isNearEnd = true
            fetchSecurityTips(context)
        }
    }

    // Update the current index when scrolling
    fun updateCurrentTipIndex(index: Int) {
        currentTipIndex = index
    }

    // Clear all tips when leaving the screen
    fun clearTips() {
        securityTips = emptyList()
        currentTipIndex = 0
        isNearEnd = false
    }
}