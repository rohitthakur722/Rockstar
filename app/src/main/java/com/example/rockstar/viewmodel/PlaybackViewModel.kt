package com.example.rockstar.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rockstar.model.Song
import com.example.rockstar.playback.PlaybackController
import com.example.rockstar.util.clampPosition
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class PlaybackViewModel(private val controller: PlaybackController) : ViewModel() {
    val uiState: StateFlow<PlaybackUiState> = controller.uiState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = PlaybackUiState(connectionState = PlaybackConnectionState.Connecting)
    )

    init {
        controller.connect()
    }

    fun playSong(song: Song, queue: List<Song>) {
        val startIndex = queue.indexOfFirst { it.id == song.id }
        playQueue(queue, startIndex)
    }

    fun playQueue(queue: List<Song>, startIndex: Int) {
        if (queue.isEmpty()) return
        controller.playQueue(queue, startIndex.coerceIn(0, queue.lastIndex))
    }

    fun togglePlayPause() = controller.togglePlayPause()
    fun play() = controller.play()
    fun pause() = controller.pause()
    fun next() = controller.seekToNext()
    fun previous() = controller.seekToPrevious()
    fun skipToQueueItem(index: Int) = controller.skipToQueueItem(index)

    fun seekTo(positionMs: Long) {
        controller.seekTo(clampPosition(positionMs, uiState.value.durationMs))
    }

    fun setShuffleEnabled(enabled: Boolean) = controller.setShuffleEnabled(enabled)

    fun cycleRepeatMode() {
        val nextMode = when (uiState.value.repeatMode) {
            RockstarRepeatMode.Off -> RockstarRepeatMode.All
            RockstarRepeatMode.All -> RockstarRepeatMode.One
            RockstarRepeatMode.One -> RockstarRepeatMode.Off
        }
        controller.setRepeatMode(nextMode)
    }

    fun setRepeatMode(mode: RockstarRepeatMode) = controller.setRepeatMode(mode)
    fun removeQueueItem(index: Int) = controller.removeQueueItem(index)
    fun clearQueue() = controller.clearQueue()
    fun stop() = controller.stop()

    override fun onCleared() {
        controller.disconnect()
        super.onCleared()
    }
}
