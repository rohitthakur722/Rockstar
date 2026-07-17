package com.example.rockstar.repo

import com.example.rockstar.data.local.RockstarDatabase
import com.example.rockstar.data.local.entity.LikedSongEntity
import com.example.rockstar.data.local.entity.PlaybackHistoryEntity
import com.example.rockstar.data.local.entity.PlaylistEntity
import com.example.rockstar.data.local.entity.PlaylistSongEntity
import com.example.rockstar.data.local.entity.SavedSongEntity
import com.example.rockstar.model.Song
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class PersonalLibraryRepositoryImpl(
    private val database: RockstarDatabase,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : PersonalLibraryRepository {
    private val savedSongDao = database.savedSongDao()
    private val likedSongDao = database.likedSongDao()
    private val playlistDao = database.playlistDao()
    private val playlistSongDao = database.playlistSongDao()
    private val historyDao = database.playbackHistoryDao()

    override fun observeLikedSongIds(ownerUid: String): Flow<List<Long>> = likedSongDao.observeLikedSongIds(ownerUid)
    override fun observeLikedSongs(ownerUid: String): Flow<List<Song>> =
        likedSongDao.observeLikedSongs(ownerUid).map { it.map(SavedSongEntity::toSong) }

    override suspend fun setLiked(ownerUid: String, song: Song, liked: Boolean): Result<Unit> = write(ownerUid) {
        savedSongDao.upsert(SavedSongEntity.fromSong(song))
        if (liked) likedSongDao.like(
            LikedSongEntity(
                ownerUid,
                song.id,
                System.currentTimeMillis()
            )
        ) else likedSongDao.unlike(ownerUid, song.id)
    }

    override suspend fun toggleLiked(ownerUid: String, song: Song): Result<Unit> = setLiked(ownerUid, song, true)

    override fun observePlaylists(ownerUid: String): Flow<List<PlaylistEntity>> = playlistDao.observePlaylists(ownerUid)
    override fun observePlaylistSongs(ownerUid: String, playlistId: Long): Flow<List<Song>> =
        playlistSongDao.observeSongs(playlistId).map { it.map(SavedSongEntity::toSong) }

    override suspend fun createPlaylist(ownerUid: String, name: String): Result<Long> = writeResult(ownerUid) {
        val trimmed = validatePlaylistName(name)
        val now = System.currentTimeMillis()
        playlistDao.insert(PlaylistEntity(ownerUid = ownerUid, name = trimmed, createdAt = now, updatedAt = now))
    }

    override suspend fun renamePlaylist(ownerUid: String, playlistId: Long, name: String): Result<Unit> =
        write(ownerUid) {
            playlistDao.rename(ownerUid, playlistId, validatePlaylistName(name), System.currentTimeMillis())
        }

    override suspend fun deletePlaylist(ownerUid: String, playlistId: Long): Result<Unit> =
        write(ownerUid) { playlistDao.delete(ownerUid, playlistId) }

    override suspend fun addSongToPlaylist(ownerUid: String, playlistId: Long, song: Song): Result<Unit> =
        write(ownerUid) {
            savedSongDao.upsert(SavedSongEntity.fromSong(song))
            val position = playlistSongDao.countSongs(playlistId)
            playlistSongDao.add(PlaylistSongEntity(playlistId, song.id, position, System.currentTimeMillis()))
            playlistDao.touch(ownerUid, playlistId, System.currentTimeMillis())
        }

    override suspend fun removeSongFromPlaylist(ownerUid: String, playlistId: Long, songId: Long): Result<Unit> =
        write(ownerUid) {
            playlistSongDao.remove(playlistId, songId)
        }

    override fun observeRecentHistory(ownerUid: String, limit: Int): Flow<List<Song>> =
        historyDao.observeRecentSongs(ownerUid, limit).map { it.map(SavedSongEntity::toSong) }

    override fun observeMostPlayedSongIds(ownerUid: String, limit: Int): Flow<List<Long>> =
        historyDao.observeMostPlayedSongIds(ownerUid, limit)

    override suspend fun recordPlayback(
        ownerUid: String,
        song: Song,
        listenedDurationMs: Long,
        completed: Boolean
    ): Result<Unit> = write(ownerUid) {
        savedSongDao.upsert(SavedSongEntity.fromSong(song))
        historyDao.insert(
            PlaybackHistoryEntity(
                ownerUid = ownerUid,
                songId = song.id,
                playedAt = System.currentTimeMillis(),
                listenedDurationMs = listenedDurationMs.coerceAtLeast(0L),
                completed = completed
            )
        )
    }

    override suspend fun clearHistory(ownerUid: String): Result<Unit> = write(ownerUid) { historyDao.clear(ownerUid) }

    override suspend fun clearOwnerData(ownerUid: String): Result<Unit> = write(ownerUid) {
        historyDao.clear(ownerUid)
        likedSongDao.clear(ownerUid)
        playlistDao.clear(ownerUid)
    }

    private suspend fun write(ownerUid: String, block: suspend () -> Unit): Result<Unit> =
        writeResult(ownerUid) { block(); Unit }

    private suspend fun <T> writeResult(ownerUid: String, block: suspend () -> T): Result<T> =
        withContext(ioDispatcher) {
            if (ownerUid.isBlank()) return@withContext Result.failure(IllegalStateException("Sign in to use your personal library."))
            runCatching { block() }
        }

    private fun validatePlaylistName(name: String): String {
        val trimmed = name.trim()
        require(trimmed.isNotBlank()) { "Playlist name cannot be blank." }
        require(trimmed.length <= 80) { "Playlist name is too long." }
        return trimmed
    }
}
