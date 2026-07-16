package com.example.rockstar.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.example.rockstar.R
import com.example.rockstar.ui.theme.RockstarTextPrimary
import com.example.rockstar.ui.theme.RockstarTextSecondary

@Composable
fun WelcomeHeader(userName: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(R.string.home_welcome, userName),
            style = MaterialTheme.typography.headlineMedium,
            color = RockstarTextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = stringResource(R.string.home_welcome_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = RockstarTextSecondary
        )
    }
}
