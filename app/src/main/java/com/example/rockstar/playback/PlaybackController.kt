package com.example.rockstar.playback

import com.example.rockstar.model.Song
import com.example.rockstar.viewmodel.PlaybackUiState
import com.example.rockstar.viewmodel.RockstarRepeatMode
import kotlinx.coroutines.flow.StateFlow

interface PlaybackController {
    val uiState: StateFlow<PlaybackUiState>

    fun connect()
    fun disconnect()
    fun playQueue(queue: List<Song>, startIndex: Int)
    fun togglePlayPause()
    fun play()
    fun pause()
    fun seekTo(positionMs: Long)
    fun seekToNext()
    fun seekToPrevious()
    fun skipToQueueItem(index: Int)
    fun setShuffleEnabled(enabled: Boolean)
    fun setRepeatMode(mode: RockstarRepeatMode)
    fun removeQueueItem(index: Int)
    fun clearQueue()
    fun stop()
}
