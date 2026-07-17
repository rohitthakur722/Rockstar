package com.example.rockstar.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "playback_history",
    foreignKeys = [ForeignKey(
        entity = SavedSongEntity::class,
        parentColumns = ["songId"],
        childColumns = ["songId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("ownerUid"), Index("songId"), Index(value = ["ownerUid", "playedAt"])]
)
data class PlaybackHistoryEntity(
    @PrimaryKey(autoGenerate = true) val historyId: Long = 0L,
    val ownerUid: String,
    val songId: Long,
    val playedAt: Long,
    val listenedDurationMs: Long,
    val completed: Boolean
)
