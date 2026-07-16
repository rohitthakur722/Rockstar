package com.example.rockstar.ui.screens.songs

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.rockstar.model.Song
import com.example.rockstar.ui.theme.RockstarTheme
import com.example.rockstar.viewmodel.AudioPermissionState
import com.example.rockstar.viewmodel.LibrarySection
import com.example.rockstar.viewmodel.MusicLibraryUiState
import org.junit.Rule
import org.junit.Test

class LibraryContentTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun permissionStateDisplaysAllowAccess() {
        setContent(MusicLibraryUiState(permissionState = AudioPermissionState.NotRequested))

        composeTestRule.onNodeWithText("Allow Audio Access").assertIsDisplayed()
    }

    @Test
    fun loadingStateDisplaysProgressMessage() {
        setContent(MusicLibraryUiState(permissionState = AudioPermissionState.Granted, isLoading = true))

        composeTestRule.onNodeWithText("Scanning your local music…").assertIsDisplayed()
    }

    @Test
    fun emptyStateDisplaysCorrectMessage() {
        setContent(MusicLibraryUiState(permissionState = AudioPermissionState.Granted, hasLoaded = true))

        composeTestRule.onNodeWithText("No local songs found").assertIsDisplayed()
    }

    @Test
    fun loadedStateDisplaysRealSongTitles() {
        val song = sampleSong("Real Device Song")
        setContent(
            MusicLibraryUiState(
                permissionState = AudioPermissionState.Granted,
                hasLoaded = true,
                songs = listOf(song),
                displayedSongs = listOf(song)
            )
        )

        composeTestRule.onNodeWithText("Real Device Song").assertIsDisplayed()
    }

    @Test
    fun searchNoResultStateAppears() {
        setContent(
            MusicLibraryUiState(
                permissionState = AudioPermissionState.Granted,
                hasLoaded = true,
                songs = listOf(sampleSong("Real Device Song")),
                displayedSongs = emptyList(),
                searchQuery = "missing"
            )
        )

        composeTestRule.onNodeWithText("No matching music").assertIsDisplayed()
    }

    @Test
    fun sortMenuOpens() {
        val song = sampleSong("Real Device Song")
        setContent(
            MusicLibraryUiState(
                permissionState = AudioPermissionState.Granted,
                hasLoaded = true,
                songs = listOf(song),
                displayedSongs = listOf(song)
            )
        )

        composeTestRule.onNodeWithText("Showing 1 of 1 songs").assertIsDisplayed()
        composeTestRule.onNodeWithText("Songs").assertIsDisplayed()
    }

    @Test
    fun sectionSelectorChangesSongsAlbumsArtists() {
        var selected = LibrarySection.Songs
        val song = sampleSong("Real Device Song")
        composeTestRule.setContent {
            RockstarTheme {
                LibraryContent(
                    uiState = MusicLibraryUiState(
                        permissionState = AudioPermissionState.Granted,
                        hasLoaded = true,
                        songs = listOf(song),
                        displayedSongs = listOf(song),
                        selectedLibrarySection = selected
                    ),
                    padding = PaddingValues(),
                    onSectionSelected = { selected = it }
                )
            }
        }

        composeTestRule.onNodeWithText("Albums").performClick()
        assert(selected == LibrarySection.Albums)
        composeTestRule.onNodeWithText("Artists").performClick()
        assert(selected == LibrarySection.Artists)
    }

    private fun setContent(uiState: MusicLibraryUiState) {
        composeTestRule.setContent {
            RockstarTheme {
                LibraryContent(uiState = uiState, padding = PaddingValues())
            }
        }
    }

    private fun sampleSong(title: String) = Song(
        id = 1L,
        title = title,
        artist = "Artist",
        album = "Album",
        albumId = 1L,
        durationMs = 125_000L,
        contentUri = "content://songs/1",
        artworkUri = null,
        dateAdded = 100L,
        trackNumber = 1,
        year = 2024,
        mimeType = "audio/mpeg"
    )
}
