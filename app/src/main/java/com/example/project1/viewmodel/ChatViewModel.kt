package com.example.project1.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatViewModel(
    private val chatApiService: ChatApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow<ChatUiState>(ChatUiState.Empty)
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private val _questionInput = MutableStateFlow("")
    val questionInput = _questionInput.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun onQuestionInputChange(input: String) {
        _questionInput.value = input
    }

    fun sendQuestion() {
        val question = _questionInput.value.trim()
        if (question.isEmpty()) return

        _isLoading.value = true

        viewModelScope.launch {
            when (val result = chatApiService.sendQuestion(question)) {
                is ChatApiService.ChatResult.Success -> {
                    _uiState.value = ChatUiState.Conversation(
                        question = question,
                        answer = result.response
                    )
                }

                is ChatApiService.ChatResult.Error -> {
                    _uiState.value = ChatUiState.Conversation(
                        question = question,
                        answer = "Error: ${result.message}"
                    )
                }
            }
            _isLoading.value = false
            _questionInput.value = ""
        }
    }

    // ViewModel Factory to create the ViewModel with dependencies
    class Factory : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
                val chatApiService = ChatApiService()
                @Suppress("UNCHECKED_CAST")
                return ChatViewModel(chatApiService) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

sealed class ChatUiState {
    object Empty : ChatUiState()
    data class Conversation(val question: String, val answer: String) : ChatUiState()
}