package com.example.zamol.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zamol.data.model.Message
import com.example.zamol.data.repo.AuthRepository
import com.example.zamol.data.repo.ChatRepository
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

    fun startListeningTo(receiverId: String) {
        val currentUserId = getCurrentUserId() ?: return

        viewModelScope.launch {
            chatRepository.getMessagesForUser(currentUserId)
                .catch { e -> _error.value = e.message }
                .collect { messageList ->
                    // Show messages where the sender OR receiver is the other user
                    val filtered = messageList.filter {
                        (it.senderId == currentUserId && it.receiverId == receiverId) ||
                                (it.senderId == receiverId && it.receiverId == currentUserId)
                    }
                    _messages.value = filtered
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

    fun getCurrentUserId(): String? {
        return authRepository.getCurrentUser()?.uid
    }

    fun clearError() {
        _error.value = null
    }
}
