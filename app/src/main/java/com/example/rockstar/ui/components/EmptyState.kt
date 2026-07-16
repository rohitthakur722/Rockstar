package com.example.rockstar.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.rockstar.ui.theme.RockstarSpacing
import com.example.rockstar.ui.theme.RockstarTextPrimary
import com.example.rockstar.ui.theme.RockstarTextSecondary

/**
 * Reusable placeholder for destinations whose real functionality arrives in
 * a later phase. Communicates state honestly instead of faking data.
 */
@Composable
fun EmptyState(
    icon: ImageVector,
    title: String,
    message: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(RockstarSpacing.extraLarge),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = RockstarTextSecondary,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(RockstarSpacing.medium))
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = RockstarTextPrimary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(RockstarSpacing.small))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = RockstarTextSecondary,
            textAlign = TextAlign.Center
        )
    }
}
