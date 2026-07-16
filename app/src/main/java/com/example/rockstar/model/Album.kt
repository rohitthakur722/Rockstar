package com.example.rockstar.model


data class Album(
    val key: String,
    val title: String,
    val artist: String,
    val artworkUri: String?,
    val songCount: Int,
    val totalDurationMs: Long
)
