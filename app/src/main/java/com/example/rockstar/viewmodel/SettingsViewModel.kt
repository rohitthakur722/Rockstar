package com.example.rockstar.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rockstar.data.preferences.PreferencesRepository
import com.example.rockstar.data.preferences.ThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            preferencesRepository.preferences.collectLatest { preferences ->
                _uiState.value = SettingsUiState(
                    themeMode = preferences.themeMode,
                    restoreQueue = preferences.restoreQueue,
                    isLoading = false
                )
            }
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            runCatching { preferencesRepository.setThemeMode(mode) }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            errorMessage = error.message ?: "Could not save theme preference."
                        )
                    }
                }
        }
    }

    fun setRestoreQueue(enabled: Boolean) {
        viewModelScope.launch {
            runCatching { preferencesRepository.setRestoreQueue(enabled) }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            errorMessage = error.message ?: "Could not save playback preference."
                        )
                    }
                }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
