package com.tokastudio.music_offline.ui.fragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tokastudio.music_offline.model.Track

class ArtistViewModel : ViewModel() {
    private val _tracks= MutableLiveData<List<Track>>()
    val tracks: LiveData<List<Track>> = _tracks

    fun setTracks(tracks: List<Track>){
        _tracks.value= tracks
    }
}