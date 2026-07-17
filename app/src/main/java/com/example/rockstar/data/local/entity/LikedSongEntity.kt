package com.example.rockstar.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "liked_songs",
    primaryKeys = ["ownerUid", "songId"],
    foreignKeys = [ForeignKey(
        entity = SavedSongEntity::class,
        parentColumns = ["songId"],
        childColumns = ["songId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("ownerUid"), Index("songId")]
)
data class LikedSongEntity(
    val ownerUid: String,
    val songId: Long,
    val likedAt: Long
)
