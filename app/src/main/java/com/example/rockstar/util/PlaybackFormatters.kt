package com.example.rockstar.util

fun clampPosition(positionMs: Long, durationMs: Long): Long {
    val safeDuration = durationMs.coerceAtLeast(0L)
    return positionMs.coerceAtLeast(0L).let { position ->
        if (safeDuration > 0L) position.coerceAtMost(safeDuration) else position
    }
}
