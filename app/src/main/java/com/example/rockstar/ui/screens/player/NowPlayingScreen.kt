package com.example.rockstar.ui.screens.player

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.rockstar.R
import com.example.rockstar.ui.components.ArtworkImage
import com.example.rockstar.ui.components.EmptyState
import com.example.rockstar.ui.components.PlaybackControls
import com.example.rockstar.ui.components.PlaybackProgressSlider
import com.example.rockstar.ui.components.PlaybackQueueSheet
import com.example.rockstar.ui.theme.RockstarBackground
import com.example.rockstar.ui.theme.RockstarTextPrimary
import com.example.rockstar.ui.theme.RockstarTextSecondary
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
    var showQueue by remember { mutableStateOf(false) }
    Scaffold(
        containerColor = RockstarBackground,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.now_playing_title), color = RockstarTextPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.cd_back))
                    }
                },
                actions = {
                    IconButton(onClick = { showQueue = true }, enabled = playbackState.queue.isNotEmpty()) {
                        Icon(
                            Icons.AutoMirrored.Filled.QueueMusic,
                            contentDescription = stringResource(R.string.cd_queue)
                        )
                    }
                },
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
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            ArtworkImage(artworkUri = song.artworkUri, size = 260.dp)
            Spacer(modifier = Modifier.height(28.dp))
            Text(
                song.title,
                color = RockstarTextPrimary,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                song.artist,
                color = RockstarTextSecondary,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                song.album,
                color = RockstarTextSecondary,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            playbackState.errorMessage?.let {
                Spacer(modifier = Modifier.height(12.dp))
                Text(it, color = RockstarTextSecondary, textAlign = TextAlign.Center)
            }
            Spacer(modifier = Modifier.height(24.dp))
            PlaybackProgressSlider(
                positionMs = playbackState.positionMs,
                durationMs = playbackState.durationMs,
                canSeek = playbackState.canSeek,
                onSeek = onSeek,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(20.dp))
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

    if (showQueue) {
        PlaybackQueueSheet(
            queue = playbackState.queue,
            currentIndex = playbackState.currentIndex,
            onDismiss = { showQueue = false },
            onSkipToQueueItem = onSkipToQueueItem,
            onRemoveQueueItem = onRemoveQueueItem,
            onClearQueue = {
                showQueue = false
                onClearQueue()
            }
        )
    }
}
