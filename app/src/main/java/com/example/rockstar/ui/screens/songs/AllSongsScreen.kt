package com.example.rockstar.ui.screens.songs

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.rockstar.R
import com.example.rockstar.navigation.RockstarDestination
import com.example.rockstar.ui.components.EmptyState
import com.example.rockstar.ui.components.RockstarAppScaffold

@Composable
fun AllSongsScreen(
    currentRoute: String,
    onTabSelected: (RockstarDestination) -> Unit
) {
    RockstarAppScaffold(
        title = stringResource(R.string.nav_all_songs),
        currentRoute = currentRoute,
        onTabSelected = onTabSelected
    ) { padding ->
        EmptyState(
            icon = Icons.Default.MusicNote,
            title = stringResource(R.string.empty_songs_title),
            message = stringResource(R.string.empty_songs_message),
            modifier = Modifier.padding(padding)
        )
    }
}
