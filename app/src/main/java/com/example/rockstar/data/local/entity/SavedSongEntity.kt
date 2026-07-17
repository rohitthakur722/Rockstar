package com.example.rockstar.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.rockstar.model.Song

@Entity(tableName = "saved_songs")
data class SavedSongEntity(
    @PrimaryKey val songId: Long,
    val title: String,
    val artist: String,
    val album: String,
    val albumId: Long?,
    val durationMs: Long,
    val contentUri: String,
    val artworkUri: String?,
    val dateAdded: Long,
    val trackNumber: Int?,
    val year: Int?,
    val mimeType: String?,
    val snapshotUpdatedAt: Long
) {
    fun toSong(): Song = Song(
        songId,
        title,
        artist,
        album,
        albumId,
        durationMs,
        contentUri,
        artworkUri,
        dateAdded,
        trackNumber,
        year,
        mimeType
    )

    companion object {
        fun fromSong(song: Song, now: Long = System.currentTimeMillis()) = SavedSongEntity(
            songId = song.id,
            title = song.title,
            artist = song.artist,
            album = song.album,
            albumId = song.albumId,
            durationMs = song.durationMs,
            contentUri = song.contentUri,
            artworkUri = song.artworkUri,
            dateAdded = song.dateAdded,
            trackNumber = song.trackNumber,
            year = song.year,
            mimeType = song.mimeType,
            snapshotUpdatedAt = now
        )
    }
}
