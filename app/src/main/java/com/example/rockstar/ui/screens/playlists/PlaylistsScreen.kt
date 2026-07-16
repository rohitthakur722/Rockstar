package com.example.rockstar.ui.screens.playlists

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.rockstar.R
import com.example.rockstar.navigation.RockstarDestination
import com.example.rockstar.ui.components.EmptyState
import com.example.rockstar.ui.components.RockstarAppScaffold

@Composable
fun PlaylistsScreen(
    currentRoute: String,
    onTabSelected: (RockstarDestination) -> Unit
) {
    RockstarAppScaffold(
        title = stringResource(R.string.nav_playlists),
        currentRoute = currentRoute,
        onTabSelected = onTabSelected
    ) { padding ->
        EmptyState(
            icon = Icons.Default.LibraryMusic,
            title = stringResource(R.string.empty_playlists_title),
            message = stringResource(R.string.empty_playlists_message),
            modifier = Modifier.padding(padding)
        )
    }
}
