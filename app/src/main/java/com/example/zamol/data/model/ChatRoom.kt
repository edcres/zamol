package com.example.zamol.data.model

import com.google.firebase.Timestamp

data class ChatRoom(
    val id: String = "",
    val name: String? = null,
    val participants: List<String> = emptyList(),
    val createdAt: Timestamp = Timestamp.now()
)
