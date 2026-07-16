package com.example.rockstar.model

data class User(
    val uid: String = "",
    val fullName: String = "",
    val email: String = "",
    val profileImageUrl: String = "",
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L
)
