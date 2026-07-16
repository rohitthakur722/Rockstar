package com.example.rockstar.repo

import com.example.rockstar.model.Song
import kotlinx.coroutines.CompletableDeferred

class FakeMusicRepository : MusicRepository {
    var songsResult: Result<List<Song>> = Result.success(emptyList())
    var refreshResult: Result<List<Song>>? = null
    var loadCallCount = 0
    var refreshCallCount = 0
    var loadGate: CompletableDeferred<Unit>? = null

    override suspend fun loadSongs(): Result<List<Song>> {
        loadCallCount++
        loadGate?.await()
        return songsResult
    }

    override suspend fun refreshSongs(): Result<List<Song>> {
        refreshCallCount++
        return refreshResult ?: loadSongs()
    }
}
