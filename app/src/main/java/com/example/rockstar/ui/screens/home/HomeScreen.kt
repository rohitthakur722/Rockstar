package com.example.rockstar.ui.screens.home

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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
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
import com.example.rockstar.ui.components.EmptyState
import com.example.rockstar.ui.components.FeaturedArtistCard
import com.example.rockstar.ui.components.LibraryPermissionCard
import com.example.rockstar.ui.components.MusicSearchBar
import com.example.rockstar.ui.components.RockstarAppScaffold
import com.example.rockstar.ui.components.SectionHeader
import com.example.rockstar.ui.components.SongRow
import com.example.rockstar.ui.components.WelcomeHeader
import com.example.rockstar.viewmodel.AudioPermissionState
import com.example.rockstar.viewmodel.MusicLibraryViewModel
import com.example.rockstar.viewmodel.PlaybackUiState

@Composable
fun HomeScreen(
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
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val activity = context.findActivity()
    val permission = rememberAudioPermission()
    var hasRequestedPermission by rememberSaveable { mutableStateOf(false) }

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

    val userName = stringResource(R.string.default_user_name)
    val recentlyAdded = uiState.songs.sortedByDescending { it.dateAdded }.take(5)
    val albumHighlights = uiState.albums.take(4)

    RockstarAppScaffold(
        title = stringResource(R.string.app_name),
        currentRoute = currentRoute,
        onTabSelected = onTabSelected,
        showBrandMark = true,
        trailingIcon = Icons.Outlined.Notifications,
        trailingContentDescription = stringResource(R.string.cd_notifications),
        playbackState = playbackState,
        onMiniPlayerClick = onMiniPlayerClick,
        onMiniPlayerPlayPause = onMiniPlayerPlayPause,
        onMiniPlayerNext = onMiniPlayerNext
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            WelcomeHeader(userName = userName)

            Spacer(modifier = Modifier.height(24.dp))

            MusicSearchBar(query = uiState.searchQuery, onQueryChange = viewModel::updateSearchQuery)

            Spacer(modifier = Modifier.height(24.dp))

            FeaturedArtistCard(onExploreClick = { onTabSelected(RockstarDestination.AllSongs) })

            Spacer(modifier = Modifier.height(32.dp))

            when {
                uiState.permissionState != AudioPermissionState.Granted -> {
                    LibraryPermissionCard(
                        permissionState = uiState.permissionState,
                        onRequestPermission = { permissionLauncher.launch(permission) },
                        onOpenSettings = { context.openAppSettings() },
                        modifier = Modifier.height(360.dp)
                    )
                }

                uiState.isLoading && !uiState.hasLoaded -> {
                    EmptyState(
                        icon = Icons.Default.LibraryMusic,
                        title = stringResource(R.string.library_loading),
                        message = stringResource(R.string.library_permission_message),
                        modifier = Modifier.height(320.dp)
                    )
                }

                uiState.songs.isEmpty() && uiState.hasLoaded -> {
                    EmptyState(
                        icon = Icons.Default.LibraryMusic,
                        title = stringResource(R.string.library_empty_title),
                        message = stringResource(R.string.library_empty_message),
                        modifier = Modifier.height(320.dp)
                    )
                }

                uiState.searchQuery.isNotBlank() -> {
                    SectionHeader(title = stringResource(R.string.home_search_results), showSeeAll = false)
                    Spacer(modifier = Modifier.height(16.dp))
                    if (uiState.displayedSongs.isEmpty()) {
                        EmptyState(
                            icon = Icons.Default.MusicNote,
                            title = stringResource(R.string.library_no_results_title),
                            message = stringResource(R.string.library_no_results_message),
                            modifier = Modifier.height(260.dp)
                        )
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            val searchQueue = uiState.displayedSongs.take(6)
                            searchQueue.forEach { song ->
                                SongRow(
                                    song = song,
                                    isActive = playbackState.currentMediaId == song.id.toString(),
                                    isPlaying = playbackState.isPlaying,
                                    isLiked = likedSongIds.contains(song.id),
                                    isLikeEnabled = likeActionEnabled(song),
                                    onClick = { onSongSelected(song, searchQueue) },
                                    onLikeClick = { onToggleLiked(song) }
                                )
                            }
                        }
                    }
                }

                else -> {
                    SectionHeader(title = stringResource(R.string.section_albums_in_library), showSeeAll = false)
                    Spacer(modifier = Modifier.height(16.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        albumHighlights.forEach { album -> AlbumCard(album = album) }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    SectionHeader(title = stringResource(R.string.section_recently_added), showSeeAll = false)
                    Spacer(modifier = Modifier.height(16.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        recentlyAdded.forEach { song ->
                            SongRow(
                                song = song,
                                isActive = playbackState.currentMediaId == song.id.toString(),
                                isPlaying = playbackState.isPlaying,
                                isLiked = likedSongIds.contains(song.id),
                                isLikeEnabled = likeActionEnabled(song),
                                onClick = { onSongSelected(song, recentlyAdded) },
                                onLikeClick = { onToggleLiked(song) }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
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
