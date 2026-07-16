package com.example.rockstar.ui.screens.account

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.rockstar.R
import com.example.rockstar.ui.components.RockstarTextField
import com.example.rockstar.ui.theme.RockstarAccent
import com.example.rockstar.ui.theme.RockstarSurfaceElevated
import com.example.rockstar.ui.theme.RockstarTextPrimary
import com.example.rockstar.ui.theme.RockstarTextSecondary
import com.example.rockstar.util.Validators

@Composable
fun EditProfileDialog(
    initialFullName: String,
    isSaving: Boolean,
    errorMessage: String?,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var fullName by remember { mutableStateOf(initialFullName) }
    var fullNameError by remember { mutableStateOf<String?>(null) }
    val invalidNameMessage = stringResource(R.string.error_invalid_full_name)

    AlertDialog(
        onDismissRequest = { if (!isSaving) onDismiss() },
        containerColor = RockstarSurfaceElevated,
        title = { Text(text = stringResource(R.string.edit_profile_title), color = RockstarTextPrimary) },
        text = {
            Column {
                RockstarTextField(
                    value = fullName,
                    onValueChange = {
                        fullName = it
                        fullNameError = null
                    },
                    placeholder = stringResource(R.string.label_full_name),
                    isError = fullNameError != null,
                    supportingText = fullNameError ?: errorMessage,
                    enabled = !isSaving
                )
                if (isSaving) {
                    Spacer(modifier = Modifier.height(12.dp))
                    CircularProgressIndicator(color = RockstarAccent)
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (Validators.isValidFullName(fullName)) {
                        onSave(fullName.trim())
                    } else {
                        fullNameError = invalidNameMessage
                    }
                },
                enabled = !isSaving
            ) {
                Text(text = stringResource(R.string.action_save), color = RockstarAccent)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isSaving) {
                Text(text = stringResource(R.string.action_cancel), color = RockstarTextSecondary)
            }
        }
    )
}
