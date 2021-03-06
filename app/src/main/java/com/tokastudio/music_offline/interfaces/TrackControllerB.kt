package com.tokastudio.music_offline.interfaces

import com.tokastudio.music_offline.model.Track

interface TrackControllerB : TrackControllerA {
    fun onChangePlayPauseButton(isPlaying: Boolean)
    fun onTrackUpdateUi(track: Track?)
}