package com.example.zamol.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zamol.data.model.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class UserListViewModel @Inject constructor() : ViewModel() {
    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users

    init {
        fetchUsers()
    }

    private fun fetchUsers() {
        FirebaseFirestore.getInstance().collection("users")
            .addSnapshotListener { snapshot, _ ->
                val userList = snapshot?.documents?.mapNotNull {
                    it.toObject(User::class.java)
                } ?: emptyList()
                _users.value = userList
            }
    }
}
