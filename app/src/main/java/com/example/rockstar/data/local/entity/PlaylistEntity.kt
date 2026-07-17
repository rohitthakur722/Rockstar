package com.example.rockstar.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "playlists", indices = [Index("ownerUid"), Index(value = ["ownerUid", "name"], unique = false)])
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true) val playlistId: Long = 0L,
    val ownerUid: String,
    val name: String,
    val description: String? = null,
    val createdAt: Long,
    val updatedAt: Long
)
