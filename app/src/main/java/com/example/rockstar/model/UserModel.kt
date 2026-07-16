package com.example.rockstar.model

data class UserModel (
    val id: String = "",
    val username: String = "",
    val email: String = "",
    val address: String = "",
    val contact: String = "",
)
{
    fun toMap(): Map<String, Any> {
        return mapOf(
            "id" to id,
            "username" to username,
            "email" to email,
            "address" to address,
            "contact" to contact,
        )
    }
}

