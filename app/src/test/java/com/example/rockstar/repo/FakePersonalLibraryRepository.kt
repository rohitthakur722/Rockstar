package com.example.rockstar.repo

import com.example.rockstar.data.local.entity.PlaylistEntity
import com.example.rockstar.model.Song
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakePersonalLibraryRepository : PersonalLibraryRepository {
    val likedIds = mutableMapOf<String, MutableStateFlow<List<Long>>>()
    val likedSongs = mutableMapOf<String, MutableStateFlow<List<Song>>>()
    val playlists = mutableMapOf<String, MutableStateFlow<List<PlaylistEntity>>>()
    val playlistSongs = mutableMapOf<Long, MutableStateFlow<List<Song>>>()
    val history = mutableMapOf<String, MutableStateFlow<List<Song>>>()
    val recordedHistory = mutableListOf<Song>()
    var failWrites = false
    private var nextPlaylistId = 1L

    override fun observeLikedSongIds(ownerUid: String): Flow<List<Long>> = likedIds.flow(ownerUid, emptyList())
    override fun observeLikedSongs(ownerUid: String): Flow<List<Song>> = likedSongs.flow(ownerUid, emptyList())

    override suspend fun setLiked(ownerUid: String, song: Song, liked: Boolean): Result<Unit> = write {
        val songs = likedSongs.state(ownerUid, emptyList())
        val ids = likedIds.state(ownerUid, emptyList())
        if (liked) {
            songs.value = (listOf(song) + songs.value.filterNot { it.id == song.id })
            ids.value = (listOf(song.id) + ids.value.filterNot { it == song.id })
        } else {
            songs.value = songs.value.filterNot { it.id == song.id }
            ids.value = ids.value.filterNot { it == song.id }
        }
    }

    override suspend fun toggleLiked(ownerUid: String, song: Song): Result<Unit> =
        setLiked(ownerUid, song, !likedIds.state(ownerUid, emptyList()).value.contains(song.id))

    override fun observePlaylists(ownerUid: String): Flow<List<PlaylistEntity>> = playlists.flow(ownerUid, emptyList())
    override fun observePlaylistSongs(ownerUid: String, playlistId: Long): Flow<List<Song>> =
        playlistSongs.getOrPut(playlistId) { MutableStateFlow(emptyList()) }

    override suspend fun createPlaylist(ownerUid: String, name: String): Result<Long> = if (name.isBlank()) {
        Result.failure(IllegalArgumentException("Playlist name cannot be blank."))
    } else writeResult {
        val id = nextPlaylistId++
        val state = playlists.state(ownerUid, emptyList())
        state.value = listOf(PlaylistEntity(id, ownerUid, name.trim(), null, 1L, 1L)) + state.value
        id
    }

    override suspend fun renamePlaylist(ownerUid: String, playlistId: Long, name: String): Result<Unit> = write {
        require(name.isNotBlank()) { "Playlist name cannot be blank." }
        val state = playlists.state(ownerUid, emptyList())
        state.value = state.value.map { if (it.playlistId == playlistId) it.copy(name = name.trim()) else it }
    }

    override suspend fun deletePlaylist(ownerUid: String, playlistId: Long): Result<Unit> = write {
        val state = playlists.state(ownerUid, emptyList())
        state.value = state.value.filterNot { it.playlistId == playlistId }
        playlistSongs.remove(playlistId)
    }

    override suspend fun addSongToPlaylist(ownerUid: String, playlistId: Long, song: Song): Result<Unit> = write {
        val state = playlistSongs.getOrPut(playlistId) { MutableStateFlow(emptyList()) }
        state.value = state.value + listOf(song).filterNot { candidate -> state.value.any { it.id == candidate.id } }
    }

    override suspend fun removeSongFromPlaylist(ownerUid: String, playlistId: Long, songId: Long): Result<Unit> =
        write {
            val state = playlistSongs.getOrPut(playlistId) { MutableStateFlow(emptyList()) }
            state.value = state.value.filterNot { it.id == songId }
        }

    override fun observeRecentHistory(ownerUid: String, limit: Int): Flow<List<Song>> =
        history.flow(ownerUid, emptyList()).map { it.take(limit) }

    override fun observeMostPlayedSongIds(ownerUid: String, limit: Int): Flow<List<Long>> =
        history.flow(ownerUid, emptyList()).map { songs ->
            songs.groupingBy { it.id }.eachCount().entries.sortedByDescending { it.value }.take(limit).map { it.key }
        }

    override suspend fun recordPlayback(
        ownerUid: String,
        song: Song,
        listenedDurationMs: Long,
        completed: Boolean
    ): Result<Unit> = write {
        recordedHistory += song
        val state = history.state(ownerUid, emptyList())
        state.value = listOf(song) + state.value
    }

    override suspend fun clearHistory(ownerUid: String): Result<Unit> =
        write { history.state(ownerUid, emptyList()).value = emptyList() }

    override suspend fun clearOwnerData(ownerUid: String): Result<Unit> = write {
        likedIds.state(ownerUid, emptyList()).value = emptyList()
        likedSongs.state(ownerUid, emptyList()).value = emptyList()
        playlists.state(ownerUid, emptyList()).value = emptyList()
        history.state(ownerUid, emptyList()).value = emptyList()
    }

    private suspend fun write(block: suspend () -> Unit): Result<Unit> = writeResult { block() }
    private suspend fun <T> writeResult(block: suspend () -> T): Result<T> =
        if (failWrites) Result.failure(IllegalStateException("database unavailable")) else runCatching { block() }

    private fun <T> MutableMap<String, MutableStateFlow<T>>.flow(ownerUid: String, initial: T): Flow<T> =
        state(ownerUid, initial)

    private fun <T> MutableMap<String, MutableStateFlow<T>>.state(ownerUid: String, initial: T): MutableStateFlow<T> =
        getOrPut(ownerUid) { MutableStateFlow(initial) }
}
