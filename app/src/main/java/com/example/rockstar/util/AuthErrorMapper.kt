package com.example.rockstar.util

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.database.DatabaseException

/**
 * Translates raw Firebase exceptions into short, user-facing messages.
 * Never surface [Throwable.message]/stack traces directly to the UI.
 */
fun mapAuthError(error: Throwable): String = when (error) {
    is FirebaseAuthWeakPasswordException ->
        "Password is too weak. Use at least 6 characters."
    is FirebaseAuthInvalidCredentialsException ->
        "The email or password is invalid."
    is FirebaseAuthUserCollisionException ->
        "An account already exists with this email."
    is FirebaseAuthInvalidUserException ->
        if (error.errorCode == "ERROR_USER_DISABLED") {
            "This account has been disabled. Contact support for help."
        } else {
            "No account found with this email."
        }
    is FirebaseTooManyRequestsException ->
        "Too many attempts. Please wait a moment and try again."
    is FirebaseNetworkException ->
        "No network connection. Check your internet and try again."
    is DatabaseException ->
        "Unable to reach the profile database. Please try again."
    is NoSuchElementException ->
        "Your profile could not be found."
    else -> "Something went wrong. Please try again."
}
