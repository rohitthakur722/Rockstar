package com.example.rockstar.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.rockstar.data.local.entity.PlaylistSongEntity
import com.example.rockstar.data.local.entity.SavedSongEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistSongDao {
    @Query("SELECT saved_songs.* FROM saved_songs INNER JOIN playlist_songs ON saved_songs.songId = playlist_songs.songId WHERE playlist_songs.playlistId = :playlistId ORDER BY playlist_songs.position ASC")
    fun observeSongs(playlistId: Long): Flow<List<SavedSongEntity>>

    @Query("SELECT COUNT(*) FROM playlist_songs WHERE playlistId = :playlistId")
    suspend fun countSongs(playlistId: Long): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun add(entity: PlaylistSongEntity)

    @Query("DELETE FROM playlist_songs WHERE playlistId = :playlistId AND songId = :songId")
    suspend fun remove(playlistId: Long, songId: Long)

    @Query("DELETE FROM playlist_songs WHERE playlistId = :playlistId")
    suspend fun clear(playlistId: Long)
}
