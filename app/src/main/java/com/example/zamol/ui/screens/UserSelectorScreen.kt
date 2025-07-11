package com.example.zamol.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.zamol.viewmodel.UserListViewModel
import com.example.zamol.data.model.User

@Composable
fun UserSelectorScreen(
    viewModel: UserListViewModel = hiltViewModel(),
    onUserSelected: (User) -> Unit
) {
    val users by viewModel.users.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Select a user to chat with:", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(16.dp))

        users.forEach { user ->
            Text(
                text = user.displayName,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onUserSelected(user) }
                    .padding(12.dp)
            )
        }
    }
}
