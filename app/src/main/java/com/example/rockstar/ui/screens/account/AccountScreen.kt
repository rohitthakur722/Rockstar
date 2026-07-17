package com.example.rockstar.ui.screens.account

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.rockstar.R
import com.example.rockstar.navigation.RockstarDestination
import com.example.rockstar.ui.components.ConfirmationDialog
import com.example.rockstar.ui.components.EmptyState
import com.example.rockstar.ui.components.ProfileHeader
import com.example.rockstar.ui.components.RockstarAppScaffold
import com.example.rockstar.ui.components.RockstarPrimaryButton
import com.example.rockstar.viewmodel.AuthEvent
import com.example.rockstar.viewmodel.PlaybackUiState
import com.example.rockstar.viewmodel.UserViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person

@Composable
fun AccountScreen(
    currentRoute: String,
    onTabSelected: (RockstarDestination) -> Unit,
    viewModel: UserViewModel,
    playbackState: PlaybackUiState,
    onMiniPlayerClick: () -> Unit,
    onMiniPlayerPlayPause: () -> Unit,
    onMiniPlayerNext: () -> Unit,
    onSettingsClick: () -> Unit,
    onLogoutConfirmed: () -> Unit,
    onLoggedOut: () -> Unit
) {
    val authState by viewModel.authState.collectAsStateWithLifecycle()
    val profileState by viewModel.profileState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    var showEditDialog by remember { mutableStateOf(false) }
    var showLogoutConfirm by remember { mutableStateOf(false) }
    var editDialogError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is AuthEvent.NavigateToLogin -> onLoggedOut()
                is AuthEvent.ShowMessage -> {
                    showEditDialog = false
                    editDialogError = null
                    snackbarHostState.showSnackbar(event.message)
                }

                is AuthEvent.ShowError -> {
                    if (showEditDialog) {
                        editDialogError = event.message
                    } else {
                        snackbarHostState.showSnackbar(event.message)
                    }
                }

                else -> Unit
            }
        }
    }

    val user = authState.currentUser

    RockstarAppScaffold(
        title = stringResource(R.string.nav_account),
        currentRoute = currentRoute,
        onTabSelected = onTabSelected,
        playbackState = playbackState,
        onMiniPlayerClick = onMiniPlayerClick,
        onMiniPlayerPlayPause = onMiniPlayerPlayPause,
        onMiniPlayerNext = onMiniPlayerNext
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (user == null) {
                Column(modifier = Modifier.fillMaxSize()) {
                    EmptyState(
                        icon = Icons.Default.Person,
                        title = stringResource(R.string.account_profile_unavailable_title),
                        message = stringResource(R.string.account_profile_unavailable_message),
                        modifier = Modifier.weight(1f)
                    )
                    RockstarPrimaryButton(
                        text = stringResource(R.string.action_retry),
                        onClick = { viewModel.restoreSession() },
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
                    )
                }
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    ProfileHeader(
                        fullName = user.fullName,
                        email = user.email,
                        modifier = Modifier.weight(1f)
                    )

                    RockstarPrimaryButton(
                        text = stringResource(R.string.action_edit_profile),
                        onClick = { showEditDialog = true },
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                    )

                    RockstarPrimaryButton(
                        text = stringResource(R.string.settings_title),
                        onClick = onSettingsClick,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                    )

                    RockstarPrimaryButton(
                        text = stringResource(R.string.action_log_out),
                        onClick = { showLogoutConfirm = true },
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                    )
                }
            }

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }

    if (showLogoutConfirm) {
        ConfirmationDialog(
            title = stringResource(R.string.logout_confirm_title),
            message = stringResource(R.string.logout_confirm_message),
            confirmLabel = stringResource(R.string.action_log_out),
            dismissLabel = stringResource(R.string.action_cancel),
            onConfirm = {
                showLogoutConfirm = false
                onLogoutConfirmed()
                viewModel.logout()
            },
            onDismiss = { showLogoutConfirm = false }
        )
    }

    if (showEditDialog && user != null) {
        EditProfileDialog(
            initialFullName = user.fullName,
            isSaving = profileState.isSaving,
            errorMessage = editDialogError,
            onDismiss = {
                showEditDialog = false
                editDialogError = null
            },
            onSave = { fullName ->
                editDialogError = null
                viewModel.updateProfile(fullName)
            }
        )
    }
}
