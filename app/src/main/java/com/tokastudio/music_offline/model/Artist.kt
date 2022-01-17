package com.tokastudio.music_offline.model

import android.os.Parcelable
import androidx.room.Entity
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "songs")
@Parcelize
data class Artist(
        val artistId: Long = 0,
        val artistName: String? = null,
        val tracks: List<Track>
): Parcelable
