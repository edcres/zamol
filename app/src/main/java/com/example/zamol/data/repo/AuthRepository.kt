package com.example.zamol.data.repo
import com.example.zamol.data.model.User

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<Unit>
    suspend fun signup(email: String, password: String, displayName: String): Result<Unit>
    fun logout()
    fun getCurrentUser(): User?
}