package com.example.rockstar.data.preferences

import com.example.rockstar.viewmodel.LibrarySortOrder

data class AppPreferences(
    val themeMode: ThemeMode = ThemeMode.System,
    val defaultSortOrder: LibrarySortOrder = LibrarySortOrder.Title,
    val sortAscending: Boolean = true,
    val restoreQueue: Boolean = true
)
