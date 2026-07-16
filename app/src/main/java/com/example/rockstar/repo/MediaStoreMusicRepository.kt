package com.example.rockstar.repo

import android.content.ContentResolver
import android.content.ContentUris
import android.database.Cursor
import android.net.Uri

import android.provider.MediaStore
import com.example.rockstar.model.Song
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MediaStoreMusicRepository(
    private val contentResolver: ContentResolver,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : MusicRepository {

    override suspend fun loadSongs(): Result<List<Song>> = withContext(ioDispatcher) {
        try {
            Result.success(querySongs())
        } catch (securityException: SecurityException) {
            Result.failure(
                MusicRepositoryException(
                    "Audio permission is required to read your local library.",
                    securityException
                )
            )
        } catch (exception: Exception) {
            Result.failure(MusicRepositoryException("We could not scan your local music library.", exception))
        }
    }

    override suspend fun refreshSongs(): Result<List<Song>> = loadSongs()

    private fun querySongs(): List<Song> {
        val songs = mutableListOf<Song>()
        val collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.TRACK,
            MediaStore.Audio.Media.YEAR,
            MediaStore.Audio.Media.MIME_TYPE,
            MediaStore.Audio.Media.IS_MUSIC
        )
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0 AND ${MediaStore.Audio.Media.DURATION} > 0"
        val sortOrder = "${MediaStore.Audio.Media.TITLE} COLLATE NOCASE ASC"

        contentResolver.query(collection, projection, selection, null, sortOrder)?.use { cursor ->
            while (cursor.moveToNext()) {
                cursor.toSongOrNull(collection)?.let(songs::add)
            }
        }

        return songs.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER, Song::title).thenBy { it.id })
    }

    private fun Cursor.toSongOrNull(collection: Uri): Song? {
        return try {
            val id = getRequiredLong(MediaStore.Audio.Media._ID) ?: return null
            val albumId = getOptionalLong(MediaStore.Audio.Media.ALBUM_ID)?.takeIf { it > 0L }
            val durationMs = getOptionalLong(MediaStore.Audio.Media.DURATION)?.coerceAtLeast(0L) ?: 0L
            Song(
                id = id,
                title = Song.safeTitle(getOptionalString(MediaStore.Audio.Media.TITLE)),
                artist = Song.safeArtist(getOptionalString(MediaStore.Audio.Media.ARTIST)),
                album = Song.safeAlbum(getOptionalString(MediaStore.Audio.Media.ALBUM)),
                albumId = albumId,
                durationMs = durationMs,
                contentUri = ContentUris.withAppendedId(collection, id),
                artworkUri = albumId?.let(::albumArtworkUri),
                dateAdded = getOptionalLong(MediaStore.Audio.Media.DATE_ADDED) ?: 0L,
                trackNumber = getOptionalInt(MediaStore.Audio.Media.TRACK)?.takeIf { it > 0 }?.rem(1000),
                year = getOptionalInt(MediaStore.Audio.Media.YEAR)?.takeIf { it > 0 },
                mimeType = getOptionalString(MediaStore.Audio.Media.MIME_TYPE)?.takeIf { it.isNotBlank() }
            )
        } catch (_: Exception) {
            null
        }
    }

    private fun albumArtworkUri(albumId: Long): Uri = ContentUris.withAppendedId(ALBUM_ARTWORK_URI, albumId)

    private fun Cursor.getRequiredLong(columnName: String): Long? = getColumnIndex(columnName)
        .takeIf { it >= 0 }
        ?.let { index -> if (isNull(index)) null else getLong(index) }

    private fun Cursor.getOptionalLong(columnName: String): Long? = getRequiredLong(columnName)

    private fun Cursor.getOptionalInt(columnName: String): Int? = getColumnIndex(columnName)
        .takeIf { it >= 0 }
        ?.let { index -> if (isNull(index)) null else getInt(index) }

    private fun Cursor.getOptionalString(columnName: String): String? = getColumnIndex(columnName)
        .takeIf { it >= 0 }
        ?.let { index -> if (isNull(index)) null else getString(index) }

    private companion object {
        val ALBUM_ARTWORK_URI: Uri = Uri.parse("content://media/external/audio/albumart")
    }
}
