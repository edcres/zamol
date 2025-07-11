package com.example.zamol.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zamol.data.model.Message
import com.example.zamol.data.repo.ChatRepository
import com.example.zamol.data.repo.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        // Start listening for messages sent *to* this user
        authRepository.getCurrentUser()?.uid?.let { uid ->
            viewModelScope.launch {
                chatRepository.getMessagesForUser(uid)
                    .catch { e -> _error.value = e.message }
                    .collect { messageList ->
                        _messages.value = messageList
                    }
            }
        }
    }

    fun sendMessage(toUserId: String, content: String) {
        val sender = authRepository.getCurrentUser() ?: return

        val message = Message(
            senderId = sender.uid,
            receiverId = toUserId,
            content = content,
            timestamp = System.currentTimeMillis()
        )

        viewModelScope.launch {
            val result = chatRepository.sendMessage(message)
            if (result.isFailure) {
                _error.value = result.exceptionOrNull()?.message
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}
