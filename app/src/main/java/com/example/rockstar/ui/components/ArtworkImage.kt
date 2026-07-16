package com.example.rockstar.ui.components


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

import com.example.rockstar.ui.theme.RockstarSurfaceElevated
import com.example.rockstar.ui.theme.RockstarTextSecondary

@Composable
fun ArtworkImage(
    artworkUri: String?,
    modifier: Modifier = Modifier,
    size: Dp = 56.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(androidx.compose.material3.MaterialTheme.shapes.medium)
            .background(RockstarSurfaceElevated),
        contentAlignment = Alignment.Center
    ) {
        if (artworkUri != null) {
            AsyncImage(
                model = artworkUri,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                onError = {}
            )
        } else {
            Icon(
                imageVector = Icons.Default.MusicNote,
                contentDescription = null,
                tint = RockstarTextSecondary,
                modifier = Modifier.size(size * 0.45f)
            )
        }
    }
}
