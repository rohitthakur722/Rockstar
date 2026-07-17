package com.example.rockstar.repo

import com.example.rockstar.model.Song
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LocalRecommendationEngineTest {
    @Test
    fun `liked artist increases related song ranking`() {
        val library =
            listOf(song(1, "Liked", "A", "One"), song(2, "Related", "A", "Two"), song(3, "Other", "B", "Three"))

        val result =
            LocalRecommendationEngine.recommend(library, likedSongs = listOf(library[0]), recentSongs = emptyList())

        assertEquals(listOf(2L), result.take(1).map { it.id })
    }

    @Test
    fun `recently played artist influences ranking`() {
        val library =
            listOf(song(1, "Recent", "A", "One"), song(2, "Related", "A", "Two"), song(3, "Other", "B", "Three"))

        val result =
            LocalRecommendationEngine.recommend(library, likedSongs = emptyList(), recentSongs = listOf(library[0]))

        assertEquals(2L, result.first().id)
    }

    @Test
    fun `unavailable and duplicate songs are excluded`() {
        val library = listOf(
            song(1, "Recent", "A", "One"),
            song(2, "Related", "A", "Two"),
            song(2, "Duplicate", "A", "Two"),
            song(4, "Missing", "A", "Two", contentUri = "")
        )

        val result =
            LocalRecommendationEngine.recommend(library, likedSongs = emptyList(), recentSongs = listOf(library[0]))

        assertEquals(result.map { it.id }.distinct(), result.map { it.id })
        assertTrue(result.none { it.id == 4L })
    }

    @Test
    fun `empty library produces no recommendations`() {
        assertTrue(LocalRecommendationEngine.recommend(emptyList(), emptyList(), emptyList()).isEmpty())
    }

    @Test
    fun `fallback uses real library songs deterministically`() {
        val library = listOf(song(1, "Old", "A", "One", dateAdded = 1), song(2, "New", "B", "Two", dateAdded = 2))

        val result = LocalRecommendationEngine.recommend(library, likedSongs = emptyList(), recentSongs = emptyList())

        assertEquals(listOf(2L, 1L), result.map { it.id })
    }

    private fun song(
        id: Long,
        title: String,
        artist: String,
        album: String,
        dateAdded: Long = id,
        contentUri: String = "content://songs/$id"
    ) = Song(id, title, artist, album, id, 120_000, contentUri, null, dateAdded, null, null, "audio/mpeg")
}
