package com.example.rockstar.ui.screens.playlists

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.rockstar.R
import com.example.rockstar.model.Song
import com.example.rockstar.ui.components.EmptyState
import com.example.rockstar.ui.components.SongRow
import com.example.rockstar.ui.theme.RockstarBackground
import com.example.rockstar.ui.theme.RockstarTextPrimary
import com.example.rockstar.viewmodel.PersonalLibraryViewModel
import com.example.rockstar.viewmodel.PlaybackUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistDetailScreen(
    playlistId: Long,
    viewModel: PersonalLibraryViewModel,
    playbackState: PlaybackUiState,
    onBack: () -> Unit,
    onPlayQueue: (Song, List<Song>) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val playlist = uiState.playlists.firstOrNull { it.playlistId == playlistId }
    val songs = uiState.selectedPlaylistSongs
    var removeTarget by remember { mutableStateOf<Song?>(null) }

    DisposableEffect(playlistId) {
        viewModel.selectPlaylist(playlistId)
        onDispose { viewModel.selectPlaylist(null) }
    }

    Scaffold(
        containerColor = RockstarBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        playlist?.name ?: stringResource(R.string.playlist_detail_title),
                        color = RockstarTextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.cd_back))
                    }
                },
                actions = {
                    IconButton(
                        onClick = { songs.firstOrNull()?.let { onPlayQueue(it, songs) } },
                        enabled = songs.isNotEmpty()
                    ) {
                        Icon(
                            Icons.Default.PlayArrow,
                            contentDescription = stringResource(R.string.action_play_playlist)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = RockstarBackground)
            )
        }
    ) { padding ->
        when {
            playlist == null -> EmptyState(
                icon = Icons.Default.LibraryMusic,
                title = stringResource(R.string.playlist_not_found_title),
                message = stringResource(R.string.playlist_not_found_message),
                modifier = Modifier.padding(padding)
            )

            songs.isEmpty() -> EmptyState(
                icon = Icons.Default.LibraryMusic,
                title = stringResource(R.string.playlist_empty_title),
                message = stringResource(R.string.playlist_empty_message),
                modifier = Modifier.padding(padding)
            )

            else -> Column(modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(onClick = { songs.firstOrNull()?.let { onPlayQueue(it, songs) } }) {
                        Text(stringResource(R.string.action_play_playlist))
                    }
                }
                LazyColumn(contentPadding = PaddingValues(bottom = 24.dp)) {
                    items(songs, key = { it.id }) { song ->
                        SongRow(
                            song = song,
                            isActive = playbackState.currentMediaId == song.id.toString(),
                            isPlaying = playbackState.isPlaying,
                            onClick = { onPlayQueue(song, songs) }
                        )
                        IconButton(onClick = { removeTarget = song }) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = stringResource(R.string.action_remove_from_playlist)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
            }
        }
    }

    removeTarget?.let { song ->
        AlertDialog(
            onDismissRequest = { removeTarget = null },
            title = { Text(stringResource(R.string.remove_from_playlist_title)) },
            text = { Text(stringResource(R.string.remove_from_playlist_message, song.title)) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.removeSongFromPlaylist(playlistId, song.id)
                    removeTarget = null
                }) { Text(stringResource(R.string.action_remove)) }
            },
            dismissButton = {
                TextButton(onClick = {
                    removeTarget = null
                }) { Text(stringResource(R.string.action_cancel)) }
            }
        )
    }
}
