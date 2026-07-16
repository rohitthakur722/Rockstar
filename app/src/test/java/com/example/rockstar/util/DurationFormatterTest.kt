package com.example.rockstar.util

import com.example.rockstar.model.Song
import org.junit.Assert.assertEquals
import org.junit.Test

class DurationFormatterTest {
    @Test
    fun `formats duration under one hour`() {
        assertEquals("2:05", formatDuration(125_000L))
    }

    @Test
    fun `formats duration over one hour`() {
        assertEquals("1:02:05", formatDuration(3_725_000L))
    }

    @Test
    fun `formats zero duration`() {
        assertEquals("0:00", formatDuration(0L))
    }

    @Test
    fun `protects against negative duration`() {
        assertEquals("0:00", formatDuration(-10_000L))
    }

    @Test
    fun `song metadata fallbacks are stable`() {
        assertEquals("Untitled Song", Song.safeTitle(" "))
        assertEquals("Unknown Artist", Song.safeArtist(null))
        assertEquals("Unknown Album", Song.safeAlbum(""))
    }
}
