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
    private var playlistSongsJob: Job? = null

    fun setOwnerUid(uid: String?) {
        val normalized = uid?.takeIf { it.isNotBlank() }
        if (ownerUid == normalized) return
        ownerUid = normalized
        likedIdsJob?.cancel()
        likedSongsJob?.cancel()
        playlistsJob?.cancel()
        playlistSongsJob?.cancel()

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
                _uiState.update { state ->
                    val selectedStillExists =
                        state.selectedPlaylistId?.let { id -> playlists.any { it.playlistId == id } } == true
                    state.copy(
                        playlists = playlists,
                        selectedPlaylistId = state.selectedPlaylistId.takeIf { selectedStillExists },
                        selectedPlaylistSongs = if (selectedStillExists) state.selectedPlaylistSongs else emptyList(),
                        isLoading = false,
                        errorMessage = null
                    )
                }
            }
        }
    }

    fun selectPlaylist(playlistId: Long?) {
        playlistSongsJob?.cancel()
        val uid = ownerUid
        if (uid.isNullOrBlank() || playlistId == null) {
            _uiState.update { it.copy(selectedPlaylistId = null, selectedPlaylistSongs = emptyList()) }
            return
        }
        _uiState.update {
            it.copy(
                selectedPlaylistId = playlistId,
                selectedPlaylistSongs = emptyList(),
                isLoading = true
            )
        }
        playlistSongsJob = viewModelScope.launch {
            repository.observePlaylistSongs(uid, playlistId).collectLatest { songs ->
                _uiState.update { it.copy(selectedPlaylistSongs = songs, isLoading = false, errorMessage = null) }
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
                .onFailure { error -> failSongOperation(error) }
        }
    }

    fun toggleLiked(song: Song) {
        setLiked(song, !_uiState.value.likedSongIds.contains(song.id))
    }

    fun createPlaylist(name: String) = ownerWrite { uid ->
        repository.createPlaylist(uid, name).map { Unit }
    }

    fun renamePlaylist(playlistId: Long, name: String) = ownerWrite { uid ->
        repository.renamePlaylist(uid, playlistId, name)
    }

    fun deletePlaylist(playlistId: Long) = ownerWrite { uid ->
        repository.deletePlaylist(uid, playlistId).onSuccess {
            if (_uiState.value.selectedPlaylistId == playlistId) selectPlaylist(null)
        }
    }

    fun addSongToPlaylist(playlistId: Long, song: Song) = ownerWrite { uid ->
        repository.addSongToPlaylist(uid, playlistId, song)
    }

    fun removeSongFromPlaylist(playlistId: Long, songId: Long) = ownerWrite { uid ->
        repository.removeSongFromPlaylist(uid, playlistId, songId)
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }

    private fun ownerWrite(operation: suspend (String) -> Result<Unit>) {
        val uid = ownerUid
        if (uid.isNullOrBlank()) {
            _uiState.update { it.copy(errorMessage = "Sign in to use playlists.") }
            return
        }
        if (_uiState.value.isLoading) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }
            operation(uid)
                .onSuccess { _uiState.update { it.copy(isLoading = false, successMessage = "Playlist updated") } }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Could not update playlist."
                        )
                    }
                }
        }
    }

    private fun failSongOperation(error: Throwable) {
        _uiState.update {
            it.copy(
                activeSongId = null,
                isLoading = false,
                errorMessage = error.message ?: "Could not update liked songs."
            )
        }
    }
}
