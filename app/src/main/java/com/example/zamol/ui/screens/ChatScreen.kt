package com.example.zamol.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.zamol.ui.components.MessageBubble
import com.example.zamol.viewmodel.ChatViewModel

@Composable
fun ChatScreen(
    chatRoomId: String,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val messages by viewModel.messages.collectAsState()
    val error by viewModel.error.collectAsState()
    val currentUserId = viewModel.getCurrentUserId()
    var messageText by remember { mutableStateOf("") }

    LaunchedEffect(chatRoomId) {
        viewModel.startListeningTo(chatRoomId)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.weight(1f).padding(8.dp),
            reverseLayout = true
        ) {
            items(messages.reversed()) { message ->
                val isCurrentUser = message.senderId == currentUserId
                MessageBubble(message = message, isCurrentUser = isCurrentUser)
            }
        }

        Row(modifier = Modifier.padding(8.dp)) {
            TextField(
                value = messageText,
                onValueChange = { messageText = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Type a message") }
            )
            IconButton(onClick = {
                viewModel.sendMessage(messageText)
                messageText = ""
            }) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Send",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }

    error?.let {
        Toast.makeText(LocalContext.current, it, Toast.LENGTH_SHORT).show()
        viewModel.clearError()
    }
}
