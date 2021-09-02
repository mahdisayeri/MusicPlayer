package com.tokastudio.music_offline

import com.tokastudio.music_offline.model.Song

interface TrackControllerB : TrackControllerA {
    fun onChangePlayPauseButton(isPlaying: Boolean)
    fun onTrackUpdateUi(track: Song?)
}