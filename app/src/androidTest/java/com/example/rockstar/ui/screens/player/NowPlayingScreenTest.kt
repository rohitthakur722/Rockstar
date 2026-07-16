package com.example.rockstar.ui.screens.player

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.rockstar.model.Song
import com.example.rockstar.ui.theme.RockstarTheme
import com.example.rockstar.viewmodel.PlaybackUiState
import org.junit.Rule
import org.junit.Test

class NowPlayingScreenTest {
    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun emptyStateDisplaysWithoutCurrentMedia() {
        setContent(PlaybackUiState())
        rule.onNodeWithText("Nothing playing").assertIsDisplayed()
    }

    @Test
    fun displaysCurrentSongAndDelegatesControls() {
        var toggled = false
        var next = false
        setContent(
            PlaybackUiState(
                currentSong = song(),
                currentMediaId = "1",
                queue = listOf(song()),
                currentIndex = 0,
                canSkipNext = true
            ), onToggle = { toggled = true }, onNext = { next = true })
        rule.onNodeWithText("Song").assertIsDisplayed()
        rule.onNodeWithContentDescription("Play").performClick()
        assert(toggled)
        rule.onNodeWithContentDescription("Next").performClick()
        assert(next)
    }

    @Test
    fun queueActionOpensQueue() {
        val song = song()
        setContent(PlaybackUiState(currentSong = song, currentMediaId = "1", queue = listOf(song), currentIndex = 0))
        rule.onNodeWithContentDescription("Queue").performClick()
        rule.onNodeWithText("Queue").assertIsDisplayed()
    }

    private fun setContent(state: PlaybackUiState, onToggle: () -> Unit = {}, onNext: () -> Unit = {}) {
        rule.setContent {
            RockstarTheme {
                NowPlayingScreen(
                    playbackState = state,
                    onBack = {},
                    onTogglePlayPause = onToggle,
                    onPrevious = {},
                    onNext = onNext,
                    onSeek = {},
                    onShuffle = {},
                    onRepeat = {},
                    onSkipToQueueItem = {},
                    onRemoveQueueItem = {},
                    onClearQueue = {}
                )
            }
        }
    }

    private fun song() = Song(1, "Song", "Artist", "Album", 1, 120_000, "content://songs/1", null, 0, null, null, null)
}
