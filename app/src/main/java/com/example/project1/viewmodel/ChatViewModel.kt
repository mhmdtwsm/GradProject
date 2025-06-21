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

    private val _messageInput = MutableStateFlow("")
    val messageInput = _messageInput.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    // Store chat history
    private val _chatHistory = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatHistory = _chatHistory.asStateFlow()

    fun onMessageInputChange(input: String) {
        _messageInput.value = input
    }

    fun sendMessage() {
        val message = _messageInput.value.trim()
        if (message.isEmpty() || _isLoading.value) return

        _isLoading.value = true

        // Add user message to chat history
        val userMessage = ChatMessage("user", message)
        val currentHistory = _chatHistory.value.toMutableList()
        currentHistory.add(userMessage)
        _chatHistory.value = currentHistory

        // Update UI state to show the conversation
        _uiState.value = ChatUiState.Conversation(_chatHistory.value)

        viewModelScope.launch {
            // Send message to API
            when (val result =
                chatApiService.sendMessage(message, _chatHistory.value.dropLast(1))) {
                is ChatApiService.ChatResult.Success -> {
                    // Add assistant response to chat history
                    val assistantMessage = ChatMessage("assistant", result.response)
                    val updatedHistory = _chatHistory.value.toMutableList()
                    updatedHistory.add(assistantMessage)
                    _chatHistory.value = updatedHistory

                    // Update UI state
                    _uiState.value = ChatUiState.Conversation(_chatHistory.value)
                }

                is ChatApiService.ChatResult.Error -> {
                    // Add error message to chat history
                    val errorMessage = ChatMessage("assistant", "Error: ${result.message}")
                    val updatedHistory = _chatHistory.value.toMutableList()
                    updatedHistory.add(errorMessage)
                    _chatHistory.value = updatedHistory

                    // Update UI state
                    _uiState.value = ChatUiState.Conversation(_chatHistory.value)
                }
            }
            _isLoading.value = false
            _messageInput.value = ""
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
    data class Conversation(val messages: List<ChatMessage>) : ChatUiState()
}
