package com.example.rockstar.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.example.rockstar.ui.theme.RockstarAccent
import com.example.rockstar.ui.theme.RockstarSurfaceElevated
import com.example.rockstar.ui.theme.RockstarTextPrimary
import com.example.rockstar.ui.theme.RockstarTextSecondary

@Composable
fun ConfirmationDialog(
    title: String,
    message: String,
    confirmLabel: String,
    dismissLabel: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = RockstarSurfaceElevated,
        title = { Text(text = title, color = RockstarTextPrimary) },
        text = { Text(text = message, color = RockstarTextSecondary) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = confirmLabel, color = RockstarAccent)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = dismissLabel, color = RockstarTextSecondary)
            }
        }
    )
}
