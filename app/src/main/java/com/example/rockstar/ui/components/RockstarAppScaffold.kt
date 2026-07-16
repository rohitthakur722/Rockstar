package com.example.rockstar.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.rockstar.navigation.RockstarDestination
import com.example.rockstar.ui.theme.RockstarBackground
import com.example.rockstar.viewmodel.PlaybackUiState

/**
 * Shared app shell for the five bottom-navigation tab destinations.
 */
@Composable
fun RockstarAppScaffold(
    title: String,
    currentRoute: String,
    onTabSelected: (RockstarDestination) -> Unit,
    modifier: Modifier = Modifier,
    showBrandMark: Boolean = false,
    trailingIcon: ImageVector? = null,
    trailingContentDescription: String? = null,
    onTrailingClick: () -> Unit = {},
    playbackState: PlaybackUiState? = null,
    onMiniPlayerClick: () -> Unit = {},
    onMiniPlayerPlayPause: () -> Unit = {},
    onMiniPlayerNext: () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        modifier = modifier,
        containerColor = RockstarBackground,
        topBar = {
            RockstarTopBar(
                title = title,
                showBrandMark = showBrandMark,
                trailingIcon = trailingIcon,
                trailingContentDescription = trailingContentDescription,
                onTrailingClick = onTrailingClick
            )
        },
        bottomBar = {
            Column {
                if (playbackState?.hasActiveMedia == true) {
                    MiniPlayer(
                        state = playbackState,
                        onTogglePlayPause = onMiniPlayerPlayPause,
                        onNext = onMiniPlayerNext,
                        onOpenNowPlaying = onMiniPlayerClick
                    )
                }
                RockstarBottomBar(currentRoute = currentRoute, onTabSelected = onTabSelected)
            }
        },
        content = content
    )
}
