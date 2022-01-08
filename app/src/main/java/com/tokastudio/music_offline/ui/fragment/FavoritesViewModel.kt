package com.tokastudio.music_offline.ui.fragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tokastudio.music_offline.model.Track

class FavoritesViewModel : ViewModel() {

    private val _favList= MutableLiveData<List<Track>>()
    val favList: LiveData<List<Track>> = _favList

    fun setFavList(items: List<Track>){
        _favList.value= items
    }

}