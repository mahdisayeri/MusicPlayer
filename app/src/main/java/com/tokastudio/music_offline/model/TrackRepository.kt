package com.tokastudio.music_offline.model

import androidx.lifecycle.LiveData

class TrackRepository (trackDao: TrackDao){
    val tracks: LiveData<List<Song>> = trackDao.getAllTracks()
    
}