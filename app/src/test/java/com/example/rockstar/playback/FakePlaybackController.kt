package com.example.rockstar.playback

import com.example.rockstar.model.Song
import com.example.rockstar.viewmodel.PlaybackConnectionState
import com.example.rockstar.viewmodel.PlaybackUiState
import com.example.rockstar.viewmodel.RockstarRepeatMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FakePlaybackController : PlaybackController {
    private val state = MutableStateFlow(PlaybackUiState())
    override val uiState: StateFlow<PlaybackUiState> = state

    var playQueueCalls = 0
    var toggleCalls = 0
    var nextCalls = 0
    var previousCalls = 0
    var lastSeek: Long? = null
    var lastRemovedIndex: Int? = null

    override fun connect() {
        state.value = state.value.copy(connectionState = PlaybackConnectionState.Connected)
    }

    override fun disconnect() {
        state.value = state.value.copy(connectionState = PlaybackConnectionState.Disconnected)
    }

    override fun playQueue(queue: List<Song>, startIndex: Int) {
        playQueueCalls++
        val safeIndex = startIndex.coerceIn(0, queue.lastIndex)
        state.value = state.value.copy(
            queue = queue,
            currentIndex = safeIndex,
            currentSong = queue[safeIndex],
            currentMediaId = queue[safeIndex].id.toString(),
            durationMs = queue[safeIndex].durationMs,
            canSeek = true
        )
    }

    override fun togglePlayPause() {
        toggleCalls++; state.value = state.value.copy(isPlaying = !state.value.isPlaying)
    }

    override fun play() {
        state.value = state.value.copy(isPlaying = true)
    }

    override fun pause() {
        state.value = state.value.copy(isPlaying = false)
    }

    override fun seekTo(positionMs: Long) {
        lastSeek = positionMs; state.value = state.value.copy(positionMs = positionMs)
    }

    override fun seekToNext() {
        nextCalls++
    }

    override fun seekToPrevious() {
        previousCalls++
    }

    override fun skipToQueueItem(index: Int) {
        state.value.queue.getOrNull(index)?.let {
            state.value = state.value.copy(currentIndex = index, currentSong = it, currentMediaId = it.id.toString())
        }
    }

    override fun setShuffleEnabled(enabled: Boolean) {
        state.value = state.value.copy(shuffleEnabled = enabled)
    }

    override fun setRepeatMode(mode: RockstarRepeatMode) {
        state.value = state.value.copy(repeatMode = mode)
    }

    override fun removeQueueItem(index: Int) {
        lastRemovedIndex = index; if (index !in state.value.queue.indices) return;
        val q = state.value.queue.toMutableList().also { it.removeAt(index) }; state.value =
            if (q.isEmpty()) PlaybackUiState(connectionState = state.value.connectionState) else state.value.copy(
                queue = q,
                currentIndex = state.value.currentIndex.coerceAtMost(q.lastIndex),
                currentSong = q[state.value.currentIndex.coerceAtMost(q.lastIndex)]
            )
    }

    override fun clearQueue() {
        state.value = PlaybackUiState(connectionState = state.value.connectionState)
    }

    override fun stop() = clearQueue()

    fun emit(newState: PlaybackUiState) {
        state.value = newState
    }
}
