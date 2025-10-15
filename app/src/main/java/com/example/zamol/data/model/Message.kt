package com.example.zamol.data.model

import com.google.firebase.Timestamp

data class Message(
    val senderId: String = "",
    val content: String = "",
    val timestamp: Timestamp = Timestamp.now()
)
