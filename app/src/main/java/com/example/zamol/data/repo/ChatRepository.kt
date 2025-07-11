package com.example.zamol.data.repo

import com.example.zamol.data.model.Message
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun getMessagesForUser(receiverId: String): Flow<List<Message>>
    suspend fun sendMessage(message: Message): Result<Unit>
}
