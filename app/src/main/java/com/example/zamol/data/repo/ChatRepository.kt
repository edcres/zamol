package com.example.zamol.data.repo

import com.example.zamol.data.model.Message
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    suspend fun createChatRoom(participants: List<String>, name: String? = null): String

    suspend fun sendMessage(chatRoomId: String, content: String)

    fun listenToMessages(chatRoomId: String): Flow<List<Message>>
}
