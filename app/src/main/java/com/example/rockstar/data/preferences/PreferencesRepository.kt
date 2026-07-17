package com.example.rockstar.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.rockstar.viewmodel.LibrarySortOrder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.rockstarDataStore by preferencesDataStore("rockstar_preferences")

class PreferencesRepository(private val context: Context) {
    val preferences: Flow<AppPreferences> = context.rockstarDataStore.data.map { prefs ->
        AppPreferences(
            themeMode = prefs[Keys.themeMode]?.let { runCatching { ThemeMode.valueOf(it) }.getOrNull() }
                ?: ThemeMode.System,
            defaultSortOrder = prefs[Keys.defaultSort]?.let { runCatching { LibrarySortOrder.valueOf(it) }.getOrNull() }
                ?: LibrarySortOrder.Title,
            sortAscending = prefs[Keys.sortAscending] ?: true,
            restoreQueue = prefs[Keys.restoreQueue] ?: true
        )
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.rockstarDataStore.edit { it[Keys.themeMode] = mode.name }
    }

    suspend fun setDefaultSort(sortOrder: LibrarySortOrder, ascending: Boolean) {
        context.rockstarDataStore.edit {
            it[Keys.defaultSort] = sortOrder.name
            it[Keys.sortAscending] = ascending
        }
    }

    suspend fun setRestoreQueue(enabled: Boolean) {
        context.rockstarDataStore.edit { it[Keys.restoreQueue] = enabled }
    }

    private object Keys {
        val themeMode = stringPreferencesKey("theme_mode")
        val defaultSort = stringPreferencesKey("default_sort")
        val sortAscending = booleanPreferencesKey("sort_ascending")
        val restoreQueue = booleanPreferencesKey("restore_queue")
    }
}
