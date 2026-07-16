package com.example.rockstar.viewmodel

import androidx.lifecycle.ViewModel
import com.example.rockstar.model.UserModel
import com.example.rockstar.repo.UserRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserViewModel(private val repo: UserRepo) : ViewModel() {

    private val _authState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val authState: StateFlow<AuthUiState> = _authState.asStateFlow()

    fun login(email: String, password: String) {
        _authState.value = AuthUiState.Loading
        repo.login(email, password) { success, message ->
            _authState.value = if (success) {
                AuthUiState.Success(message ?: "Login successful")
            } else {
                AuthUiState.Error(message ?: "Login failed")
            }
        }
    }

    fun register(username: String, email: String, password: String) {
        _authState.value = AuthUiState.Loading
        repo.register(email, password) { success, message, uid ->
            if (success && uid.isNotEmpty()) {
                val newUser = UserModel(id = uid, username = username, email = email)
                repo.addUser(uid, newUser) { addSuccess, addMessage ->
                    _authState.value = if (addSuccess) {
                        AuthUiState.Success("Account created successfully")
                    } else {
                        AuthUiState.Error(addMessage)
                    }
                }
            } else {
                _authState.value = AuthUiState.Error(message)
            }
        }
    }

    fun forgotPassword(email: String) {
        _authState.value = AuthUiState.Loading
        repo.forgetPassword(email) { success, message ->
            _authState.value = if (success) {
                AuthUiState.Success(message ?: "Reset link sent")
            } else {
                AuthUiState.Error(message ?: "Unable to send reset link")
            }
        }
    }

    fun logout(onComplete: () -> Unit) {
        repo.logout { _, _ ->
            _authState.value = AuthUiState.Idle
            onComplete()
        }
    }

    fun resetAuthState() {
        _authState.value = AuthUiState.Idle
    }
}
