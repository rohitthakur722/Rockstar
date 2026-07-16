package com.example.rockstar.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rockstar.model.Song
import com.example.rockstar.repo.MusicRepository
import com.example.rockstar.util.buildAlbums
import com.example.rockstar.util.buildArtists
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MusicLibraryViewModel(private val repository: MusicRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(MusicLibraryUiState())
    val uiState: StateFlow<MusicLibraryUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null

    fun onPermissionStateChanged(permissionState: AudioPermissionState) {
        _uiState.update { it.copy(permissionState = permissionState) }
        if (permissionState == AudioPermissionState.Granted) {
            loadLibraryIfNeeded()
        }
    }

    fun loadLibraryIfNeeded() {
        val state = _uiState.value
        if (state.permissionState != AudioPermissionState.Granted || state.hasLoaded) return
        loadLibrary(refresh = false)
    }

    fun refreshLibrary() {
        if (_uiState.value.permissionState != AudioPermissionState.Granted) return
        loadLibrary(refresh = true)
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { state -> state.copy(searchQuery = query).recalculate() }
    }

    fun selectSection(section: LibrarySection) {
        _uiState.update { it.copy(selectedLibrarySection = section) }
    }

    fun updateSortOrder(sortOrder: LibrarySortOrder) {
        _uiState.update { state ->
            val nextAscending =
                if (state.selectedSortOrder == sortOrder) !state.isSortAscending else defaultAscending(sortOrder)
            state.copy(selectedSortOrder = sortOrder, isSortAscending = nextAscending).recalculate()
        }
    }

    fun toggleSortDirection() {
        _uiState.update { state -> state.copy(isSortAscending = !state.isSortAscending).recalculate() }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    private fun loadLibrary(refresh: Boolean) {
        if (loadJob?.isActive == true) return
        loadJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = if (refresh) repository.refreshSongs() else repository.loadSongs()
            result
                .onSuccess { songs ->
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            songs = songs,
                            hasLoaded = true,
                            lastRefreshTime = System.currentTimeMillis(),
                            errorMessage = null
                        ).recalculate()
                    }
                }
                .onFailure {
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            hasLoaded = true,
                            errorMessage = "We could not scan your local music library. Please try again."
                        ).recalculate()
                    }
                }
        }
    }

    private fun MusicLibraryUiState.recalculate(): MusicLibraryUiState {
        val sorted = songs.filtered(searchQuery).sortedBy(selectedSortOrder, isSortAscending)
        return copy(
            displayedSongs = sorted,
            albums = buildAlbums(sorted),
            artists = buildArtists(sorted)
        )
    }

    private fun List<Song>.filtered(query: String): List<Song> {
        val normalized = query.trim()
        if (normalized.isBlank()) return this
        return filter { song ->
            song.title.contains(normalized, ignoreCase = true) ||
                    song.artist.contains(normalized, ignoreCase = true) ||
                    song.album.contains(normalized, ignoreCase = true)
        }
    }

    private fun List<Song>.sortedBy(sortOrder: LibrarySortOrder, ascending: Boolean): List<Song> {
        val comparator = when (sortOrder) {
            LibrarySortOrder.Title -> compareBy(String.CASE_INSENSITIVE_ORDER, Song::title).thenBy { it.id }
            LibrarySortOrder.Artist -> compareBy(String.CASE_INSENSITIVE_ORDER, Song::artist)
                .thenBy(String.CASE_INSENSITIVE_ORDER, Song::title)
                .thenBy { it.id }

            LibrarySortOrder.Album -> compareBy(String.CASE_INSENSITIVE_ORDER, Song::album)
                .thenBy { it.trackNumber ?: Int.MAX_VALUE }
                .thenBy(String.CASE_INSENSITIVE_ORDER, Song::title)
                .thenBy { it.id }

            LibrarySortOrder.DateAdded -> compareBy<Song> { it.dateAdded }.thenBy(
                String.CASE_INSENSITIVE_ORDER,
                Song::title
            ).thenBy { it.id }

            LibrarySortOrder.Duration -> compareBy<Song> { it.durationMs }.thenBy(
                String.CASE_INSENSITIVE_ORDER,
                Song::title
            ).thenBy { it.id }

            LibrarySortOrder.Year -> compareBy<Song> { it.year ?: Int.MAX_VALUE }.thenBy(
                String.CASE_INSENSITIVE_ORDER,
                Song::title
            ).thenBy { it.id }
        }
        val sorted = sortedWith(comparator)
        return if (ascending) sorted else sorted.asReversed()
    }

    private fun defaultAscending(sortOrder: LibrarySortOrder): Boolean = when (sortOrder) {
        LibrarySortOrder.DateAdded -> false
        else -> true
    }
}
