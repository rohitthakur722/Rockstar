package com.example.rockstar.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import com.example.rockstar.data.preferences.ThemeMode
import com.example.rockstar.ui.theme.RockstarBackground
import com.example.rockstar.ui.theme.RockstarTextPrimary
import com.example.rockstar.ui.theme.RockstarTextSecondary
import com.example.rockstar.viewmodel.PersonalLibraryViewModel
import com.example.rockstar.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel,
    personalLibraryViewModel: PersonalLibraryViewModel,
    onBack: () -> Unit
) {
    val settingsState by settingsViewModel.uiState.collectAsStateWithLifecycle()
    val personalState by personalLibraryViewModel.uiState.collectAsStateWithLifecycle()
    var clearHistoryConfirm by remember { mutableStateOf(false) }
    var clearAllConfirm by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = RockstarBackground,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_title), color = RockstarTextPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.cd_back))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = RockstarBackground)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            SettingsSectionTitle(stringResource(R.string.settings_appearance))
            ThemeMode.entries.forEach { mode ->
                SettingRadioRow(
                    title = when (mode) {
                        ThemeMode.System -> stringResource(R.string.theme_system)
                        ThemeMode.Dark -> stringResource(R.string.theme_dark)
                        ThemeMode.Light -> stringResource(R.string.theme_light)
                    },
                    selected = settingsState.themeMode == mode,
                    onClick = { settingsViewModel.setThemeMode(mode) }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            SettingsSectionTitle(stringResource(R.string.settings_playback))
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(stringResource(R.string.setting_restore_queue), color = RockstarTextPrimary)
                    Text(stringResource(R.string.setting_restore_queue_description), color = RockstarTextSecondary)
                }
                Switch(checked = settingsState.restoreQueue, onCheckedChange = settingsViewModel::setRestoreQueue)
            }

            Spacer(modifier = Modifier.height(8.dp))
            SettingsSectionTitle(stringResource(R.string.settings_privacy_data))
            TextButton(enabled = personalState.isAuthenticated, onClick = { clearHistoryConfirm = true }) {
                Text(stringResource(R.string.action_clear_history))
            }
            TextButton(enabled = personalState.isAuthenticated, onClick = { clearAllConfirm = true }) {
                Text(stringResource(R.string.action_clear_all_local_data))
            }

            Spacer(modifier = Modifier.height(8.dp))
            SettingsSectionTitle(stringResource(R.string.settings_about))
            Text(stringResource(R.string.settings_about_description), color = RockstarTextSecondary)
        }
    }

    if (clearHistoryConfirm) {
        ConfirmDataDialog(
            title = stringResource(R.string.clear_history_title),
            message = stringResource(R.string.clear_history_message),
            onConfirm = {
                personalLibraryViewModel.clearHistory()
                clearHistoryConfirm = false
            },
            onDismiss = { clearHistoryConfirm = false }
        )
    }
    if (clearAllConfirm) {
        ConfirmDataDialog(
            title = stringResource(R.string.clear_all_data_title),
            message = stringResource(R.string.clear_all_data_message),
            onConfirm = {
                personalLibraryViewModel.clearOwnerData()
                clearAllConfirm = false
            },
            onDismiss = { clearAllConfirm = false }
        )
    }
}

@Composable
private fun SettingsSectionTitle(text: String) {
    Text(text, color = RockstarTextPrimary)
}

@Composable
private fun SettingRadioRow(title: String, selected: Boolean, onClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        RadioButton(selected = selected, onClick = onClick)
        Text(title, color = RockstarTextPrimary)
    }
}

@Composable
private fun ConfirmDataDialog(
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(message) },
        confirmButton = { TextButton(onClick = onConfirm) { Text(stringResource(R.string.action_delete)) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.action_cancel)) } }
    )
}
