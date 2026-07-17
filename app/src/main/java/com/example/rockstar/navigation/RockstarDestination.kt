package com.example.rockstar.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.rockstar.R

sealed class RockstarDestination(val route: String) {
    data object Splash : RockstarDestination("splash")
    data object Login : RockstarDestination("login")
    data object Register : RockstarDestination("register")
    data object ForgotPassword : RockstarDestination("forgot_password")
    data object Home : RockstarDestination("home")
    data object Playlists : RockstarDestination("playlists")
    data object PlaylistDetail : RockstarDestination("playlist/{playlistId}") {
        fun createRoute(playlistId: Long): String = "playlist/$playlistId"
    }

    data object Liked : RockstarDestination("liked")
    data object AllSongs : RockstarDestination("all_songs")
    data object Account : RockstarDestination("account")
    data object Settings : RockstarDestination("settings")
    data object NowPlaying : RockstarDestination("now_playing")
}

data class BottomNavItem(
    val destination: RockstarDestination,
    val labelRes: Int,
    val icon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(RockstarDestination.Home, R.string.nav_home, Icons.Default.Home),
    BottomNavItem(RockstarDestination.Playlists, R.string.nav_playlists, Icons.Default.LibraryMusic),
    BottomNavItem(RockstarDestination.Liked, R.string.nav_liked, Icons.Default.FavoriteBorder),
    BottomNavItem(RockstarDestination.AllSongs, R.string.nav_all_songs, Icons.Default.MusicNote),
    BottomNavItem(RockstarDestination.Account, R.string.nav_account, Icons.Default.Person)
)
