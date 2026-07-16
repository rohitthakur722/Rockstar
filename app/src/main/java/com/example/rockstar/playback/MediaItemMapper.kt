package com.example.rockstar.playback

import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata

import com.example.rockstar.model.Song

fun Song.toMediaItemOrNull(): MediaItem? {
    val mediaUri = contentUri.toUriOrNull() ?: return null
    val artwork = artworkUri?.toUriOrNull()
    return MediaItem.Builder()
        .setMediaId(id.toString())
        .setUri(mediaUri)
        .setMimeType(mimeType)
        .setMediaMetadata(
            MediaMetadata.Builder()
                .setTitle(title)
                .setArtist(artist)
                .setAlbumTitle(album)
                .setArtworkUri(artwork)
                .setDurationMs(durationMs.takeIf { it > 0L })
                .setMediaType(MediaMetadata.MEDIA_TYPE_MUSIC)
                .build()
        )
        .build()
}

fun String.toUriOrNull(): Uri? = runCatching { Uri.parse(this) }
    .getOrNull()
    ?.takeIf { it.scheme == "content" }
