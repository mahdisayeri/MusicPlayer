package com.tokastudio.music_offline.model.dataBase

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.tokastudio.music_offline.model.Track

@Dao
interface TrackDao{

    @Insert
    suspend fun insertTracks(tracks : List<Track>)

    @Insert
    suspend  fun insertTrack(track : Track)

    @Query("SELECT * FROM tracks WHERE id =:id")
    fun getTrack(id : Int) : Track

    @Query("SELECT * FROM tracks")
    fun getAllTracks() :LiveData<List<Track>>

}