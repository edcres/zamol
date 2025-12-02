package com.example.zamol.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zamol.data.model.ChatRoom
import com.example.zamol.data.repo.ChatRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatRoomsViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _rooms = MutableStateFlow<List<ChatRoom>>(emptyList())
    val rooms: StateFlow<List<ChatRoom>> = _rooms

    init {
        loadRooms()
    }

    private fun loadRooms() {
        val currentUserId = auth.currentUser?.uid ?: return

        viewModelScope.launch {
            val chatRooms = chatRepository.getChatRoomsForUser(currentUserId)
            _rooms.value = chatRooms
        }
    }
}
