package com.example.rockstar.util

import com.example.rockstar.model.Album
import com.example.rockstar.model.Artist
import com.example.rockstar.model.Song

fun buildAlbums(songs: List<Song>): List<Album> = songs
    .groupBy { song -> "${song.albumId ?: -1L}|${song.album.lowercase()}|${song.artist.lowercase()}" }
    .map { (key, albumSongs) ->
        val first = albumSongs.minWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.title })
        Album(
            key = key,
            title = first.album,
            artist = first.artist,
            artworkUri = albumSongs.firstNotNullOfOrNull { it.artworkUri },
            songCount = albumSongs.size,
            totalDurationMs = albumSongs.sumOf { it.durationMs.coerceAtLeast(0L) }
        )
    }
    .sortedWith(
        compareBy(String.CASE_INSENSITIVE_ORDER, Album::title).thenBy(
            String.CASE_INSENSITIVE_ORDER,
            Album::artist
        )
    )

fun buildArtists(songs: List<Song>): List<Artist> = songs
    .groupBy { it.artist.lowercase() }
    .map { (key, artistSongs) ->
        Artist(
            key = key,
            name = artistSongs.first().artist,
            albumCount = artistSongs.map { it.album.lowercase() }.toSet().size,
            songCount = artistSongs.size
        )
    }
    .sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER, Artist::name))
