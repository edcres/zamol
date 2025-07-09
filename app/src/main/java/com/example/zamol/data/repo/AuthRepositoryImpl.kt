package com.example.zamol.data.repo

import com.example.zamol.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<Unit> = try {
        firebaseAuth.signInWithEmailAndPassword(email, password).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun signup(email: String, password: String, displayName: String): Result<Unit> = try {
        firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        firebaseAuth.currentUser?.updateProfile(UserProfileChangeRequest.Builder().setDisplayName(displayName).build())?.await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override fun logout() {
        firebaseAuth.signOut()
    }

    override fun getCurrentUser(): User? {
        return firebaseAuth.currentUser?.let {
            User(it.uid, it.displayName ?: "", it.email ?: "")
        }
    }
}
