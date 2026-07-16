package com.example.rockstar.ui.screens.splash

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.rockstar.R
import com.example.rockstar.ui.theme.RockstarAccent
import com.example.rockstar.ui.theme.RockstarBackground
import com.example.rockstar.ui.theme.RockstarTextSecondary
import com.example.rockstar.viewmodel.UserViewModel
import kotlinx.coroutines.delay

private const val MIN_BRANDING_DURATION_MS = 900L
private const val SPLASH_ANIMATION_MS = 600

@Composable
fun SplashScreen(
    viewModel: UserViewModel,
    onNavigateToHome: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    var startAnimation by remember { mutableStateOf(false) }
    var hasNavigated by remember { mutableStateOf(false) }
    val authState by viewModel.authState.collectAsStateWithLifecycle()

    val scale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.7f,
        animationSpec = tween(durationMillis = SPLASH_ANIMATION_MS),
        label = "splash_scale"
    )
    val alpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = SPLASH_ANIMATION_MS),
        label = "splash_alpha"
    )

    LaunchedEffect(Unit) {
        startAnimation = true
    }

    // Authentication state — not a fixed timer — decides the destination.
    // The minimum branding delay only smooths out a near-instant resolution.
    LaunchedEffect(authState.isSessionResolved) {
        if (!authState.isSessionResolved || hasNavigated) return@LaunchedEffect
        delay(MIN_BRANDING_DURATION_MS)
        hasNavigated = true
        if (authState.isAuthenticated) onNavigateToHome() else onNavigateToLogin()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(RockstarBackground)
            .safeDrawingPadding(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(R.string.app_name).uppercase(),
                style = MaterialTheme.typography.headlineLarge,
                color = RockstarAccent,
                modifier = Modifier.scale(scale).alpha(alpha)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.splash_tagline),
                style = MaterialTheme.typography.bodyLarge,
                color = RockstarTextSecondary,
                modifier = Modifier.alpha(alpha)
            )
        }
    }
}
