package com.example.rockstar.repo

import com.example.rockstar.data.local.entity.PlaylistEntity
import com.example.rockstar.model.Song
import kotlinx.coroutines.flow.Flow

interface PersonalLibraryRepository {
    fun observeLikedSongIds(ownerUid: String): Flow<List<Long>>
    fun observeLikedSongs(ownerUid: String): Flow<List<Song>>
    suspend fun setLiked(ownerUid: String, song: Song, liked: Boolean): Result<Unit>
    suspend fun toggleLiked(ownerUid: String, song: Song): Result<Unit>

    fun observePlaylists(ownerUid: String): Flow<List<PlaylistEntity>>
    fun observePlaylistSongs(ownerUid: String, playlistId: Long): Flow<List<Song>>
    suspend fun createPlaylist(ownerUid: String, name: String): Result<Long>
    suspend fun renamePlaylist(ownerUid: String, playlistId: Long, name: String): Result<Unit>
    suspend fun deletePlaylist(ownerUid: String, playlistId: Long): Result<Unit>
    suspend fun addSongToPlaylist(ownerUid: String, playlistId: Long, song: Song): Result<Unit>
    suspend fun removeSongFromPlaylist(ownerUid: String, playlistId: Long, songId: Long): Result<Unit>

    fun observeRecentHistory(ownerUid: String, limit: Int): Flow<List<Song>>
    fun observeMostPlayedSongIds(ownerUid: String, limit: Int): Flow<List<Long>>
    suspend fun recordPlayback(ownerUid: String, song: Song, listenedDurationMs: Long, completed: Boolean): Result<Unit>
    suspend fun clearHistory(ownerUid: String): Result<Unit>
    suspend fun clearOwnerData(ownerUid: String): Result<Unit>
}
