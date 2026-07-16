package com.example.rockstar.ui.screens.player

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.rockstar.R
import com.example.rockstar.ui.components.ArtworkImage
import com.example.rockstar.ui.components.EmptyState
import com.example.rockstar.ui.components.PlaybackControls
import com.example.rockstar.ui.theme.RockstarBackground
import com.example.rockstar.ui.theme.RockstarTextPrimary
import com.example.rockstar.viewmodel.PlaybackUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NowPlayingScreen(
    playbackState: PlaybackUiState,
    onBack: () -> Unit,
    onTogglePlayPause: () -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onSeek: (Long) -> Unit,
    onShuffle: () -> Unit,
    onRepeat: () -> Unit,
    onSkipToQueueItem: (Int) -> Unit,
    onRemoveQueueItem: (Int) -> Unit,
    onClearQueue: () -> Unit
) {
    Scaffold(
        containerColor = RockstarBackground,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.now_playing_title)) },
                navigationIcon = { IconButton(onClick = onBack) { Text("‹") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = RockstarBackground)
            )
        }
    ) { padding ->
        val song = playbackState.currentSong
        if (song == null) {
            EmptyState(
                icon = Icons.Default.MusicNote,
                title = stringResource(R.string.now_playing_empty_title),
                message = stringResource(R.string.now_playing_empty_message),
                modifier = Modifier.padding(padding)
            )
            return@Scaffold
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            ArtworkImage(artworkUri = song.artworkUri, size = 260.dp)
            Spacer(modifier = Modifier.height(28.dp))
            Text(song.title, color = RockstarTextPrimary, textAlign = TextAlign.Center)
            Text(song.artist, textAlign = TextAlign.Center)
            playbackState.errorMessage?.let { Text(it, textAlign = TextAlign.Center) }
            Spacer(modifier = Modifier.height(24.dp))
            PlaybackControls(
                isPlaying = playbackState.isPlaying,
                canSkipPrevious = playbackState.canSkipPrevious,
                canSkipNext = playbackState.canSkipNext,
                shuffleEnabled = playbackState.shuffleEnabled,
                repeatMode = playbackState.repeatMode,
                onShuffle = onShuffle,
                onPrevious = onPrevious,
                onTogglePlayPause = onTogglePlayPause,
                onNext = onNext,
                onRepeat = onRepeat
            )
        }
    }
}
