package com.tokastudio.music_offline.model

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TrackDao{

    @Insert
    suspend fun insertTracks(tracks : List<Song>)

    @Insert
    suspend  fun insertTrack(track : Song)

    @Query("SELECT * FROM songs WHERE id =:id")
    fun getTrack(id : Int) : Song

    @Query("SELECT * FROM songs")
    fun getAllTracks() :LiveData<List<Song>>

}