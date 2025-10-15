package com.example.zamol.data.repo

import com.example.zamol.data.model.Message
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : ChatRepository {

    override fun getMessagesForRoom(chatRoomId: String): Flow<List<Message>> = callbackFlow {
        val messagesRef = firestore
            .collection("chatRooms")
            .document(chatRoomId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)

        val listener = messagesRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            val messages = snapshot?.documents?.mapNotNull { doc ->
                doc.toObject(Message::class.java)?.copy(id = doc.id)
            } ?: emptyList()

            trySend(messages).isSuccess
        }

        awaitClose { listener.remove() }
    }

    override suspend fun sendMessageToRoom(chatRoomId: String, message: Message): Result<Unit> = try {
        val messagesRef = firestore
            .collection("chatRooms")
            .document(chatRoomId)
            .collection("messages")

        messagesRef.add(message).await()

        // (Optional) Update chat room metadata like lastMessage/lastUpdated
        firestore.collection("chatRooms").document(chatRoomId).update(
            mapOf(
                "lastMessage" to message.content,
                "lastUpdated" to message.timestamp
            )
        ).await()

        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
