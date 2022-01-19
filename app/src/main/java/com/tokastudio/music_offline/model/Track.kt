package com.tokastudio.music_offline.model

import android.media.MediaMetadataRetriever
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

//@Entity(tableName = "tracks")
@Parcelize
data class Track(
        @ColumnInfo(name = "id") @PrimaryKey val id: Long,
        val title: String? = null,
        val trackNumber: Int = 0,
        val year: Int = 0,
        val duration: Long = 0,
        val data: String? = null,
        val dateModified: Long = 0,
        val albumId: Long = 0,
        val albumName: String? = null,
        val artistId: Long = 0,
        val artistName: String? = null,
        val lyric: String? = null,
        var isPlaying: Boolean,
        var inAssets: Boolean
) : Parcelable {

    @Ignore
    private var cover: ByteArray? = null
//    fun getCover(): Uri {
//        val sArtworkUri: Uri = Uri
//                .parse("content://media/external/audio/albumart")
//        return ContentUris.withAppendedId(sArtworkUri,albumId)
//    }

    fun getCover(): ByteArray? {
        return if (cover != null || data.isNullOrEmpty()){
            cover
        }else{
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(this.data)
            retriever.embeddedPicture
        }
    }
}