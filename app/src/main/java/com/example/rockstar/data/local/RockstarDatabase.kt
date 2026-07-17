package com.example.rockstar.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.rockstar.data.local.dao.LikedSongDao
import com.example.rockstar.data.local.dao.PlaybackHistoryDao
import com.example.rockstar.data.local.dao.PlaylistDao
import com.example.rockstar.data.local.dao.PlaylistSongDao
import com.example.rockstar.data.local.dao.SavedSongDao
import com.example.rockstar.data.local.entity.LikedSongEntity
import com.example.rockstar.data.local.entity.PlaybackHistoryEntity
import com.example.rockstar.data.local.entity.PlaylistEntity
import com.example.rockstar.data.local.entity.PlaylistSongEntity
import com.example.rockstar.data.local.entity.SavedSongEntity

@Database(
    entities = [SavedSongEntity::class, LikedSongEntity::class, PlaylistEntity::class, PlaylistSongEntity::class, PlaybackHistoryEntity::class],
    version = 1,
    exportSchema = true
)
abstract class RockstarDatabase : RoomDatabase() {
    abstract fun savedSongDao(): SavedSongDao
    abstract fun likedSongDao(): LikedSongDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun playlistSongDao(): PlaylistSongDao
    abstract fun playbackHistoryDao(): PlaybackHistoryDao

    companion object {
        @Volatile
        private var instance: RockstarDatabase? = null

        fun getInstance(context: Context): RockstarDatabase = instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(context.applicationContext, RockstarDatabase::class.java, "rockstar.db")
                .build()
                .also { instance = it }
        }
    }
}
