package com.example.rockstar.viewmodel

import com.example.rockstar.model.Song
import com.example.rockstar.repo.FakePersonalLibraryRepository
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PersonalLibraryViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private lateinit var repo: FakePersonalLibraryRepository
    private lateinit var viewModel: PersonalLibraryViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        repo = FakePersonalLibraryRepository()
        viewModel = PersonalLibraryViewModel(repo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `unauthenticated state does not expose previous user data`() = runTest(dispatcher) {
        viewModel.setOwnerUid("a")
        viewModel.setLiked(song(1), true)
        advanceUntilIdle()

        viewModel.setOwnerUid(null)
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isAuthenticated)
        assertTrue(viewModel.uiState.value.likedSongs.isEmpty())
    }

    @Test
    fun `owner change resets previous state and observes new owner`() = runTest(dispatcher) {
        repo.likedSongs["b"] = kotlinx.coroutines.flow.MutableStateFlow(listOf(song(2)))
        repo.likedIds["b"] = kotlinx.coroutines.flow.MutableStateFlow(listOf(2L))

        viewModel.setOwnerUid("a")
        viewModel.setLiked(song(1), true)
        advanceUntilIdle()
        viewModel.setOwnerUid("b")
        advanceUntilIdle()

        assertEquals(listOf(2L), viewModel.uiState.value.likedSongs.map { it.id })
    }

    @Test
    fun `like and unlike update state`() = runTest(dispatcher) {
        viewModel.setOwnerUid("a")
        viewModel.setLiked(song(1), true)
        advanceUntilIdle()
        assertEquals(setOf(1L), viewModel.uiState.value.likedSongIds)

        viewModel.setLiked(song(1), false)
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.likedSongIds.isEmpty())
    }

    @Test
    fun `create playlist validates name and success updates list`() = runTest(dispatcher) {
        viewModel.setOwnerUid("a")
        advanceUntilIdle()
        viewModel.createPlaylist("   ")
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.errorMessage!!.contains("blank"))

        viewModel.clearMessages()
        viewModel.createPlaylist("Road Trip")
        advanceUntilIdle()
        assertEquals("Road Trip", viewModel.uiState.value.playlists.single().name)
    }

    @Test
    fun `adding a song prevents duplicate membership`() = runTest(dispatcher) {
        viewModel.setOwnerUid("a")
        advanceUntilIdle()
        viewModel.createPlaylist("Mix")
        advanceUntilIdle()
        val playlistId = viewModel.uiState.value.playlists.single().playlistId

        viewModel.addSongToPlaylist(playlistId, song(1))
        viewModel.addSongToPlaylist(playlistId, song(1))
        viewModel.selectPlaylist(playlistId)
        advanceUntilIdle()

        assertEquals(listOf(1L), viewModel.uiState.value.selectedPlaylistSongs.map { it.id })
    }

    @Test
    fun `brief accidental playback is not recorded but qualified playback records once`() = runTest(dispatcher) {
        viewModel.setOwnerUid("a")
        val playbackSong = song(1, duration = 120_000)

        viewModel.onPlaybackProgress(
            PlaybackUiState(
                currentSong = playbackSong,
                currentMediaId = "1",
                isPlaying = true,
                positionMs = 5_000,
                durationMs = 120_000
            )
        )
        advanceUntilIdle()
        assertTrue(repo.recordedHistory.isEmpty())

        viewModel.onPlaybackProgress(
            PlaybackUiState(
                currentSong = playbackSong,
                currentMediaId = "1",
                isPlaying = true,
                positionMs = 31_000,
                durationMs = 120_000
            )
        )
        viewModel.onPlaybackProgress(
            PlaybackUiState(
                currentSong = playbackSong,
                currentMediaId = "1",
                isPlaying = true,
                positionMs = 32_000,
                durationMs = 120_000
            )
        )
        advanceUntilIdle()

        assertEquals(1, repo.recordedHistory.size)
    }

    @Test
    fun `clear history updates state`() = runTest(dispatcher) {
        viewModel.setOwnerUid("a")
        viewModel.onPlaybackProgress(
            PlaybackUiState(
                currentSong = song(1),
                currentMediaId = "1",
                isPlaying = true,
                positionMs = 31_000,
                durationMs = 120_000
            )
        )
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.recentSongs.isNotEmpty())

        viewModel.clearHistory()
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.recentSongs.isEmpty())
    }

    private fun song(id: Long, duration: Long = 120_000L) = Song(
        id = id,
        title = "Song $id",
        artist = if (id == 2L) "Other" else "Artist",
        album = "Album",
        albumId = id,
        durationMs = duration,
        contentUri = "content://songs/$id",
        artworkUri = null,
        dateAdded = id,
        trackNumber = null,
        year = null,
        mimeType = "audio/mpeg"
    )
}
