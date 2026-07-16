package com.example.rockstar.playback

fun mapPlaybackError(message: String?): String = when {
    message.isNullOrBlank() -> "Rockstar could not play this audio file."
    message.contains("permission", ignoreCase = true) -> "Audio access was removed. Grant permission to continue."
    message.contains("not found", ignoreCase = true) || message.contains(
        "deleted",
        ignoreCase = true
    ) -> "This song is no longer available."

    else -> "Rockstar could not play this audio file."
}
