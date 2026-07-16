package com.example.rockstar.viewmodel

import com.example.rockstar.model.Album
import com.example.rockstar.model.Artist
import com.example.rockstar.model.Song

enum class AudioPermissionState {
    NotRequested,
    Granted,
    Denied,
    DeniedWithRationale,
    PermanentlyDenied
}

enum class LibrarySection {
    Songs,
    Albums,
    Artists
}

enum class LibrarySortOrder {
    Title,
    Artist,
    Album,
    DateAdded,
    Duration,
    Year
}

data class MusicLibraryUiState(
    val isLoading: Boolean = false,
    val permissionState: AudioPermissionState = AudioPermissionState.NotRequested,
    val songs: List<Song> = emptyList(),
    val displayedSongs: List<Song> = emptyList(),
    val albums: List<Album> = emptyList(),
    val artists: List<Artist> = emptyList(),
    val searchQuery: String = "",
    val selectedLibrarySection: LibrarySection = LibrarySection.Songs,
    val selectedSortOrder: LibrarySortOrder = LibrarySortOrder.Title,
    val isSortAscending: Boolean = true,
    val errorMessage: String? = null,
    val hasLoaded: Boolean = false,
    val lastRefreshTime: Long? = null
)
