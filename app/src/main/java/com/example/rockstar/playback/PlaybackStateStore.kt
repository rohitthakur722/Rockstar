package com.example.rockstar.playback

import android.content.Context
import com.example.rockstar.model.Song
import com.example.rockstar.viewmodel.RockstarRepeatMode
import java.util.Base64

class PlaybackStateStore(context: Context) {
    private val prefs = context.applicationContext.getSharedPreferences("rockstar_playback_state", Context.MODE_PRIVATE)

    fun save(queue: List<Song>, currentIndex: Int, positionMs: Long, shuffle: Boolean, repeatMode: RockstarRepeatMode) {
        prefs.edit()
            .putString(KEY_QUEUE, encodeQueue(queue.take(MAX_QUEUE_SIZE)))
            .putInt(KEY_INDEX, currentIndex)
            .putLong(KEY_POSITION, positionMs.coerceAtLeast(0L))
            .putBoolean(KEY_SHUFFLE, shuffle)
            .putString(KEY_REPEAT, repeatMode.name)
            .apply()
    }

    fun restore(): RestoredPlaybackState? {
        val queue = decodeQueue(prefs.getString(KEY_QUEUE, null).orEmpty())
        if (queue.isEmpty()) return null
        return RestoredPlaybackState(
            queue = queue,
            currentIndex = prefs.getInt(KEY_INDEX, 0).coerceIn(0, queue.lastIndex),
            positionMs = prefs.getLong(KEY_POSITION, 0L).coerceAtLeast(0L),
            shuffle = prefs.getBoolean(KEY_SHUFFLE, false),
            repeatMode = runCatching {
                RockstarRepeatMode.valueOf(
                    prefs.getString(
                        KEY_REPEAT,
                        RockstarRepeatMode.Off.name
                    )!!
                )
            }.getOrDefault(RockstarRepeatMode.Off)
        )
    }

    fun clear() {
        prefs.edit().clear().apply()
    }

    private fun encodeQueue(queue: List<Song>): String = queue.joinToString(ROW_SEPARATOR) { song ->
        listOf(
            song.id.toString(),
            encode(song.title),
            encode(song.artist),
            encode(song.album),
            song.albumId?.toString().orEmpty(),
            song.durationMs.toString(),
            encode(song.contentUri),
            encode(song.artworkUri.orEmpty()),
            song.dateAdded.toString(),
            song.trackNumber?.toString().orEmpty(),
            song.year?.toString().orEmpty(),
            encode(song.mimeType.orEmpty())
        ).joinToString(FIELD_SEPARATOR)
    }

    private fun decodeQueue(value: String): List<Song> = value.split(ROW_SEPARATOR)
        .filter { it.isNotBlank() }
        .mapNotNull { row ->
            val fields = row.split(FIELD_SEPARATOR)
            if (fields.size < 12) return@mapNotNull null
            runCatching {
                Song(
                    id = fields[0].toLong(),
                    title = decode(fields[1]),
                    artist = decode(fields[2]),
                    album = decode(fields[3]),
                    albumId = fields[4].toLongOrNull(),
                    durationMs = fields[5].toLongOrNull() ?: 0L,
                    contentUri = decode(fields[6]),
                    artworkUri = decode(fields[7]).ifBlank { null },
                    dateAdded = fields[8].toLongOrNull() ?: 0L,
                    trackNumber = fields[9].toIntOrNull(),
                    year = fields[10].toIntOrNull(),
                    mimeType = decode(fields[11]).ifBlank { null }
                )
            }.getOrNull()
        }

    private fun encode(value: String): String = Base64.getUrlEncoder().encodeToString(value.toByteArray(Charsets.UTF_8))
    private fun decode(value: String): String = String(Base64.getUrlDecoder().decode(value), Charsets.UTF_8)

    data class RestoredPlaybackState(
        val queue: List<Song>,
        val currentIndex: Int,
        val positionMs: Long,
        val shuffle: Boolean,
        val repeatMode: RockstarRepeatMode
    )

    private companion object {
        const val KEY_QUEUE = "queue"
        const val KEY_INDEX = "index"
        const val KEY_POSITION = "position"
        const val KEY_SHUFFLE = "shuffle"
        const val KEY_REPEAT = "repeat"
        const val FIELD_SEPARATOR = "\u001F"
        const val ROW_SEPARATOR = "\u001E"
        const val MAX_QUEUE_SIZE = 100
    }
}
