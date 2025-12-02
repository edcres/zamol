package com.example.zamol.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.zamol.data.model.ChatRoom
import com.example.zamol.data.model.User
import com.example.zamol.viewmodel.ChatRoomsViewModel
import com.example.zamol.viewmodel.UserListViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun UserSelectorScreen(
    onUserSelected: (User) -> Unit,
    onGroupSelected: (ChatRoom) -> Unit
) {
    val userViewModel: UserListViewModel = hiltViewModel()
    val roomsViewModel: ChatRoomsViewModel = hiltViewModel()

    val users by userViewModel.users.collectAsStateWithLifecycle()
    val rooms by roomsViewModel.rooms.collectAsStateWithLifecycle()

    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    // 1-to-1: hide current user
    val filteredUsers = if (currentUserId == null) {
        users
    } else {
        users.filter { it.uid != currentUserId }
    }

    // Groups: only rooms with 3+ participants
    val groupRooms = rooms.filter { it.participants.size > 2 }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 🔹 Groups section
        Text(
            text = "Groups",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (groupRooms.isEmpty()) {
            Text(
                text = "No groups yet",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 200.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(groupRooms) { room ->
                    GroupRow(room = room) { onGroupSelected(room) }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Divider()
        Spacer(modifier = Modifier.height(16.dp))

        // 🔹 1-to-1 section
        Text(
            text = "1-to-1 chats",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredUsers) { user ->
                UserRow(user = user) { onUserSelected(user) }
            }
        }
    }
}

@Composable
private fun GroupRow(
    room: ChatRoom,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = room.name ?: "Group (${room.participants.size} members)",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
private fun UserRow(
    user: User,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = user.displayName.ifBlank { user.email },
                    style = MaterialTheme.typography.bodyLarge
                )
                if (user.email.isNotBlank()) {
                    Text(
                        text = user.email,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
