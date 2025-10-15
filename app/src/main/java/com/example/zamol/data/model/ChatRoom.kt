package com.example.zamol.data.model

data class ChatRoom(
    val id: String = "",
    val participants: List<String> = emptyList(),
    val lastMessage: String = "",
    val lastUpdated: Long = 0L
)
