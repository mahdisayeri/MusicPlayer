package com.tokastudio.music_offline.ui

import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.tokastudio.music_offline.model.CurrentPlayingSong
import com.tokastudio.music_offline.model.Track
import com.tokastudio.music_offline.service.TrackService
import com.tokastudio.music_offline.ui.fragment.SplashScreenFragmentDirections
import kotlin.coroutines.coroutineContext

class MainViewModel: ViewModel() {
    private val _trackService= MutableLiveData<TrackService>()
    val trackService: LiveData<TrackService> = _trackService

    fun setTrackService(trackService: TrackService){
        _trackService.value= trackService
    }

    private val _currentPlayingSong= MutableLiveData<CurrentPlayingSong>()
    val currentPlayingSong: LiveData<CurrentPlayingSong> = _currentPlayingSong

    fun setCurrentSong(item: CurrentPlayingSong){
        _currentPlayingSong.value= item
    }

    private val _tracks= MutableLiveData<List<Track>>()
    val tracks: LiveData<List<Track>> = _tracks

    fun setTracks(items: List<Track>){
        _tracks.value= items
    }

    private val _favListId= MutableLiveData<List<Long>>()
    val favListId: LiveData<List<Long>> = _favListId

    fun setFavListId(items: List<Long>){
        _favListId.value= items
    }
}