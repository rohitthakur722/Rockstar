package com.example.rockstar.ui.screens.songs

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.rockstar.R
import com.example.rockstar.model.Song
import com.example.rockstar.navigation.RockstarDestination
import com.example.rockstar.ui.components.AlbumCard
import com.example.rockstar.ui.components.ArtistRow
import com.example.rockstar.ui.components.EmptyState
import com.example.rockstar.ui.components.LibraryPermissionCard
import com.example.rockstar.ui.components.LibrarySectionSelector
import com.example.rockstar.ui.components.LibrarySortMenu
import com.example.rockstar.ui.components.MusicSearchBar
import com.example.rockstar.ui.components.RockstarAppScaffold
import com.example.rockstar.ui.components.SongRow
import com.example.rockstar.ui.theme.RockstarSpacing
import com.example.rockstar.ui.theme.RockstarTextPrimary
import com.example.rockstar.ui.theme.RockstarTextSecondary
import com.example.rockstar.viewmodel.AudioPermissionState
import com.example.rockstar.viewmodel.LibrarySection
import com.example.rockstar.viewmodel.MusicLibraryUiState
import com.example.rockstar.viewmodel.MusicLibraryViewModel
import com.example.rockstar.viewmodel.PlaybackUiState

@Composable
fun AllSongsScreen(
    currentRoute: String,
    onTabSelected: (RockstarDestination) -> Unit,
    viewModel: MusicLibraryViewModel,
    playbackState: PlaybackUiState,
    likedSongIds: Set<Long> = emptySet(),
    likeActionEnabled: (Song) -> Boolean = { true },
    onToggleLiked: (Song) -> Unit = {},
    onSongSelected: (Song, List<Song>) -> Unit,
    onMiniPlayerClick: () -> Unit,
    onMiniPlayerPlayPause: () -> Unit,
    onMiniPlayerNext: () -> Unit
) {
    val context = LocalContext.current
    val activity = context.findActivity()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var hasRequestedPermission by rememberSaveable { mutableStateOf(false) }
    val permission = rememberAudioPermission()

    fun syncPermissionState() {
        val granted = context.hasPermission(permission)
        val state = when {
            granted -> AudioPermissionState.Granted
            !hasRequestedPermission -> AudioPermissionState.NotRequested
            activity?.shouldShowRequestPermissionRationale(permission) == true -> AudioPermissionState.DeniedWithRationale
            else -> AudioPermissionState.PermanentlyDenied
        }
        viewModel.onPermissionStateChanged(state)
    }

    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        hasRequestedPermission = true
        val state = when {
            granted -> AudioPermissionState.Granted
            activity?.shouldShowRequestPermissionRationale(permission) == true -> AudioPermissionState.DeniedWithRationale
            else -> AudioPermissionState.PermanentlyDenied
        }
        viewModel.onPermissionStateChanged(state)
    }

    LaunchedEffect(permission) { syncPermissionState() }
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner, permission, hasRequestedPermission) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) syncPermissionState()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    RockstarAppScaffold(
        title = stringResource(R.string.nav_all_songs),
        currentRoute = currentRoute,
        onTabSelected = onTabSelected,
        trailingIcon = Icons.Default.Refresh,
        trailingContentDescription = stringResource(R.string.cd_refresh_library),
        onTrailingClick = viewModel::refreshLibrary,
        playbackState = playbackState,
        onMiniPlayerClick = onMiniPlayerClick,
        onMiniPlayerPlayPause = onMiniPlayerPlayPause,
        onMiniPlayerNext = onMiniPlayerNext
    ) { padding ->
        LibraryContent(
            uiState = uiState,
            padding = padding,
            onRequestPermission = { permissionLauncher.launch(permission) },
            onOpenSettings = { context.openAppSettings() },
            onSearchQueryChange = viewModel::updateSearchQuery,
            onSectionSelected = viewModel::selectSection,
            onSortOrderSelected = viewModel::updateSortOrder,
            onToggleSortDirection = viewModel::toggleSortDirection,
            onRefresh = viewModel::refreshLibrary,
            playbackState = playbackState,
            likedSongIds = likedSongIds,
            likeActionEnabled = likeActionEnabled,
            onToggleLiked = onToggleLiked,
            onSongSelected = onSongSelected
        )
    }
}

@Composable
fun LibraryContent(
    uiState: MusicLibraryUiState,
    padding: PaddingValues = PaddingValues(),
    onRequestPermission: () -> Unit = {},
    onOpenSettings: () -> Unit = {},
    onSearchQueryChange: (String) -> Unit = {},
    onSectionSelected: (LibrarySection) -> Unit = {},
    onSortOrderSelected: (com.example.rockstar.viewmodel.LibrarySortOrder) -> Unit = {},
    onToggleSortDirection: () -> Unit = {},
    onRefresh: () -> Unit = {},
    playbackState: PlaybackUiState = PlaybackUiState(),
    likedSongIds: Set<Long> = emptySet(),
    likeActionEnabled: (Song) -> Boolean = { true },
    onToggleLiked: (Song) -> Unit = {},
    onSongSelected: (Song, List<Song>) -> Unit = { _, _ -> }
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(horizontal = 20.dp)
    ) {
        when {
            uiState.permissionState != AudioPermissionState.Granted -> {
                LibraryPermissionCard(
                    permissionState = uiState.permissionState,
                    onRequestPermission = onRequestPermission,
                    onOpenSettings = onOpenSettings,
                    modifier = Modifier.fillMaxSize()
                )
            }

            uiState.isLoading && !uiState.hasLoaded -> LoadingLibraryState()
            uiState.errorMessage != null && !uiState.hasLoaded -> LibraryErrorState(uiState.errorMessage, onRefresh)
            else -> LoadedLibraryState(
                uiState = uiState,
                onSearchQueryChange = onSearchQueryChange,
                onSectionSelected = onSectionSelected,
                onSortOrderSelected = onSortOrderSelected,
                onToggleSortDirection = onToggleSortDirection,
                onRefresh = onRefresh,
                playbackState = playbackState,
                likedSongIds = likedSongIds,
                likeActionEnabled = likeActionEnabled,
                onToggleLiked = onToggleLiked,
                onSongSelected = onSongSelected
            )
        }
    }
}

@Composable
private fun LoadedLibraryState(
    uiState: MusicLibraryUiState,
    onSearchQueryChange: (String) -> Unit,
    onSectionSelected: (LibrarySection) -> Unit,
    onSortOrderSelected: (com.example.rockstar.viewmodel.LibrarySortOrder) -> Unit,
    onToggleSortDirection: () -> Unit,
    onRefresh: () -> Unit,
    playbackState: PlaybackUiState,
    likedSongIds: Set<Long>,
    likeActionEnabled: (Song) -> Boolean,
    onToggleLiked: (Song) -> Unit,
    onSongSelected: (Song, List<Song>) -> Unit
) {
    Spacer(modifier = Modifier.height(16.dp))
    MusicSearchBar(query = uiState.searchQuery, onQueryChange = onSearchQueryChange)
    Spacer(modifier = Modifier.height(12.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.library_song_count, uiState.displayedSongs.size, uiState.songs.size),
            color = RockstarTextSecondary,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.weight(1f)
        )
        LibrarySortMenu(
            selectedSortOrder = uiState.selectedSortOrder,
            isAscending = uiState.isSortAscending,
            onSortOrderSelected = onSortOrderSelected,
            onToggleDirection = onToggleSortDirection
        )
    }
    LibrarySectionSelector(
        selectedSection = uiState.selectedLibrarySection,
        onSectionSelected = onSectionSelected
    )
    Spacer(modifier = Modifier.height(12.dp))

    if (uiState.isLoading) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) { CircularProgressIndicator() }
    }

    if (uiState.songs.isEmpty() && uiState.hasLoaded) {
        EmptyState(
            icon = Icons.Default.LibraryMusic,
            title = stringResource(R.string.library_empty_title),
            message = stringResource(R.string.library_empty_message),
            modifier = Modifier.fillMaxSize()
        )
        return
    }

    if (uiState.displayedSongs.isEmpty() && uiState.searchQuery.isNotBlank()) {
        EmptyState(
            icon = Icons.Default.MusicNote,
            title = stringResource(R.string.library_no_results_title),
            message = stringResource(R.string.library_no_results_message),
            modifier = Modifier.fillMaxSize()
        )
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        when (uiState.selectedLibrarySection) {
            LibrarySection.Songs -> items(uiState.displayedSongs, key = { it.id }) { song ->
                SongRow(
                    song = song,
                    isActive = playbackState.currentMediaId == song.id.toString(),
                    isPlaying = playbackState.isPlaying,
                    isLiked = likedSongIds.contains(song.id),
                    isLikeEnabled = likeActionEnabled(song),
                    onClick = { onSongSelected(song, uiState.displayedSongs) },
                    onLikeClick = { onToggleLiked(song) }
                )
            }

            LibrarySection.Albums -> items(uiState.albums, key = { it.key }) { album -> AlbumCard(album = album) }
            LibrarySection.Artists -> items(uiState.artists, key = { it.key }) { artist -> ArtistRow(artist = artist) }
        }
    }
}

@Composable
private fun LoadingLibraryState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator()
        Spacer(modifier = Modifier.height(RockstarSpacing.medium))
        Text(
            text = stringResource(R.string.library_loading),
            color = RockstarTextSecondary,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun LibraryErrorState(message: String, onRefresh: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(RockstarSpacing.extraLarge),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = RockstarTextSecondary)
        Spacer(modifier = Modifier.height(RockstarSpacing.medium))
        Text(message, color = RockstarTextPrimary, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(RockstarSpacing.medium))
        IconButton(onClick = onRefresh) {
            Icon(Icons.Default.Refresh, contentDescription = stringResource(R.string.action_retry))
        }
    }
}

@Composable
private fun rememberAudioPermission(): String = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
    Manifest.permission.READ_MEDIA_AUDIO
} else {
    Manifest.permission.READ_EXTERNAL_STORAGE
}

private fun Context.hasPermission(permission: String): Boolean =
    ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED

private fun Context.openAppSettings() {
    startActivity(
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
        }
    )
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
