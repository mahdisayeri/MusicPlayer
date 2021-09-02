package com.tokastudio.music_offline.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "songs")
@Parcelize
data class Artist(
        val data: String? = null,
        val artistId: Long = 0,
        val artistName: String? = null,
        val song: List<Song>
): Parcelable
