package com.example.rockstar.viewmodel

import com.example.rockstar.model.User

data class AuthUiState(
    val isLoading: Boolean = false,
    val isSessionResolved: Boolean = false,
    val isAuthenticated: Boolean = false,
    val currentUser: User? = null
)

data class ProfileUiState(
    val isSaving: Boolean = false
)

sealed interface AuthEvent {
    data object NavigateToHome : AuthEvent
    data object NavigateToLogin : AuthEvent
    data class ShowMessage(val message: String) : AuthEvent
    data class ShowError(val message: String) : AuthEvent
}
