package com.example.rockstar.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.rockstar.R
import com.example.rockstar.viewmodel.RockstarRepeatMode

@Composable
fun PlaybackControls(
    isPlaying: Boolean,
    canSkipPrevious: Boolean,
    canSkipNext: Boolean,
    shuffleEnabled: Boolean,
    repeatMode: RockstarRepeatMode,
    onShuffle: () -> Unit,
    onPrevious: () -> Unit,
    onTogglePlayPause: () -> Unit,
    onNext: () -> Unit,
    onRepeat: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onShuffle) {
            Icon(
                Icons.Default.Shuffle,
                contentDescription = stringResource(R.string.cd_shuffle)
            )
        }
        IconButton(onClick = onPrevious, enabled = canSkipPrevious) {
            Icon(
                Icons.Default.SkipPrevious,
                contentDescription = stringResource(R.string.cd_previous)
            )
        }
        IconButton(onClick = onTogglePlayPause) {
            Icon(
                if (isPlaying) Icons.Default.PauseCircle else Icons.Default.PlayCircle,
                contentDescription = stringResource(if (isPlaying) R.string.cd_pause else R.string.cd_play)
            )
        }
        IconButton(onClick = onNext, enabled = canSkipNext) {
            Icon(
                Icons.Default.SkipNext,
                contentDescription = stringResource(R.string.cd_next)
            )
        }
        IconButton(onClick = onRepeat) {
            Icon(
                if (repeatMode == RockstarRepeatMode.One) Icons.Default.RepeatOne else Icons.Default.Repeat,
                contentDescription = stringResource(R.string.cd_repeat)
            )
        }
    }
}
