package com.example.rockstar.util

private val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
private const val MIN_FULL_NAME_LENGTH = 2
private const val MIN_PASSWORD_LENGTH = 6

object Validators {

    fun isValidFullName(fullName: String): Boolean =
        fullName.trim().length >= MIN_FULL_NAME_LENGTH

    fun isValidEmail(email: String): Boolean =
        EMAIL_REGEX.matches(email.trim())

    fun isValidPassword(password: String): Boolean =
        password.length >= MIN_PASSWORD_LENGTH

    fun doPasswordsMatch(password: String, confirmPassword: String): Boolean =
        password == confirmPassword
}
