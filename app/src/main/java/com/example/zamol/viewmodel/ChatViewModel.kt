package com.example.zamol.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zamol.data.model.Message
import com.example.zamol.data.repo.ChatRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private var currentChatRoomId: String? = null

    fun startListeningTo(chatRoomId: String) {
        if (currentChatRoomId == chatRoomId) return
        currentChatRoomId = chatRoomId

        viewModelScope.launch {
            try {
                chatRepository.listenToMessages(chatRoomId).collect { msgs ->
                    _messages.value = msgs
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun sendMessage(content: String) {
        val chatRoomId = currentChatRoomId ?: return
        viewModelScope.launch {
            try {
                chatRepository.sendMessage(chatRoomId, content)
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun leaveCurrentGroup() {
        val chatRoomId = currentChatRoomId ?: return
        val userId = auth.currentUser?.uid ?: return

        viewModelScope.launch {
            try {
                chatRepository.leaveChatRoom(chatRoomId, userId)
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }


    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    fun clearError() {
        _error.value = null
    }
}
