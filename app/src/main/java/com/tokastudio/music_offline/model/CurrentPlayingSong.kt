package com.tokastudio.music_offline.model

data class CurrentPlayingSong(
        val position: Int,
        val song: Song,
        var hideCurrentPlayingLayout: Boolean= true
)
