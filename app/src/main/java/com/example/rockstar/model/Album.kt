package com.example.rockstar.model

import android.net.Uri

data class Album(
    val key: String,
    val title: String,
    val artist: String,
    val artworkUri: Uri?,
    val songCount: Int,
    val totalDurationMs: Long
)
