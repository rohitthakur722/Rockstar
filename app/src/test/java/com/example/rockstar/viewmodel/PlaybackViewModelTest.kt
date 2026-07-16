package com.example.rockstar.viewmodel

import com.example.rockstar.model.Song
import com.example.rockstar.playback.FakePlaybackController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PlaybackViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private lateinit var controller: FakePlaybackController
    private lateinit var viewModel: PlaybackViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher); controller = FakePlaybackController(); viewModel =
            PlaybackViewModel(controller)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state connects controller`() = runTest(dispatcher) {
        advanceUntilIdle(); assertEquals(
        PlaybackConnectionState.Connected,
        viewModel.uiState.value.connectionState
    )
    }

    @Test
    fun `playSong builds queue and respects selected index`() = runTest(dispatcher) {
        val queue = sampleSongs()
        viewModel.playSong(queue[2], queue)
        advanceUntilIdle()
        assertEquals(1, controller.playQueueCalls)
        assertEquals(2, viewModel.uiState.value.currentIndex)
        assertEquals("Gamma", viewModel.uiState.value.currentSong!!.title)
    }

    @Test
    fun `invalid index is clamped`() = runTest(dispatcher) {
        viewModel.playQueue(sampleSongs(), 99)
        assertEquals(2, viewModel.uiState.value.currentIndex)
    }

    @Test
    fun `play pause next previous delegate`() = runTest(dispatcher) {
        viewModel.togglePlayPause(); viewModel.next(); viewModel.previous()
        assertEquals(1, controller.toggleCalls)
        assertEquals(1, controller.nextCalls)
        assertEquals(1, controller.previousCalls)
    }

    @Test
    fun `seek clamps negative and beyond duration`() = runTest(dispatcher) {
        viewModel.playQueue(sampleSongs(), 0)
        viewModel.seekTo(-100)
        assertEquals(0L, controller.lastSeek)
        viewModel.seekTo(999_999)
        assertEquals(120_000L, controller.lastSeek)
    }

    @Test
    fun `shuffle and repeat update state`() = runTest(dispatcher) {
        viewModel.setShuffleEnabled(true)
        assertTrue(viewModel.uiState.value.shuffleEnabled)
        viewModel.cycleRepeatMode(); assertEquals(RockstarRepeatMode.All, viewModel.uiState.value.repeatMode)
        viewModel.cycleRepeatMode(); assertEquals(RockstarRepeatMode.One, viewModel.uiState.value.repeatMode)
        viewModel.cycleRepeatMode(); assertEquals(RockstarRepeatMode.Off, viewModel.uiState.value.repeatMode)
    }

    @Test
    fun `current media transition updates current song`() = runTest(dispatcher) {
        viewModel.playQueue(sampleSongs(), 0)
        viewModel.skipToQueueItem(1)
        assertEquals("Beta", viewModel.uiState.value.currentSong!!.title)
    }

    @Test
    fun `playback error exposes professional message`() = runTest(dispatcher) {
        controller.emit(
            PlaybackUiState(
                playbackState = RockstarPlaybackState.Error,
                errorMessage = "Rockstar could not play this audio file."
            )
        )
        assertTrue(viewModel.uiState.value.errorMessage!!.contains("could not play"))
    }

    @Test
    fun `clearing queue clears active media`() = runTest(dispatcher) {
        viewModel.playQueue(sampleSongs(), 0)
        viewModel.clearQueue()
        assertFalse(viewModel.uiState.value.hasActiveMedia)
        assertNull(viewModel.uiState.value.currentSong)
    }

    @Test
    fun `queue removal handles invalid current and final item`() = runTest(dispatcher) {
        viewModel.playQueue(sampleSongs(), 1)
        viewModel.removeQueueItem(0)
        assertEquals(2, viewModel.uiState.value.queue.size)
        viewModel.clearQueue()
        assertTrue(viewModel.uiState.value.queue.isEmpty())
    }

    private fun sampleSongs() = listOf(song(1, "Alpha"), song(2, "Beta"), song(3, "Gamma"))
    private fun song(id: Long, title: String) =
        Song(id, title, "Artist", "Album", 1, 120_000, "content://songs/$id", null, id, id.toInt(), 2024, "audio/mpeg")
}
