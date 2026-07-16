package com.example.rockstar.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.rockstar.ui.theme.RockstarTextSecondary
import com.example.rockstar.util.formatDuration

@Composable
fun PlaybackProgressSlider(
    positionMs: Long,
    durationMs: Long,
    canSeek: Boolean,
    onSeek: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val safeDuration = durationMs.coerceAtLeast(0L)
    var dragging by remember { mutableStateOf(false) }
    var dragValue by remember { mutableFloatStateOf(0f) }
    val progress = if (safeDuration > 0L) positionMs.toFloat() / safeDuration else 0f
    val shownProgress = if (dragging) dragValue else progress.coerceIn(0f, 1f)
    val shownPosition = (shownProgress * safeDuration).toLong()

    Column(modifier = modifier) {
        Slider(
            value = shownProgress,
            onValueChange = {
                dragging = true
                dragValue = it.coerceIn(0f, 1f)
            },
            onValueChangeFinished = {
                dragging = false
                onSeek((dragValue * safeDuration).toLong())
            },
            enabled = canSeek && safeDuration > 0L
        )
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(formatDuration(shownPosition), color = RockstarTextSecondary, modifier = Modifier.weight(1f))
            Text(formatDuration(safeDuration), color = RockstarTextSecondary)
        }
    }
}
