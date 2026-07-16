package com.example.rockstar.ui.components

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.rockstar.model.Song
import com.example.rockstar.ui.theme.RockstarTheme
import com.example.rockstar.viewmodel.PlaybackUiState
import org.junit.Rule
import org.junit.Test

class MiniPlayerTest {
    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun hiddenWhenNoActiveMediaExists() {
        rule.setContent { RockstarTheme { MiniPlayer(PlaybackUiState(), {}, {}, {}) } }
        rule.onAllNodesWithText("Song").assertCountEquals(0)
    }

    @Test
    fun visibleWhenSongIsActiveAndDelegatesActions() {
        var opened = false
        var toggled = false
        rule.setContent {
            RockstarTheme {
                MiniPlayer(
                    state = PlaybackUiState(currentSong = song(), currentMediaId = "1", isPlaying = true),
                    onTogglePlayPause = { toggled = true },
                    onNext = {},
                    onOpenNowPlaying = { opened = true }
                )
            }
        }
        rule.onNodeWithText("Song").assertIsDisplayed()
        rule.onNodeWithText("Artist").assertIsDisplayed()
        rule.onNodeWithContentDescription("Pause").performClick()
        assert(toggled)
        rule.onNodeWithText("Song").performClick()
        assert(opened)
    }

    private fun song() = Song(1, "Song", "Artist", "Album", 1, 1, "content://songs/1", null, 0, null, null, null)
}
