package com.tokastudio.music_offline.ui.fragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tokastudio.music_offline.model.Track

class PageViewModel : ViewModel() {

    private val _sectionNumber = MutableLiveData<String>()
    val sectionNumber: LiveData<String> = _sectionNumber

    fun setIndex(value: String) {
        _sectionNumber.value = value
    }

//    private val _songs = MutableLiveData<List<Track>>()
//    val songs: LiveData<List<Track>> = _songs
//
//    fun setSongs(tracks: ArrayList<Track>) {
//        _songs.value = tracks
//    }
}