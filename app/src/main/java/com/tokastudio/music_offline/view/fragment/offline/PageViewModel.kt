package com.tokastudio.music_offline.view.fragment.offline

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tokastudio.music_offline.model.Song

class PageViewModel : ViewModel() {

    private val _sectionNumber = MutableLiveData<String>()
    val sectionNumber: LiveData<String> = _sectionNumber

    fun setIndex(value: String) {
        _sectionNumber.value = value
    }

    private val _songs = MutableLiveData<List<Song>>()
    val songs: LiveData<List<Song>> = _songs

    fun setSongs(songs: ArrayList<Song>) {
        _songs.value = songs
    }
}