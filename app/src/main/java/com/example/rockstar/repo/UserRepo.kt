package com.example.rockstar.repo

import com.example.rockstar.model.User

interface UserRepo {

    fun currentUserId(): String?

    fun currentUserEmail(): String?

    suspend fun register(email: String, password: String, fullName: String): Result<User>

    suspend fun login(email: String, password: String): Result<User>

    suspend fun fetchUserProfile(uid: String): Result<User>

    suspend fun updateUserProfile(uid: String, fullName: String): Result<User>

    suspend fun sendPasswordResetEmail(email: String): Result<Unit>

    fun logout()
}
