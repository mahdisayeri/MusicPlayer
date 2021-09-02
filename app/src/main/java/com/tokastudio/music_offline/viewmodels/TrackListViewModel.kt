package com.tokastudio.music_offline.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.tokastudio.music_offline.model.MyDataBase
import com.tokastudio.music_offline.model.Song
import com.tokastudio.music_offline.model.TrackRepository

class TrackListViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TrackRepository
    private val tracks:LiveData<List<Song>>
    init {
        val trackDao=MyDataBase.getDataBase(application,viewModelScope).trackDao()
        repository= TrackRepository(trackDao)
        tracks=repository.tracks
    }
    fun getTracks(): LiveData<List<Song>>{
        return tracks
    }
}