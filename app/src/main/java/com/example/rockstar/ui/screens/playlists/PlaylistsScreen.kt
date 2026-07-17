package com.example.rockstar.ui.screens.playlists

import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.rockstar.R
import com.example.rockstar.data.local.entity.PlaylistEntity
import com.example.rockstar.navigation.RockstarDestination
import com.example.rockstar.ui.components.EmptyState
import com.example.rockstar.ui.components.RockstarAppScaffold
import com.example.rockstar.ui.theme.RockstarAccent
import com.example.rockstar.ui.theme.RockstarTextPrimary
import com.example.rockstar.ui.theme.RockstarTextSecondary
import com.example.rockstar.viewmodel.PersonalLibraryViewModel
import com.example.rockstar.viewmodel.PlaybackUiState

@Composable
fun PlaylistsScreen(
    currentRoute: String,
    onTabSelected: (RockstarDestination) -> Unit,
    viewModel: PersonalLibraryViewModel,
    playbackState: PlaybackUiState,
    onPlaylistClick: (Long) -> Unit,
    onMiniPlayerClick: () -> Unit,
    onMiniPlayerPlayPause: () -> Unit,
    onMiniPlayerNext: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var createDialog by remember { mutableStateOf(false) }
    var renameTarget by remember { mutableStateOf<PlaylistEntity?>(null) }
    var deleteTarget by remember { mutableStateOf<PlaylistEntity?>(null) }

    RockstarAppScaffold(
        title = stringResource(R.string.nav_playlists),
        currentRoute = currentRoute,
        onTabSelected = onTabSelected,
        trailingIcon = Icons.Default.Add,
        trailingContentDescription = stringResource(R.string.action_create_playlist),
        onTrailingClick = { createDialog = true },
        playbackState = playbackState,
        onMiniPlayerClick = onMiniPlayerClick,
        onMiniPlayerPlayPause = onMiniPlayerPlayPause,
        onMiniPlayerNext = onMiniPlayerNext
    ) { padding ->
        when {
            !uiState.isAuthenticated -> EmptyState(
                icon = Icons.Default.LibraryMusic,
                title = stringResource(R.string.playlists_unauthenticated_title),
                message = stringResource(R.string.playlists_unauthenticated_message),
                modifier = Modifier.padding(padding)
            )

            uiState.playlists.isEmpty() -> EmptyState(
                icon = Icons.Default.LibraryMusic,
                title = stringResource(R.string.empty_playlists_title),
                message = stringResource(R.string.empty_playlists_message),
                modifier = Modifier.padding(padding)
            )

            else -> LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp),
                contentPadding = PaddingValues(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.playlists, key = { it.playlistId }) { playlist ->
                    PlaylistRow(
                        playlist = playlist,
                        onClick = { onPlaylistClick(playlist.playlistId) },
                        onRename = { renameTarget = playlist },
                        onDelete = { deleteTarget = playlist }
                    )
                }
            }
        }
    }

    if (createDialog) {
        PlaylistNameDialog(
            title = stringResource(R.string.action_create_playlist),
            confirmLabel = stringResource(R.string.action_create),
            onDismiss = { createDialog = false },
            onConfirm = { name ->
                viewModel.createPlaylist(name)
                createDialog = false
            }
        )
    }
    renameTarget?.let { playlist ->
        PlaylistNameDialog(
            title = stringResource(R.string.action_rename_playlist),
            initialValue = playlist.name,
            confirmLabel = stringResource(R.string.action_save),
            onDismiss = { renameTarget = null },
            onConfirm = { name ->
                viewModel.renamePlaylist(playlist.playlistId, name)
                renameTarget = null
            }
        )
    }
    deleteTarget?.let { playlist ->
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            title = { Text(stringResource(R.string.delete_playlist_title)) },
            text = { Text(stringResource(R.string.delete_playlist_message, playlist.name)) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deletePlaylist(playlist.playlistId)
                    deleteTarget = null
                }) { Text(stringResource(R.string.action_delete)) }
            },
            dismissButton = {
                TextButton(onClick = { deleteTarget = null }) { Text(stringResource(R.string.action_cancel)) }
            }
        )
    }
}

@Composable
private fun PlaylistRow(
    playlist: PlaylistEntity,
    onClick: () -> Unit,
    onRename: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.LibraryMusic, contentDescription = null, tint = RockstarAccent)
        Column(modifier = Modifier.weight(1f).padding(horizontal = 16.dp)) {
            Text(playlist.name, color = RockstarTextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(
                stringResource(R.string.playlist_updated),
                color = RockstarTextSecondary,
                style = MaterialTheme.typography.bodySmall
            )
        }
        IconButton(onClick = onRename) {
            Icon(Icons.Default.Edit, contentDescription = stringResource(R.string.action_rename_playlist))
        }
        IconButton(onClick = onDelete) {
            Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.action_delete_playlist))
        }
    }
}

@Composable
fun PlaylistNameDialog(
    title: String,
    initialValue: String = "",
    confirmLabel: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var name by remember(initialValue) { mutableStateOf(initialValue) }
    val trimmed = name.trim()
    val isValid = trimmed.isNotBlank() && trimmed.length <= 80
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.playlist_name_label)) },
                    isError = name.isNotEmpty() && !isValid,
                    singleLine = true
                )
                if (name.isNotEmpty() && !isValid) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(stringResource(R.string.playlist_name_error), color = MaterialTheme.colorScheme.error)
                }
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(trimmed) }, enabled = isValid) { Text(confirmLabel) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.action_cancel)) } }
    )
}
