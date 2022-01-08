package com.tokastudio.music_offline.model

import androidx.lifecycle.LiveData
import com.tokastudio.music_offline.model.dataBase.TrackDao

class TrackRepository (trackDao: TrackDao){
    val tracks: LiveData<List<Track>> = trackDao.getAllTracks()
    
}