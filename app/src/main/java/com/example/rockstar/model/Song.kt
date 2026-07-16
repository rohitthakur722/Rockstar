package com.example.rockstar.model

private const val UNKNOWN_ARTIST = "Unknown Artist"
private const val UNKNOWN_ALBUM = "Unknown Album"
private const val UNKNOWN_TITLE = "Untitled Song"

data class Song(
    val id: Long,
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
    val mimeType: String?
) {
    companion object {
        fun safeTitle(value: String?): String = value?.trim().orEmpty().ifBlank { UNKNOWN_TITLE }
        fun safeArtist(value: String?): String = value?.trim().orEmpty().ifBlank { UNKNOWN_ARTIST }
        fun safeAlbum(value: String?): String = value?.trim().orEmpty().ifBlank { UNKNOWN_ALBUM }
    }
}
