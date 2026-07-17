package com.example.rockstar.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.compose.rememberNavController
import com.example.rockstar.RockstarApplication
import com.example.rockstar.playback.Media3PlaybackController
import com.example.rockstar.repo.UserRepoImpl
import com.example.rockstar.ui.screens.account.AccountScreen
import com.example.rockstar.ui.screens.auth.ForgotPasswordScreen
import com.example.rockstar.ui.screens.auth.LoginScreen
import com.example.rockstar.ui.screens.auth.RegisterScreen
import com.example.rockstar.ui.screens.home.HomeScreen
import com.example.rockstar.ui.screens.liked.LikedScreen
import com.example.rockstar.ui.screens.playlists.PlaylistDetailScreen
import com.example.rockstar.ui.screens.playlists.PlaylistsScreen
import com.example.rockstar.ui.screens.songs.AllSongsScreen
import com.example.rockstar.ui.screens.player.NowPlayingScreen
import com.example.rockstar.ui.screens.settings.SettingsScreen
import com.example.rockstar.ui.screens.splash.SplashScreen
import com.example.rockstar.viewmodel.MusicLibraryViewModel
import com.example.rockstar.viewmodel.PlaybackViewModel
import com.example.rockstar.viewmodel.PlaybackViewModelFactory
import com.example.rockstar.viewmodel.MusicLibraryViewModelFactory
import com.example.rockstar.viewmodel.PersonalLibraryViewModel
import com.example.rockstar.viewmodel.PersonalLibraryViewModelFactory
import com.example.rockstar.viewmodel.SettingsViewModel
import com.example.rockstar.viewmodel.SettingsViewModelFactory
import com.example.rockstar.viewmodel.UserViewModel
import com.example.rockstar.viewmodel.UserViewModelFactory

@Composable
fun RockstarNavGraph(
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current.applicationContext
    val container = (context as RockstarApplication).container
    val userViewModel: UserViewModel = viewModel(factory = UserViewModelFactory(UserRepoImpl()))
    val musicLibraryViewModel: MusicLibraryViewModel = viewModel(
        factory = MusicLibraryViewModelFactory(container.musicRepository)
    )
    val playbackViewModel: PlaybackViewModel = viewModel(
        factory = PlaybackViewModelFactory(Media3PlaybackController(context))
    )
    val personalLibraryViewModel: PersonalLibraryViewModel = viewModel(
        factory = PersonalLibraryViewModelFactory(container.personalLibraryRepository)
    )
    val settingsViewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModelFactory(container.preferencesRepository)
    )
    val playbackState by playbackViewModel.uiState.collectAsStateWithLifecycle()
    val authState by userViewModel.authState.collectAsStateWithLifecycle()
    val personalLibraryState by personalLibraryViewModel.uiState.collectAsStateWithLifecycle()
    val musicLibraryState by musicLibraryViewModel.uiState.collectAsStateWithLifecycle()

    androidx.compose.runtime.LaunchedEffect(authState.currentUser?.uid) {
        personalLibraryViewModel.setOwnerUid(authState.currentUser?.uid)
    }
    androidx.compose.runtime.LaunchedEffect(musicLibraryState.songs) {
        personalLibraryViewModel.updateLibrarySongs(musicLibraryState.songs)
    }
    androidx.compose.runtime.LaunchedEffect(
        playbackState.currentMediaId,
        playbackState.positionMs,
        playbackState.isPlaying
    ) {
        personalLibraryViewModel.onPlaybackProgress(playbackState)
    }

    NavHost(
        navController = navController,
        startDestination = RockstarDestination.Splash.route
    ) {
        composable(RockstarDestination.Splash.route) {
            SplashScreen(
                viewModel = userViewModel,
                onNavigateToHome = {
                    navController.navigate(RockstarDestination.Home.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate(RockstarDestination.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(RockstarDestination.Login.route) {
            LoginScreen(
                viewModel = userViewModel,
                onLoginSuccess = {
                    navController.navigate(RockstarDestination.Home.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToRegister = { navController.navigate(RockstarDestination.Register.route) },
                onNavigateToForgotPassword = { navController.navigate(RockstarDestination.ForgotPassword.route) }
            )
        }

        composable(RockstarDestination.Register.route) {
            RegisterScreen(
                viewModel = userViewModel,
                onRegistrationSuccess = {
                    navController.navigate(RockstarDestination.Home.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToLogin = { navController.popBackStack() },
                onBack = { navController.popBackStack() }
            )
        }

        composable(RockstarDestination.ForgotPassword.route) {
            ForgotPasswordScreen(
                viewModel = userViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(RockstarDestination.Home.route) {
            HomeScreen(
                currentRoute = RockstarDestination.Home.route,
                onTabSelected = { destination -> navController.navigateToTab(destination) },
                viewModel = musicLibraryViewModel,
                playbackState = playbackState,
                likedSongIds = personalLibraryState.likedSongIds,
                likeActionEnabled = { song -> personalLibraryState.activeSongId != song.id && personalLibraryState.isAuthenticated },
                recentSongs = personalLibraryState.recentSongs,
                recommendedSongs = personalLibraryState.recommendedSongs,
                onToggleLiked = personalLibraryViewModel::toggleLiked,
                onSongSelected = playbackViewModel::playSong,
                onMiniPlayerClick = { navController.navigateToNowPlaying() },
                onMiniPlayerPlayPause = playbackViewModel::togglePlayPause,
                onMiniPlayerNext = playbackViewModel::next
            )
        }

        composable(RockstarDestination.Playlists.route) {
            PlaylistsScreen(
                currentRoute = RockstarDestination.Playlists.route,
                onTabSelected = { destination -> navController.navigateToTab(destination) },
                viewModel = personalLibraryViewModel,
                playbackState = playbackState,
                onPlaylistClick = { playlistId ->
                    navController.navigate(
                        RockstarDestination.PlaylistDetail.createRoute(
                            playlistId
                        )
                    )
                },
                onMiniPlayerClick = { navController.navigateToNowPlaying() },
                onMiniPlayerPlayPause = playbackViewModel::togglePlayPause,
                onMiniPlayerNext = playbackViewModel::next
            )
        }

        composable(
            route = RockstarDestination.PlaylistDetail.route,
            arguments = listOf(navArgument("playlistId") { type = NavType.LongType })
        ) { backStackEntry ->
            val playlistId = backStackEntry.arguments?.getLong("playlistId") ?: return@composable
            PlaylistDetailScreen(
                playlistId = playlistId,
                viewModel = personalLibraryViewModel,
                playbackState = playbackState,
                onBack = { navController.popBackStack() },
                onPlayQueue = playbackViewModel::playSong
            )
        }

        composable(RockstarDestination.Liked.route) {
            LikedScreen(
                currentRoute = RockstarDestination.Liked.route,
                onTabSelected = { destination -> navController.navigateToTab(destination) },
                viewModel = personalLibraryViewModel,
                playbackState = playbackState,
                onSongSelected = playbackViewModel::playSong,
                onMiniPlayerClick = { navController.navigateToNowPlaying() },
                onMiniPlayerPlayPause = playbackViewModel::togglePlayPause,
                onMiniPlayerNext = playbackViewModel::next
            )
        }

        composable(RockstarDestination.AllSongs.route) {
            AllSongsScreen(
                currentRoute = RockstarDestination.AllSongs.route,
                onTabSelected = { destination -> navController.navigateToTab(destination) },
                viewModel = musicLibraryViewModel,
                playbackState = playbackState,
                likedSongIds = personalLibraryState.likedSongIds,
                likeActionEnabled = { song -> personalLibraryState.activeSongId != song.id && personalLibraryState.isAuthenticated },
                onToggleLiked = personalLibraryViewModel::toggleLiked,
                onSongSelected = playbackViewModel::playSong,
                onMiniPlayerClick = { navController.navigateToNowPlaying() },
                onMiniPlayerPlayPause = playbackViewModel::togglePlayPause,
                onMiniPlayerNext = playbackViewModel::next
            )
        }

        composable(RockstarDestination.NowPlaying.route) {
            NowPlayingScreen(
                playbackState = playbackState,
                onBack = { navController.popBackStack() },
                onTogglePlayPause = playbackViewModel::togglePlayPause,
                onPrevious = playbackViewModel::previous,
                onNext = playbackViewModel::next,
                onSeek = playbackViewModel::seekTo,
                onShuffle = { playbackViewModel.setShuffleEnabled(!playbackState.shuffleEnabled) },
                onRepeat = playbackViewModel::cycleRepeatMode,
                isCurrentSongLiked = playbackState.currentSong?.id?.let(personalLibraryState.likedSongIds::contains) == true,
                isLikeEnabled = playbackState.currentSong?.let { personalLibraryState.activeSongId != it.id && personalLibraryState.isAuthenticated } == true,
                playlists = personalLibraryState.playlists,
                onToggleLiked = { playbackState.currentSong?.let(personalLibraryViewModel::toggleLiked) },
                onAddCurrentSongToPlaylist = { playlistId ->
                    playbackState.currentSong?.let {
                        personalLibraryViewModel.addSongToPlaylist(
                            playlistId,
                            it
                        )
                    }
                },
                onSkipToQueueItem = playbackViewModel::skipToQueueItem,
                onRemoveQueueItem = playbackViewModel::removeQueueItem,
                onClearQueue = playbackViewModel::clearQueue
            )
        }

        composable(RockstarDestination.Settings.route) {
            SettingsScreen(
                settingsViewModel = settingsViewModel,
                personalLibraryViewModel = personalLibraryViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(RockstarDestination.Account.route) {
            AccountScreen(
                currentRoute = RockstarDestination.Account.route,
                onTabSelected = { destination -> navController.navigateToTab(destination) },
                viewModel = userViewModel,
                playbackState = playbackState,
                onMiniPlayerClick = { navController.navigateToNowPlaying() },
                onMiniPlayerPlayPause = playbackViewModel::togglePlayPause,
                onMiniPlayerNext = playbackViewModel::next,
                onSettingsClick = { navController.navigate(RockstarDestination.Settings.route) },
                onLogoutConfirmed = playbackViewModel::clearQueue,
                onLoggedOut = {
                    navController.navigate(RockstarDestination.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}

/**
 * Standard bottom-navigation pattern: avoids stacking a new back-stack entry
 * per tab switch while still restoring each tab's own scroll/UI state.
 */
private fun NavHostController.navigateToNowPlaying() {
    navigate(RockstarDestination.NowPlaying.route) {
        launchSingleTop = true
    }
}

private fun NavHostController.navigateToTab(destination: RockstarDestination) {
    navigate(destination.route) {
        popUpTo(RockstarDestination.Home.route) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
