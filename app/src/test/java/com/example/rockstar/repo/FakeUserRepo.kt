package com.example.rockstar.repo

import com.example.rockstar.model.User

/** Deterministic [UserRepo] test double — no network or Firebase involved. */
class FakeUserRepo : UserRepo {

    var uid: String? = null
    var email: String? = null

    var registerResult: Result<User> = Result.failure(IllegalStateException("registerResult not configured"))
    var loginResult: Result<User> = Result.failure(IllegalStateException("loginResult not configured"))
    var fetchProfileResult: Result<User> = Result.failure(IllegalStateException("fetchProfileResult not configured"))
    var updateProfileResult: Result<User> = Result.failure(IllegalStateException("updateProfileResult not configured"))
    var resetPasswordResult: Result<Unit> = Result.success(Unit)

    var registerCallCount = 0
        private set
    var loginCallCount = 0
        private set
    var logoutCalled = false
        private set

    override fun currentUserId(): String? = uid

    override fun currentUserEmail(): String? = email

    override suspend fun register(email: String, password: String, fullName: String): Result<User> {
        registerCallCount++
        return registerResult
    }

    override suspend fun login(email: String, password: String): Result<User> {
        loginCallCount++
        return loginResult
    }

    override suspend fun fetchUserProfile(uid: String): Result<User> = fetchProfileResult

    override suspend fun updateUserProfile(uid: String, fullName: String): Result<User> = updateProfileResult

    override suspend fun sendPasswordResetEmail(email: String): Result<Unit> = resetPasswordResult

    override fun logout() {
        logoutCalled = true
    }
}
