package com.example.rockstar.viewmodel

import com.example.rockstar.model.Song

enum class PlaybackConnectionState { Disconnected, Connecting, Connected, Failed }

enum class RockstarPlaybackState { Idle, Buffering, Ready, Ended, Error }

enum class RockstarRepeatMode { Off, All, One }

data class PlaybackUiState(
    val connectionState: PlaybackConnectionState = PlaybackConnectionState.Disconnected,
    val queue: List<Song> = emptyList(),
    val currentIndex: Int = -1,
    val currentMediaId: String? = null,
    val currentSong: Song? = null,
    val isPlaying: Boolean = false,
    val playbackState: RockstarPlaybackState = RockstarPlaybackState.Idle,
    val positionMs: Long = 0L,
    val durationMs: Long = 0L,
    val bufferedPositionMs: Long = 0L,
    val shuffleEnabled: Boolean = false,
    val repeatMode: RockstarRepeatMode = RockstarRepeatMode.Off,
    val canSeek: Boolean = false,
    val canSkipPrevious: Boolean = false,
    val canSkipNext: Boolean = false,
    val errorMessage: String? = null
) {
    val hasActiveMedia: Boolean = currentSong != null
}
