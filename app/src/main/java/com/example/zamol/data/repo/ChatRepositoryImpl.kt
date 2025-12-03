package com.example.zamol.data.repo

import com.example.zamol.data.model.ChatRoom
import com.example.zamol.data.model.Message
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ChatRepository {

    override suspend fun createChatRoom(participants: List<String>, name: String?): String {
        val data = mutableMapOf<String, Any>(
            "participants" to participants,
            "createdAt" to FieldValue.serverTimestamp()
        )
        name?.let { data["name"] = it }

        val docRef = firestore.collection("chatRooms").add(data).await()
        return docRef.id
    }

    override suspend fun sendMessage(chatRoomId: String, content: String) {
        val sender = auth.currentUser
        val senderId = sender?.uid ?: "debug-fake-user"

        val message = Message(
            senderId = senderId,
            content = content,
            timestamp = Timestamp.now()
        )

        firestore.collection("chatRooms")
            .document(chatRoomId)
            .collection("messages")
            .add(message)
            .await()
    }

    override fun listenToMessages(chatRoomId: String): Flow<List<Message>> = callbackFlow {
        val listenerRegistration = firestore.collection("chatRooms")
            .document(chatRoomId)
            .collection("messages")
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                val messages = snapshot.documents.mapNotNull { it.toObject(Message::class.java) }
                trySend(messages)
            }

        awaitClose {
            listenerRegistration.remove()
        }
    }

    override suspend fun getChatRoomsForUser(userId: String): List<ChatRoom> {
        return try {
            val snapshot = firestore.collection("chatRooms")
                .whereArrayContains("participants", userId)
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                doc.toObject(ChatRoom::class.java)?.copy(id = doc.id)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun leaveChatRoom(chatRoomId: String, userId: String) {
        firestore.collection("chatRooms")
            .document(chatRoomId)
            .update("participants", FieldValue.arrayRemove(userId))
            .await()
    }

}
