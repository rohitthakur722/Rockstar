package com.example.rockstar.repo

import com.example.rockstar.model.Song

interface MusicRepository {
    suspend fun loadSongs(): Result<List<Song>>

    suspend fun refreshSongs(): Result<List<Song>> = loadSongs()
}
