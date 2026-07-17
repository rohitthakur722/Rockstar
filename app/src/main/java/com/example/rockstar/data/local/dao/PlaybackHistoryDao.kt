package com.example.rockstar.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.rockstar.data.local.entity.PlaybackHistoryEntity
import com.example.rockstar.data.local.entity.SavedSongEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaybackHistoryDao {
    @Insert
    suspend fun insert(entity: PlaybackHistoryEntity)

    @Query("SELECT saved_songs.* FROM saved_songs INNER JOIN playback_history ON saved_songs.songId = playback_history.songId WHERE playback_history.ownerUid = :ownerUid GROUP BY saved_songs.songId ORDER BY MAX(playback_history.playedAt) DESC LIMIT :limit")
    fun observeRecentSongs(ownerUid: String, limit: Int): Flow<List<SavedSongEntity>>

    @Query("SELECT songId FROM playback_history WHERE ownerUid = :ownerUid GROUP BY songId ORDER BY COUNT(*) DESC, MAX(playedAt) DESC LIMIT :limit")
    fun observeMostPlayedSongIds(ownerUid: String, limit: Int): Flow<List<Long>>

    @Query("DELETE FROM playback_history WHERE ownerUid = :ownerUid")
    suspend fun clear(ownerUid: String)
}
