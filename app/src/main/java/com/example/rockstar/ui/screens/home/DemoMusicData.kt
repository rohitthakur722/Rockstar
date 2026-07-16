package com.example.rockstar.ui.screens.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Preview/demo content only, used to give the Home screen shape before real
 * music data exists. Not backed by the device library or Firebase, and must
 * not be treated as such.
 */
data class DemoRecommendation(val title: String, val icon: ImageVector = Icons.Default.MusicNote)

data class DemoRecentlyPlayed(
    val title: String,
    val duration: String,
    val icon: ImageVector = Icons.Default.MusicNote
)

object DemoMusicData {
    val recommendations = listOf(
        DemoRecommendation("SZA - Snooze"),
        DemoRecommendation("The Marias - Heavy"),
        DemoRecommendation("Wave to Earth - Home")
    )

    val recentlyPlayed = listOf(
        DemoRecentlyPlayed("Dosii - In Dreams", "2:56"),
        DemoRecentlyPlayed("Dvwn - Fairy", "3:10"),
        DemoRecentlyPlayed("ADOY - Swim", "3:48")
    )
}
