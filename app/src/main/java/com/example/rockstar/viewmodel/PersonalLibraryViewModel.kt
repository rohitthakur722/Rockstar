package com.example.rockstar.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rockstar.model.Song
import com.example.rockstar.repo.PersonalLibraryRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PersonalLibraryViewModel(
    private val repository: PersonalLibraryRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(PersonalLibraryUiState())
    val uiState = _uiState.asStateFlow()

    private var ownerUid: String? = null
    private var likedIdsJob: Job? = null
    private var likedSongsJob: Job? = null
    private var playlistsJob: Job? = null

    fun setOwnerUid(uid: String?) {
        val normalized = uid?.takeIf { it.isNotBlank() }
        if (ownerUid == normalized) return
        ownerUid = normalized
        likedIdsJob?.cancel()
        likedSongsJob?.cancel()
        playlistsJob?.cancel()

        if (normalized == null) {
            _uiState.value = PersonalLibraryUiState()
            return
        }

        _uiState.value = PersonalLibraryUiState(ownerUid = normalized, isLoading = true)
        likedIdsJob = viewModelScope.launch {
            repository.observeLikedSongIds(normalized).collectLatest { ids ->
                _uiState.update { it.copy(likedSongIds = ids.toSet(), isLoading = false, errorMessage = null) }
            }
        }
        likedSongsJob = viewModelScope.launch {
            repository.observeLikedSongs(normalized).collectLatest { songs ->
                _uiState.update { it.copy(likedSongs = songs, isLoading = false, errorMessage = null) }
            }
        }
        playlistsJob = viewModelScope.launch {
            repository.observePlaylists(normalized).collectLatest { playlists ->
                _uiState.update { it.copy(playlists = playlists, isLoading = false, errorMessage = null) }
            }
        }
    }

    fun setLiked(song: Song, liked: Boolean) {
        val uid = ownerUid
        if (uid.isNullOrBlank()) {
            _uiState.update { it.copy(errorMessage = "Sign in to save liked songs.") }
            return
        }
        if (_uiState.value.activeSongId == song.id) return

        viewModelScope.launch {
            _uiState.update { it.copy(activeSongId = song.id, errorMessage = null, successMessage = null) }
            repository.setLiked(uid, song, liked)
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            activeSongId = null,
                            successMessage = if (liked) "Added to Liked Songs" else "Removed from Liked Songs"
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            activeSongId = null,
                            errorMessage = error.message ?: "Could not update liked songs."
                        )
                    }
                }
        }
    }

    fun toggleLiked(song: Song) {
        setLiked(song, !_uiState.value.likedSongIds.contains(song.id))
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }
}
