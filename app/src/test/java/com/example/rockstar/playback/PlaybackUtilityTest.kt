package com.example.rockstar.playback

import com.example.rockstar.util.clampPosition
import org.junit.Assert.assertEquals
import org.junit.Test

class PlaybackUtilityTest {
    @Test
    fun `position clamps negative values`() {
        assertEquals(0L, clampPosition(-100L, 1_000L))
    }

    @Test
    fun `position clamps beyond duration`() {
        assertEquals(1_000L, clampPosition(2_000L, 1_000L))
    }

    @Test
    fun `unknown duration allows non-negative position`() {
        assertEquals(2_000L, clampPosition(2_000L, 0L))
    }

    @Test
    fun `playback error mapper returns professional messages`() {
        assertEquals("This song is no longer available.", mapPlaybackError("file not found"))
        assertEquals("Audio access was removed. Grant permission to continue.", mapPlaybackError("permission denied"))
        assertEquals("Rockstar could not play this audio file.", mapPlaybackError("decoder failed"))
    }
}
