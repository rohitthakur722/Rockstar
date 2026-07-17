package com.example.rockstar.ui.screens.liked

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.rockstar.R
import com.example.rockstar.model.Song
import com.example.rockstar.navigation.RockstarDestination
import com.example.rockstar.ui.components.EmptyState
import com.example.rockstar.ui.components.RockstarAppScaffold
import com.example.rockstar.ui.components.SongRow
import com.example.rockstar.viewmodel.PersonalLibraryViewModel
import com.example.rockstar.viewmodel.PlaybackUiState

@Composable
fun LikedScreen(
    currentRoute: String,
    onTabSelected: (RockstarDestination) -> Unit,
    viewModel: PersonalLibraryViewModel,
    playbackState: PlaybackUiState,
    onSongSelected: (Song, List<Song>) -> Unit,
    onMiniPlayerClick: () -> Unit,
    onMiniPlayerPlayPause: () -> Unit,
    onMiniPlayerNext: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.errorMessage, uiState.successMessage) {
        if (uiState.errorMessage != null || uiState.successMessage != null) {
            viewModel.clearMessages()
        }
    }

    RockstarAppScaffold(
        title = stringResource(R.string.nav_liked),
        currentRoute = currentRoute,
        onTabSelected = onTabSelected,
        playbackState = playbackState,
        onMiniPlayerClick = onMiniPlayerClick,
        onMiniPlayerPlayPause = onMiniPlayerPlayPause,
        onMiniPlayerNext = onMiniPlayerNext
    ) { padding ->
        when {
            !uiState.isAuthenticated -> EmptyState(
                icon = Icons.Default.FavoriteBorder,
                title = stringResource(R.string.liked_unauthenticated_title),
                message = stringResource(R.string.liked_unauthenticated_message),
                modifier = Modifier.padding(padding)
            )

            uiState.isLoading && uiState.likedSongs.isEmpty() -> Column(
                modifier = Modifier.fillMaxSize().padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
            }

            uiState.likedSongs.isEmpty() -> EmptyState(
                icon = Icons.Default.Favorite,
                title = stringResource(R.string.empty_liked_title),
                message = stringResource(R.string.empty_liked_message),
                modifier = Modifier.padding(padding)
            )

            else -> LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(uiState.likedSongs, key = { it.id }) { song ->
                    SongRow(
                        song = song,
                        isActive = playbackState.currentMediaId == song.id.toString(),
                        isPlaying = playbackState.isPlaying,
                        isLiked = true,
                        isLikeEnabled = uiState.activeSongId != song.id,
                        onClick = { onSongSelected(song, uiState.likedSongs) },
                        onLikeClick = { viewModel.setLiked(song, false) }
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }
}
