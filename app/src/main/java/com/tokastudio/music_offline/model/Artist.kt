package com.tokastudio.music_offline.model

import android.os.Parcelable
import androidx.room.Entity
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "songs")
@Parcelize
data class Artist(
        val data: String? = null,
        val artistId: Long = 0,
        val artistName: String? = null,
        val track: List<Track>
): Parcelable
