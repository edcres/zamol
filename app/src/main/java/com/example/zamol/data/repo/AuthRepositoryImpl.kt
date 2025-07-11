package com.example.zamol.data.repo

import com.example.zamol.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {

    private val usersCollection = FirebaseFirestore.getInstance().collection("users")

    override suspend fun login(email: String, password: String): Result<Unit> = try {
        firebaseAuth.signInWithEmailAndPassword(email, password).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun signup(email: String, password: String, displayName: String): Result<Unit> = try {
        firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        val user = firebaseAuth.currentUser

        user?.updateProfile(UserProfileChangeRequest.Builder().setDisplayName(displayName).build())?.await()

        // ✅ Save to users collection
        val userData = hashMapOf(
            "uid" to user?.uid,
            "email" to user?.email,
            "displayName" to displayName
        )
        user?.uid?.let { usersCollection.document(it).set(userData).await() }

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
