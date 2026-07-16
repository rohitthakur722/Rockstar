package com.example.rockstar.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.rockstar.repo.MediaStoreMusicRepository
import com.example.rockstar.repo.UserRepoImpl
import com.example.rockstar.ui.screens.account.AccountScreen
import com.example.rockstar.ui.screens.auth.ForgotPasswordScreen
import com.example.rockstar.ui.screens.auth.LoginScreen
import com.example.rockstar.ui.screens.auth.RegisterScreen
import com.example.rockstar.ui.screens.home.HomeScreen
import com.example.rockstar.ui.screens.liked.LikedScreen
import com.example.rockstar.ui.screens.playlists.PlaylistsScreen
import com.example.rockstar.ui.screens.songs.AllSongsScreen
import com.example.rockstar.ui.screens.splash.SplashScreen
import com.example.rockstar.viewmodel.MusicLibraryViewModel
import com.example.rockstar.viewmodel.MusicLibraryViewModelFactory
import com.example.rockstar.viewmodel.UserViewModel
import com.example.rockstar.viewmodel.UserViewModelFactory

@Composable
fun RockstarNavGraph(
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current.applicationContext
    val userViewModel: UserViewModel = viewModel(factory = UserViewModelFactory(UserRepoImpl()))
    val musicLibraryViewModel: MusicLibraryViewModel = viewModel(
        factory = MusicLibraryViewModelFactory(MediaStoreMusicRepository(context.contentResolver))
    )

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
                onTabSelected = { destination -> navController.navigateToTab(destination) }
            )
        }

        composable(RockstarDestination.Playlists.route) {
            PlaylistsScreen(
                currentRoute = RockstarDestination.Playlists.route,
                onTabSelected = { destination -> navController.navigateToTab(destination) }
            )
        }

        composable(RockstarDestination.Liked.route) {
            LikedScreen(
                currentRoute = RockstarDestination.Liked.route,
                onTabSelected = { destination -> navController.navigateToTab(destination) }
            )
        }

        composable(RockstarDestination.AllSongs.route) {
            AllSongsScreen(
                currentRoute = RockstarDestination.AllSongs.route,
                onTabSelected = { destination -> navController.navigateToTab(destination) },
                viewModel = musicLibraryViewModel
            )
        }

        composable(RockstarDestination.Account.route) {
            AccountScreen(
                currentRoute = RockstarDestination.Account.route,
                onTabSelected = { destination -> navController.navigateToTab(destination) },
                viewModel = userViewModel,
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
private fun NavHostController.navigateToTab(destination: RockstarDestination) {
    navigate(destination.route) {
        popUpTo(RockstarDestination.Home.route) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
