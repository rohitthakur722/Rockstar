package com.example.rockstar.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rockstar.model.User
import com.example.rockstar.repo.UserRepo
import com.example.rockstar.util.mapAuthError
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UserViewModel(private val repo: UserRepo) : ViewModel() {

    private val _authState = MutableStateFlow(AuthUiState())
    val authState: StateFlow<AuthUiState> = _authState.asStateFlow()

    private val _profileState = MutableStateFlow(ProfileUiState())
    val profileState: StateFlow<ProfileUiState> = _profileState.asStateFlow()

    private val _events = Channel<AuthEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        restoreSession()
    }

    fun restoreSession() {
        val uid = repo.currentUserId()
        if (uid == null) {
            _authState.value = AuthUiState(isSessionResolved = true, isAuthenticated = false)
            return
        }

        viewModelScope.launch {
            repo.fetchUserProfile(uid)
                .onSuccess { user ->
                    _authState.value = AuthUiState(
                        isSessionResolved = true,
                        isAuthenticated = true,
                        currentUser = user
                    )
                }
                .onFailure {
                    // The Firebase session itself is valid even if the profile
                    // record could not be read; fall back to a minimal profile
                    // rather than forcing the user back through Login.
                    _authState.value = AuthUiState(
                        isSessionResolved = true,
                        isAuthenticated = true,
                        currentUser = User(
                            uid = uid,
                            email = repo.currentUserEmail() ?: ""
                        )
                    )
                }
        }
    }

    fun login(email: String, password: String) {
        if (_authState.value.isLoading) return
        _authState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            repo.login(email.trim(), password)
                .onSuccess { user ->
                    _authState.update {
                        it.copy(
                            isLoading = false,
                            isSessionResolved = true,
                            isAuthenticated = true,
                            currentUser = user
                        )
                    }
                    _events.send(AuthEvent.NavigateToHome)
                }
                .onFailure { error ->
                    _authState.update { it.copy(isLoading = false) }
                    _events.send(AuthEvent.ShowError(mapAuthError(error)))
                }
        }
    }

    fun register(fullName: String, email: String, password: String) {
        if (_authState.value.isLoading) return
        _authState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            repo.register(email.trim(), password, fullName.trim())
                .onSuccess { user ->
                    _authState.update {
                        it.copy(
                            isLoading = false,
                            isSessionResolved = true,
                            isAuthenticated = true,
                            currentUser = user
                        )
                    }
                    _events.send(AuthEvent.NavigateToHome)
                }
                .onFailure { error ->
                    _authState.update { it.copy(isLoading = false) }
                    _events.send(AuthEvent.ShowError(mapAuthError(error)))
                }
        }
    }

    fun sendPasswordResetEmail(email: String) {
        if (_authState.value.isLoading) return
        _authState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            repo.sendPasswordResetEmail(email.trim())
                .onSuccess {
                    _authState.update { it.copy(isLoading = false) }
                    _events.send(AuthEvent.ShowMessage("Password reset email sent. Check your inbox."))
                }
                .onFailure { error ->
                    _authState.update { it.copy(isLoading = false) }
                    _events.send(AuthEvent.ShowError(mapAuthError(error)))
                }
        }
    }

    fun updateProfile(fullName: String) {
        val uid = _authState.value.currentUser?.uid ?: return
        if (_profileState.value.isSaving) return
        _profileState.update { it.copy(isSaving = true) }

        viewModelScope.launch {
            repo.updateUserProfile(uid, fullName.trim())
                .onSuccess { user ->
                    _profileState.update { it.copy(isSaving = false) }
                    _authState.update { it.copy(currentUser = user) }
                    _events.send(AuthEvent.ShowMessage("Profile updated"))
                }
                .onFailure { error ->
                    _profileState.update { it.copy(isSaving = false) }
                    _events.send(AuthEvent.ShowError(mapAuthError(error)))
                }
        }
    }

    fun logout() {
        repo.logout()
        _authState.value = AuthUiState(isSessionResolved = true, isAuthenticated = false)
        _profileState.value = ProfileUiState()
        viewModelScope.launch { _events.send(AuthEvent.NavigateToLogin) }
    }
}
