package com.tokastudio.music_offline

interface TrackControllerA {
    fun onTrackPrevious()
    fun onTrackPlay(pos: Int)
    fun onTrackPause()
    fun onTrackNext()
    fun onTrackRelease()
}