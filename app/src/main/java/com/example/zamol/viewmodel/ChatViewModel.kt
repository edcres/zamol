package com.example.zamol.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zamol.data.model.Message
import com.example.zamol.data.model.User
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

    fun startListeningTo(chatRoomId: String) {
        viewModelScope.launch {
            chatRepository.getMessagesForRoom(chatRoomId)
                .catch { e -> _error.value = e.message }
                .collect { _messages.value = it }
        }
    }

    fun sendMessage(chatRoomId: String, content: String) {
        val sender = authRepository.getCurrentUser() ?: User(
            uid = "FAKE_UID_001",
            displayName = "Dev Tester",
            email = "dev@example.com"
        )

        val message = Message(
            senderId = sender.uid,
            receiverId = "", // Optional now, since the room groups messages
            content = content,
            timestamp = System.currentTimeMillis()
        )

        viewModelScope.launch {
            val result = chatRepository.sendMessageToRoom(chatRoomId, message)
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


//package com.example.zamol.viewmodel
//
//import android.util.Log
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.example.zamol.data.model.Message
//import com.example.zamol.data.model.User
//import com.example.zamol.data.repo.AuthRepository
//import com.example.zamol.data.repo.ChatRepository
//import dagger.hilt.android.lifecycle.HiltViewModel
//import kotlinx.coroutines.flow.*
//import kotlinx.coroutines.launch
//import javax.inject.Inject
//
//@HiltViewModel
//class ChatViewModel @Inject constructor(
//    private val chatRepository: ChatRepository,
//    private val authRepository: AuthRepository
//) : ViewModel() {
//
//    private final val TAG = "CHAT_VM_TAG"
//
//    private val _messages = MutableStateFlow<List<Message>>(emptyList())
//    val messages: StateFlow<List<Message>> = _messages.asStateFlow()
//
//    private val _error = MutableStateFlow<String?>(null)
//    val error: StateFlow<String?> = _error.asStateFlow()
//
//    fun startListeningTo(receiverId: String) {
//        val currentUserId = getCurrentUserId() ?: return
//
//        viewModelScope.launch {
//            chatRepository.getMessagesForUser(currentUserId)
//                .catch { e -> _error.value = e.message }
//                .collect { messageList ->
//                    // Show messages where the sender OR receiver is the other user
//                    val filtered = messageList.filter {
//                        (it.senderId == currentUserId && it.receiverId == receiverId) ||
//                                (it.senderId == receiverId && it.receiverId == currentUserId)
//                    }
//                    _messages.value = filtered
//                }
//        }
//    }
//
//    fun sendMessage(toUserId: String, content: String) {
//        Log.d(TAG, "sendMessage: called1 ____________")
//        // TODO: replace with: `val sender = authRepository.getCurrentUser() ?: return`
//        val sender = authRepository.getCurrentUser() ?: User(
//            uid = "FAKE_UID_001",
//            displayName = "Dev Tester",
//            email = "dev@example.com"
//        )
//
//        Log.d(TAG, "sendMessage: called2 -\n${sender.uid}-\n${toUserId}-\n${toUserId}-\n${System.currentTimeMillis()}____________")
//
//        val message = Message(
//            senderId = sender.uid,
//            receiverId = toUserId,
//            content = content,
//            timestamp = System.currentTimeMillis()
//        )
//
//        Log.d(TAG, "sendMessage: called3 ____________")
//
//        viewModelScope.launch {
//            val result = chatRepository.sendMessage(message)
//            if (result.isFailure) {
//                _error.value = result.exceptionOrNull()?.message
//            }
//        }
//        Log.d(TAG, "sendMessage: called4 ____________")
//    }
//
//    fun getCurrentUserId(): String? {
//        return authRepository.getCurrentUser()?.uid
//    }
//
//    fun clearError() {
//        _error.value = null
//    }
//}
