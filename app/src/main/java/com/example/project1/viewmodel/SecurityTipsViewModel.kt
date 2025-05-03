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
import kotlinx.coroutines.launch

class SecurityTipsViewModel : ViewModel() {
    // Create an instance of the API service
    private val apiService = SecurityTipsApiService()

    // UI state
    var securityTip by mutableStateOf<SecurityTip?>(null)
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf("")

    // Fetch security tip from API
    fun fetchSecurityTip(context: Context) {
        isLoading = true
        errorMessage = ""

        viewModelScope.launch {
            try {
                // Call the getSecurityTip method from the API service
                val result = apiService.getSecurityTip(context)

                when (result) {
                    is SecurityTipResult.Success -> {
                        securityTip = result.tip
                        isLoading = false
                        errorMessage = ""
                    }

                    is SecurityTipResult.Error -> {
                        errorMessage = result.message
                        isLoading = false
                    }
                    // Remove this case since Loading is not part of your sealed class
                    // SecurityTipResult.Loading -> {
                    //     isLoading = true
                    // }
                }
            } catch (e: Exception) {
                errorMessage = "Unexpected error: ${e.message}"
                isLoading = false
            }
        }
    }

    // Refresh security tip
    fun refreshSecurityTip(context: Context) {
        fetchSecurityTip(context)
    }
}
