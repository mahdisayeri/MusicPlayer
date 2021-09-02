package com.tokastudio.music_offline.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Parcelize
@Entity(tableName = "tracks")
data class Track(
                // @ColumnInfo(name = "media_store_id") val mediaStoreId: Long,
        @ColumnInfo(name = "track_id") @PrimaryKey val trackId: Long,
        @ColumnInfo(name = "title") var title: String?,
        @ColumnInfo(name = "artist") var artist: String?,
        @ColumnInfo(name = "path") var path: String?,
        @ColumnInfo(name = "duration") val duration: Int?,
        @ColumnInfo(name = "album") val album: String?,
        @ColumnInfo(name = "lyric") val lyric: String?,
        @ColumnInfo(name = "playlist_id") val playListId: Int,
        @ColumnInfo(name = "fromAsset") val fromAsset: Boolean
                 ) : Serializable , Parcelable {
//    constructor(parcel: Parcel) : this(
//            parcel.readLong(),
//            parcel.readString(),
//            parcel.readString(),
//            parcel.readString(),
//            parcel.readInt(),
//            parcel.readString(),
//            parcel.readString(),
//            parcel.readInt()) {
//    }
//
//    override fun writeToParcel(parcel: Parcel, flags: Int) {
//        parcel.writeLong(trackId)
//        parcel.writeString(title)
//        parcel.writeString(artist)
//        parcel.writeString(path)
//        if (duration != null) {
//            parcel.writeInt(duration)
//        }
//        parcel.writeString(album)
//        parcel.writeString(lyric)
//        parcel.writeInt(playListId)
//    }
//
//    override fun describeContents(): Int {
//        return 0
//    }
//
//    companion object CREATOR : Parcelable.Creator<Track> {
//        override fun createFromParcel(parcel: Parcel): Track {
//            return Track(parcel)
//        }
//
//        override fun newArray(size: Int): Array<Track?> {
//            return arrayOfNulls(size)
//        }
//    }
}

