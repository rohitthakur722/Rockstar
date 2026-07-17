package com.example.rockstar.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.rockstar.data.local.entity.PlaylistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {
    @Query("SELECT * FROM playlists WHERE ownerUid = :ownerUid ORDER BY updatedAt DESC")
    fun observePlaylists(ownerUid: String): Flow<List<PlaylistEntity>>

    @Query("SELECT * FROM playlists WHERE ownerUid = :ownerUid AND playlistId = :playlistId LIMIT 1")
    fun observePlaylist(ownerUid: String, playlistId: Long): Flow<PlaylistEntity?>

    @Insert
    suspend fun insert(playlist: PlaylistEntity): Long

    @Update
    suspend fun update(playlist: PlaylistEntity)

    @Query("UPDATE playlists SET name = :name, updatedAt = :updatedAt WHERE ownerUid = :ownerUid AND playlistId = :playlistId")
    suspend fun rename(ownerUid: String, playlistId: Long, name: String, updatedAt: Long)

    @Query("UPDATE playlists SET updatedAt = :updatedAt WHERE ownerUid = :ownerUid AND playlistId = :playlistId")
    suspend fun touch(ownerUid: String, playlistId: Long, updatedAt: Long)

    @Query("DELETE FROM playlists WHERE ownerUid = :ownerUid AND playlistId = :playlistId")
    suspend fun delete(ownerUid: String, playlistId: Long)

    @Query("DELETE FROM playlists WHERE ownerUid = :ownerUid")
    suspend fun clear(ownerUid: String)
}
