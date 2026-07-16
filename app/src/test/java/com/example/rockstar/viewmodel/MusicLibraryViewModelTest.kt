package com.example.rockstar.viewmodel

import com.example.rockstar.model.Song
import com.example.rockstar.repo.FakeMusicRepository
import kotlinx.coroutines.CompletableDeferred
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
class MusicLibraryViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private lateinit var repo: FakeMusicRepository
    private lateinit var viewModel: MusicLibraryViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        repo = FakeMusicRepository()
        viewModel = MusicLibraryViewModel(repo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `permission granted triggers loading`() = runTest(dispatcher) {
        repo.songsResult = Result.success(sampleSongs())

        viewModel.onPermissionStateChanged(AudioPermissionState.Granted)
        advanceUntilIdle()

        assertEquals(1, repo.loadCallCount)
        assertTrue(viewModel.uiState.value.hasLoaded)
        assertEquals(3, viewModel.uiState.value.songs.size)
    }

    @Test
    fun `permission denied does not query repository`() = runTest(dispatcher) {
        viewModel.onPermissionStateChanged(AudioPermissionState.Denied)
        advanceUntilIdle()

        assertEquals(0, repo.loadCallCount)
        assertFalse(viewModel.uiState.value.hasLoaded)
    }

    @Test
    fun `successful loading exposes songs`() = runTest(dispatcher) {
        repo.songsResult = Result.success(sampleSongs())

        viewModel.onPermissionStateChanged(AudioPermissionState.Granted)
        advanceUntilIdle()

        assertEquals(listOf("Alpha", "Beta", "Gamma"), viewModel.uiState.value.displayedSongs.map { it.title })
    }

    @Test
    fun `empty repository exposes authentic empty state`() = runTest(dispatcher) {
        repo.songsResult = Result.success(emptyList())

        viewModel.onPermissionStateChanged(AudioPermissionState.Granted)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.hasLoaded)
        assertTrue(viewModel.uiState.value.songs.isEmpty())
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `repository failure clears loading and exposes error`() = runTest(dispatcher) {
        repo.songsResult = Result.failure(IllegalStateException("broken"))

        viewModel.onPermissionStateChanged(AudioPermissionState.Granted)
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        assertTrue(viewModel.uiState.value.errorMessage!!.contains("could not scan"))
    }

    @Test
    fun `duplicate load is prevented`() = runTest(dispatcher) {
        repo.loadGate = CompletableDeferred()
        repo.songsResult = Result.success(sampleSongs())

        viewModel.onPermissionStateChanged(AudioPermissionState.Granted)
        viewModel.loadLibraryIfNeeded()
        advanceUntilIdle()

        assertEquals(1, repo.loadCallCount)
        repo.loadGate!!.complete(Unit)
        advanceUntilIdle()
    }

    @Test
    fun `refresh reloads the library`() = runTest(dispatcher) {
        repo.songsResult = Result.success(sampleSongs().take(1))
        viewModel.onPermissionStateChanged(AudioPermissionState.Granted)
        advanceUntilIdle()
        repo.refreshResult = Result.success(sampleSongs())

        viewModel.refreshLibrary()
        advanceUntilIdle()

        assertEquals(1, repo.refreshCallCount)
        assertEquals(3, viewModel.uiState.value.songs.size)
    }

    @Test
    fun `search matches title artist and album case insensitively`() = runTest(dispatcher) {
        repo.songsResult = Result.success(sampleSongs())
        viewModel.onPermissionStateChanged(AudioPermissionState.Granted)
        advanceUntilIdle()

        viewModel.updateSearchQuery("alp")
        assertEquals(listOf("Alpha"), viewModel.uiState.value.displayedSongs.map { it.title })

        viewModel.updateSearchQuery("artist two")
        assertEquals(listOf("Beta"), viewModel.uiState.value.displayedSongs.map { it.title })

        viewModel.updateSearchQuery("rock")
        assertEquals(listOf("Alpha", "Gamma"), viewModel.uiState.value.displayedSongs.map { it.title })

        viewModel.updateSearchQuery("")
        assertEquals(3, viewModel.uiState.value.displayedSongs.size)
    }

    @Test
    fun `sorting works by title artist album date and direction`() = runTest(dispatcher) {
        repo.songsResult = Result.success(sampleSongs())
        viewModel.onPermissionStateChanged(AudioPermissionState.Granted)
        advanceUntilIdle()

        viewModel.updateSortOrder(LibrarySortOrder.Artist)
        assertEquals(listOf("Alpha", "Gamma", "Beta"), viewModel.uiState.value.displayedSongs.map { it.title })

        viewModel.updateSortOrder(LibrarySortOrder.Album)
        assertEquals(listOf("Beta", "Alpha", "Gamma"), viewModel.uiState.value.displayedSongs.map { it.title })

        viewModel.updateSortOrder(LibrarySortOrder.DateAdded)
        assertEquals(listOf("Gamma", "Beta", "Alpha"), viewModel.uiState.value.displayedSongs.map { it.title })

        viewModel.toggleSortDirection()
        assertEquals(listOf("Alpha", "Beta", "Gamma"), viewModel.uiState.value.displayedSongs.map { it.title })

        viewModel.updateSortOrder(LibrarySortOrder.Title)
        assertEquals(listOf("Alpha", "Beta", "Gamma"), viewModel.uiState.value.displayedSongs.map { it.title })
    }

    @Test
    fun `album and artist grouping works`() = runTest(dispatcher) {
        repo.songsResult = Result.success(sampleSongs())

        viewModel.onPermissionStateChanged(AudioPermissionState.Granted)
        advanceUntilIdle()

        assertEquals(2, viewModel.uiState.value.albums.size)
        assertEquals(2, viewModel.uiState.value.artists.size)
        assertEquals(2, viewModel.uiState.value.artists.first { it.name == "Artist One" }.songCount)
    }

    private fun sampleSongs(): List<Song> = listOf(
        song(id = 1, title = "Alpha", artist = "Artist One", album = "Rock", albumId = 10, dateAdded = 100),
        song(id = 2, title = "Beta", artist = "Artist Two", album = "Acoustic", albumId = 20, dateAdded = 200),
        song(id = 3, title = "Gamma", artist = "Artist One", album = "Rock", albumId = 10, dateAdded = 300)
    )

    private fun song(
        id: Long,
        title: String,
        artist: String,
        album: String,
        albumId: Long,
        dateAdded: Long
    ) = Song(
        id = id,
        title = title,
        artist = artist,
        album = album,
        albumId = albumId,
        durationMs = 125_000L + id,
        contentUri = "content://songs/$id",
        artworkUri = "content://albums/$albumId",
        dateAdded = dateAdded,
        trackNumber = id.toInt(),
        year = 2024,
        mimeType = "audio/mpeg"
    )
}
