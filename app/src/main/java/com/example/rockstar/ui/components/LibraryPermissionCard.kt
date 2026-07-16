package com.example.rockstar.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.example.rockstar.R
import com.example.rockstar.viewmodel.AudioPermissionState
import com.example.rockstar.ui.theme.RockstarSpacing
import com.example.rockstar.ui.theme.RockstarTextPrimary
import com.example.rockstar.ui.theme.RockstarTextSecondary

@Composable
fun LibraryPermissionCard(
    permissionState: AudioPermissionState,
    onRequestPermission: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val permanentlyDenied = permissionState == AudioPermissionState.PermanentlyDenied
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(RockstarSpacing.extraLarge),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Default.LibraryMusic, contentDescription = null, tint = RockstarTextSecondary)
        Spacer(modifier = Modifier.height(RockstarSpacing.medium))
        Text(
            text = if (permanentlyDenied) stringResource(R.string.library_permission_settings_title) else stringResource(
                R.string.library_permission_title
            ),
            color = RockstarTextPrimary,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(RockstarSpacing.small))
        Text(
            text = when (permissionState) {
                AudioPermissionState.DeniedWithRationale -> stringResource(R.string.library_permission_rationale_message)
                AudioPermissionState.Denied -> stringResource(R.string.library_permission_denied_message)
                AudioPermissionState.PermanentlyDenied -> stringResource(R.string.library_permission_settings_message)
                else -> stringResource(R.string.library_permission_message)
            },
            color = RockstarTextSecondary,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(RockstarSpacing.large))
        RockstarPrimaryButton(
            text = if (permanentlyDenied) stringResource(R.string.action_open_settings) else stringResource(R.string.action_allow_audio_access),
            onClick = if (permanentlyDenied) onOpenSettings else onRequestPermission
        )
    }
}
