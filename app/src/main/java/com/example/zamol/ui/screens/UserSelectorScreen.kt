package com.example.zamol.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.zamol.data.model.User
import com.example.zamol.viewmodel.UserListViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun UserSelectorScreen(
    onUserSelected: (User) -> Unit,
    viewModel: UserListViewModel = hiltViewModel()
) {
    val users by viewModel.users.collectAsStateWithLifecycle()

    // Current logged-in user ID
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    // Filter out the current user from the list
    val filteredUsers = if (currentUserId == null) {
        users
    } else {
        users.filter { it.uid != currentUserId }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Start a chat",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 🔹 In the future, you can add a "Groups" section above or below this.

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredUsers) { user ->
                UserRow(user = user, onClick = { onUserSelected(user) })
            }
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
