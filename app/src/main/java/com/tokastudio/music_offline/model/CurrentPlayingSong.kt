package com.tokastudio.music_offline.model

data class CurrentPlayingSong(
        val position: Int,
        val track: Track,
        var hideCurrentPlayingLayout: Boolean= true
)
