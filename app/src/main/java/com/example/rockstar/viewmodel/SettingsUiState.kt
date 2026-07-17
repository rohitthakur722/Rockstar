package com.example.rockstar.viewmodel

import com.example.rockstar.data.preferences.ThemeMode

data class SettingsUiState(
    val themeMode: ThemeMode = ThemeMode.System,
    val restoreQueue: Boolean = true,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)
