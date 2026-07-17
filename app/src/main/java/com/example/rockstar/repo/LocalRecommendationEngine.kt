package com.example.rockstar.repo

import com.example.rockstar.model.Song

object LocalRecommendationEngine {
    fun recommend(
        librarySongs: List<Song>,
        likedSongs: List<Song>,
        recentSongs: List<Song>,
        limit: Int = 8
    ): List<Song> {
        if (librarySongs.isEmpty()) return emptyList()

        val recentIds = recentSongs.take(10).map { it.id }.toSet()
        val likedIds = likedSongs.map { it.id }.toSet()
        val artistScores = mutableMapOf<String, Int>()
        val albumScores = mutableMapOf<String, Int>()

        likedSongs.forEach { song ->
            artistScores[song.artist] = (artistScores[song.artist] ?: 0) + 4
            albumScores[song.album] = (albumScores[song.album] ?: 0) + 3
        }
        recentSongs.take(20).forEachIndexed { index, song ->
            val weight = (20 - index).coerceAtLeast(1)
            artistScores[song.artist] = (artistScores[song.artist] ?: 0) + weight
            albumScores[song.album] = (albumScores[song.album] ?: 0) + weight / 2
        }

        val ranked = librarySongs
            .asSequence()
            .filter { it.contentUri.isNotBlank() }
            .filterNot { it.id in likedIds }
            .distinctBy { it.id }
            .map { song ->
                val affinity = (artistScores[song.artist] ?: 0) + (albumScores[song.album] ?: 0)
                val recencyPenalty = if (song.id in recentIds) 10 else 0
                song to (affinity - recencyPenalty)
            }
            .filter { it.second > 0 }
            .sortedWith(compareByDescending<Pair<Song, Int>> { it.second }.thenByDescending { it.first.dateAdded }
                .thenBy { it.first.title })
            .map { it.first }
            .take(limit)
            .toList()

        return if (ranked.isNotEmpty()) ranked else librarySongs
            .asSequence()
            .filter { it.contentUri.isNotBlank() }
            .distinctBy { it.id }
            .sortedByDescending { it.dateAdded }
            .take(limit)
            .toList()
    }
}
