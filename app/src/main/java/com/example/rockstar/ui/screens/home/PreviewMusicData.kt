package com.example.rockstar.ui.screens.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.ui.graphics.vector.ImageVector

data class PreviewRecommendation(val title: String, val icon: ImageVector = Icons.Default.MusicNote)

data class PreviewRecentlyAdded(
    val title: String,
    val duration: String,
    val icon: ImageVector = Icons.Default.MusicNote
)

object PreviewMusicData {
    val recommendations = listOf(
        PreviewRecommendation("Local Song Preview"),
        PreviewRecommendation("Album Preview"),
        PreviewRecommendation("Artist Preview")
    )

    val recentlyAdded = listOf(
        PreviewRecentlyAdded("Preview Track", "2:56"),
        PreviewRecentlyAdded("Another Track", "3:10")
    )
}
