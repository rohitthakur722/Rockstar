package com.example.rockstar.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.rockstar.R
import com.example.rockstar.navigation.RockstarDestination
import com.example.rockstar.ui.components.FeaturedArtistCard
import com.example.rockstar.ui.components.MusicSearchBar
import com.example.rockstar.ui.components.RecentlyPlayedRow
import com.example.rockstar.ui.components.RecommendationRow
import com.example.rockstar.ui.components.RockstarAppScaffold
import com.example.rockstar.ui.components.SectionHeader
import com.example.rockstar.ui.components.WelcomeHeader

@Composable
fun HomeScreen(
    currentRoute: String,
    onTabSelected: (RockstarDestination) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val userName = stringResource(R.string.default_user_name)

    RockstarAppScaffold(
        title = stringResource(R.string.app_name),
        currentRoute = currentRoute,
        onTabSelected = onTabSelected,
        showBrandMark = true,
        trailingIcon = Icons.Outlined.Notifications,
        trailingContentDescription = stringResource(R.string.cd_notifications)
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

            MusicSearchBar(query = searchQuery, onQueryChange = { searchQuery = it })

            Spacer(modifier = Modifier.height(24.dp))

            FeaturedArtistCard()

            Spacer(modifier = Modifier.height(32.dp))

            SectionHeader(title = stringResource(R.string.section_recommendation))
            Spacer(modifier = Modifier.height(16.dp))
            RecommendationRow(items = DemoMusicData.recommendations)

            Spacer(modifier = Modifier.height(32.dp))

            SectionHeader(title = stringResource(R.string.section_recently_played), showSeeAll = false)
            Spacer(modifier = Modifier.height(16.dp))
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                DemoMusicData.recentlyPlayed.forEach { song ->
                    RecentlyPlayedRow(song = song)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
