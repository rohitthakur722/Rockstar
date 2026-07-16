package com.example.rockstar.repo

import com.example.rockstar.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class UserRepoImpl(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    database: FirebaseDatabase = FirebaseDatabase.getInstance()
) : UserRepo {

    private val usersRef = database.getReference("users")

    override fun currentUserId(): String? = auth.currentUser?.uid

    override fun currentUserEmail(): String? = auth.currentUser?.email

    override suspend fun register(email: String, password: String, fullName: String): Result<User> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = authResult.user?.uid
                ?: return Result.failure(IllegalStateException("Account created but no user id was returned"))

            val now = System.currentTimeMillis()
            val user = User(
                uid = uid,
                fullName = fullName,
                email = email,
                createdAt = now,
                updatedAt = now
            )

            try {
                usersRef.child(uid).setValue(user).await()
            } catch (profileError: Exception) {
                // Auth account exists but the profile write failed: roll back the
                // orphaned account so the user can safely retry registration.
                auth.currentUser?.delete()?.await()
                return Result.failure(profileError)
            }

            try {
                auth.currentUser
                    ?.updateProfile(UserProfileChangeRequest.Builder().setDisplayName(fullName).build())
                    ?.await()
            } catch (_: Exception) {
                // Non-fatal: the canonical profile lives in Realtime Database.
            }

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun login(email: String, password: String): Result<User> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val uid = authResult.user?.uid
                ?: return Result.failure(IllegalStateException("Login succeeded but no user id was returned"))

            fetchUserProfile(uid).recoverCatching {
                // Session is genuinely authenticated even if the profile record
                // is missing or unreadable; fall back to a minimal profile.
                User(uid = uid, email = auth.currentUser?.email ?: email)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun fetchUserProfile(uid: String): Result<User> {
        return try {
            val snapshot = usersRef.child(uid).get().await()
            val user = snapshot.getValue(User::class.java)
            if (user != null) {
                Result.success(user)
            } else {
                Result.failure(NoSuchElementException("Profile not found for user"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateUserProfile(uid: String, fullName: String): Result<User> {
        return try {
            val now = System.currentTimeMillis()
            val updates = mapOf(
                "fullName" to fullName,
                "updatedAt" to now
            )
            usersRef.child(uid).updateChildren(updates).await()

            try {
                auth.currentUser
                    ?.updateProfile(UserProfileChangeRequest.Builder().setDisplayName(fullName).build())
                    ?.await()
            } catch (_: Exception) {
                // Non-fatal: Realtime Database remains the source of truth.
            }

            fetchUserProfile(uid)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun logout() {
        auth.signOut()
    }
}
