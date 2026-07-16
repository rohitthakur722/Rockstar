package com.example.rockstar.playback

import android.content.ComponentName
import android.content.Context
import androidx.core.content.ContextCompat
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.rockstar.model.Song
import com.example.rockstar.viewmodel.PlaybackConnectionState
import com.example.rockstar.viewmodel.PlaybackUiState
import com.example.rockstar.viewmodel.RockstarPlaybackState
import com.example.rockstar.viewmodel.RockstarRepeatMode
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class Media3PlaybackController(context: Context) : PlaybackController {
    private val appContext = context.applicationContext
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val _uiState = MutableStateFlow(PlaybackUiState())
    override val uiState: StateFlow<PlaybackUiState> = _uiState.asStateFlow()

    private var controllerFuture: ListenableFuture<MediaController>? = null
    private var controller: MediaController? = null
    private var progressJob: Job? = null
    private var lastQueue: List<Song> = emptyList()
    private val stateStore = PlaybackStateStore(appContext)

    private val listener = object : Player.Listener {
        override fun onEvents(player: Player, events: Player.Events) {
            syncFromPlayer(player)
        }

        override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
            _uiState.update {
                it.copy(
                    playbackState = RockstarPlaybackState.Error,
                    errorMessage = mapPlaybackError(error.message),
                    isPlaying = false
                )
            }
        }
    }

    override fun connect() {
        if (controller != null || controllerFuture != null) return
        _uiState.update { it.copy(connectionState = PlaybackConnectionState.Connecting) }
        val token = SessionToken(appContext, ComponentName(appContext, PlaybackService::class.java))
        val future = MediaController.Builder(appContext, token).buildAsync()
        controllerFuture = future
        future.addListener(
            {
                runCatching { future.get() }
                    .onSuccess { mediaController ->
                        controller = mediaController
                        mediaController.addListener(listener)
                        _uiState.update { it.copy(connectionState = PlaybackConnectionState.Connected) }
                        restoreQueueIfAvailable(mediaController)
                        syncFromPlayer(mediaController)
                    }
                    .onFailure { error ->
                        _uiState.update {
                            it.copy(
                                connectionState = PlaybackConnectionState.Failed,
                                errorMessage = mapPlaybackError(error.message)
                            )
                        }
                    }
            },
            ContextCompat.getMainExecutor(appContext)
        )
    }

    override fun disconnect() {
        progressJob?.cancel()
        progressJob = null
        controller?.removeListener(listener)
        controllerFuture?.let(MediaController::releaseFuture)
        controller = null
        controllerFuture = null
        _uiState.update { it.copy(connectionState = PlaybackConnectionState.Disconnected) }
    }

    override fun playQueue(queue: List<Song>, startIndex: Int) {
        val mediaController = controller ?: return
        val safeQueue = queue.filter { it.toMediaItemOrNull() != null }
        if (safeQueue.isEmpty()) return
        val safeIndex = startIndex.coerceIn(0, safeQueue.lastIndex)
        val mediaItems = safeQueue.mapNotNull(Song::toMediaItemOrNull)
        lastQueue = safeQueue
        mediaController.setMediaItems(mediaItems, safeIndex, C.TIME_UNSET)
        mediaController.prepare()
        mediaController.play()
        _uiState.update { it.copy(queue = safeQueue, currentIndex = safeIndex, currentSong = safeQueue[safeIndex]) }
        persist(mediaController)
        syncFromPlayer(mediaController)
    }

    override fun togglePlayPause() {
        val mediaController = controller ?: return
        if (mediaController.isPlaying) mediaController.pause() else mediaController.play()
    }

    override fun play() {
        controller?.play()
    }

    override fun pause() {
        controller?.pause()
    }

    override fun seekTo(positionMs: Long) {
        val mediaController = controller ?: return
        val duration = mediaController.duration.takeIf { it != C.TIME_UNSET } ?: 0L
        mediaController.seekTo(
            positionMs.coerceAtLeast(0L).let { if (duration > 0L) it.coerceAtMost(duration) else it })
        syncFromPlayer(mediaController)
    }

    override fun seekToNext() {
        controller?.seekToNextMediaItem()
    }

    override fun seekToPrevious() {
        controller?.seekToPreviousMediaItem()
    }

    override fun skipToQueueItem(index: Int) {
        val mediaController = controller ?: return
        if (index in 0 until mediaController.mediaItemCount) mediaController.seekToDefaultPosition(index)
    }

    override fun setShuffleEnabled(enabled: Boolean) {
        controller?.shuffleModeEnabled = enabled
    }

    override fun setRepeatMode(mode: RockstarRepeatMode) {
        controller?.repeatMode = when (mode) {
            RockstarRepeatMode.Off -> Player.REPEAT_MODE_OFF
            RockstarRepeatMode.All -> Player.REPEAT_MODE_ALL
            RockstarRepeatMode.One -> Player.REPEAT_MODE_ONE
        }
    }

    override fun removeQueueItem(index: Int) {
        val mediaController = controller ?: return
        if (index !in lastQueue.indices) return
        val nextQueue = lastQueue.toMutableList().also { it.removeAt(index) }
        if (nextQueue.isEmpty()) {
            clearQueue()
            return
        }
        lastQueue = nextQueue
        mediaController.removeMediaItem(index)
        syncFromPlayer(mediaController)
    }

    override fun clearQueue() {
        val mediaController = controller
        mediaController?.stop()
        mediaController?.clearMediaItems()
        lastQueue = emptyList()
        stateStore.clear()
        _uiState.value = PlaybackUiState(connectionState = _uiState.value.connectionState)
        progressJob?.cancel()
    }

    override fun stop() {
        controller?.stop()
        clearQueue()
    }

    private fun syncFromPlayer(player: Player) {
        val index = player.currentMediaItemIndex.takeIf { it >= 0 } ?: -1
        val currentSong = lastQueue.getOrNull(index)
        val duration =
            player.duration.takeIf { it != C.TIME_UNSET }?.coerceAtLeast(0L) ?: currentSong?.durationMs?.coerceAtLeast(
                0L
            ) ?: 0L
        _uiState.update {
            it.copy(
                connectionState = PlaybackConnectionState.Connected,
                queue = lastQueue,
                currentIndex = index,
                currentMediaId = player.currentMediaItem?.mediaId,
                currentSong = currentSong,
                isPlaying = player.isPlaying,
                playbackState = player.playbackState.toRockstarState(),
                positionMs = player.currentPosition.coerceAtLeast(0L),
                durationMs = duration,
                bufferedPositionMs = player.bufferedPosition.coerceAtLeast(0L),
                shuffleEnabled = player.shuffleModeEnabled,
                repeatMode = player.repeatMode.toRockstarRepeatMode(),
                canSeek = duration > 0L,
                canSkipPrevious = player.hasPreviousMediaItem() || player.currentPosition > 3_000L,
                canSkipNext = player.hasNextMediaItem(),
                errorMessage = if (player.playbackState == Player.STATE_IDLE) it.errorMessage else null
            )
        }
        persist(player)
        updateProgressTicker(player.isPlaying && currentSong != null)
    }

    private fun restoreQueueIfAvailable(mediaController: MediaController) {
        val restored = stateStore.restore() ?: return
        val mediaItems = restored.queue.mapNotNull(Song::toMediaItemOrNull)
        if (mediaItems.isEmpty()) {
            stateStore.clear()
            return
        }
        lastQueue = restored.queue
        mediaController.setMediaItems(mediaItems, restored.currentIndex, restored.positionMs)
        mediaController.shuffleModeEnabled = restored.shuffle
        mediaController.repeatMode = when (restored.repeatMode) {
            RockstarRepeatMode.Off -> Player.REPEAT_MODE_OFF
            RockstarRepeatMode.All -> Player.REPEAT_MODE_ALL
            RockstarRepeatMode.One -> Player.REPEAT_MODE_ONE
        }
        mediaController.prepare()
        mediaController.pause()
    }

    private fun persist(player: Player) {
        if (lastQueue.isEmpty()) return
        stateStore.save(
            queue = lastQueue,
            currentIndex = player.currentMediaItemIndex.takeIf { it >= 0 } ?: 0,
            positionMs = player.currentPosition,
            shuffle = player.shuffleModeEnabled,
            repeatMode = player.repeatMode.toRockstarRepeatMode()
        )
    }

    private fun updateProgressTicker(active: Boolean) {
        if (!active) {
            progressJob?.cancel()
            progressJob = null
            return
        }
        if (progressJob?.isActive == true) return
        progressJob = scope.launch {
            while (true) {
                delay(750L)
                controller?.let(::syncFromPlayer)
            }
        }
    }

    private fun Int.toRockstarState(): RockstarPlaybackState = when (this) {
        Player.STATE_BUFFERING -> RockstarPlaybackState.Buffering
        Player.STATE_READY -> RockstarPlaybackState.Ready
        Player.STATE_ENDED -> RockstarPlaybackState.Ended
        else -> RockstarPlaybackState.Idle
    }

    private fun Int.toRockstarRepeatMode(): RockstarRepeatMode = when (this) {
        Player.REPEAT_MODE_ALL -> RockstarRepeatMode.All
        Player.REPEAT_MODE_ONE -> RockstarRepeatMode.One
        else -> RockstarRepeatMode.Off
    }
}
