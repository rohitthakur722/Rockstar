package com.example.rockstar.viewmodel

import com.example.rockstar.data.local.entity.PlaylistEntity
import com.example.rockstar.model.Song

data class PersonalLibraryUiState(
    val ownerUid: String? = null,
    val likedSongIds: Set<Long> = emptySet(),
    val likedSongs: List<Song> = emptyList(),
    val playlists: List<PlaylistEntity> = emptyList(),
    val selectedPlaylistId: Long? = null,
    val selectedPlaylistSongs: List<Song> = emptyList(),
    val isLoading: Boolean = false,
    val activeSongId: Long? = null,
    val errorMessage: String? = null,
    val successMessage: String? = null
) {
    val isAuthenticated: Boolean = !ownerUid.isNullOrBlank()
}
