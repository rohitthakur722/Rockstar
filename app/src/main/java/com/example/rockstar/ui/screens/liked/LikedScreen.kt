package com.example.rockstar.ui.screens.liked

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.rockstar.R
import com.example.rockstar.navigation.RockstarDestination
import com.example.rockstar.ui.components.EmptyState
import com.example.rockstar.ui.components.RockstarAppScaffold
import com.example.rockstar.viewmodel.PlaybackUiState

@Composable
fun LikedScreen(
    currentRoute: String,
    onTabSelected: (RockstarDestination) -> Unit,
    playbackState: PlaybackUiState,
    onMiniPlayerClick: () -> Unit,
    onMiniPlayerPlayPause: () -> Unit,
    onMiniPlayerNext: () -> Unit
) {
    RockstarAppScaffold(
        title = stringResource(R.string.nav_liked),
        currentRoute = currentRoute,
        onTabSelected = onTabSelected,
        playbackState = playbackState,
        onMiniPlayerClick = onMiniPlayerClick,
        onMiniPlayerPlayPause = onMiniPlayerPlayPause,
        onMiniPlayerNext = onMiniPlayerNext
    ) { padding ->
        EmptyState(
            icon = Icons.Default.FavoriteBorder,
            title = stringResource(R.string.empty_liked_title),
            message = stringResource(R.string.empty_liked_message),
            modifier = Modifier.padding(padding)
        )
    }
}
