package com.example.rockstar.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.rockstar.data.local.entity.LikedSongEntity
import com.example.rockstar.data.local.entity.SavedSongEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LikedSongDao {
    @Query("SELECT songId FROM liked_songs WHERE ownerUid = :ownerUid ORDER BY likedAt DESC")
    fun observeLikedSongIds(ownerUid: String): Flow<List<Long>>

    @Query("SELECT saved_songs.* FROM saved_songs INNER JOIN liked_songs ON saved_songs.songId = liked_songs.songId WHERE liked_songs.ownerUid = :ownerUid ORDER BY liked_songs.likedAt DESC")
    fun observeLikedSongs(ownerUid: String): Flow<List<SavedSongEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM liked_songs WHERE ownerUid = :ownerUid AND songId = :songId)")
    fun observeIsLiked(ownerUid: String, songId: Long): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun like(entity: LikedSongEntity)

    @Query("DELETE FROM liked_songs WHERE ownerUid = :ownerUid AND songId = :songId")
    suspend fun unlike(ownerUid: String, songId: Long)

    @Query("DELETE FROM liked_songs WHERE ownerUid = :ownerUid")
    suspend fun clear(ownerUid: String)
}
