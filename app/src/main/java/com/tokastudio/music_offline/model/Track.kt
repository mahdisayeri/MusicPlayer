package com.tokastudio.music_offline.model

import android.media.MediaMetadataRetriever
import android.os.Parcelable
import android.util.Log
import androidx.room.ColumnInfo
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

//@Entity(tableName = "tracks")
@Parcelize
data class Track(
        @ColumnInfo(name = "id") @PrimaryKey val id: Long,
        val title: String = "",
        val trackNumber: Int = 0,
        val year: Int = 0,
        val duration: Long = 0,
        val data: String? = null,
        val dateModified: Long = 0,
        val albumId: Long = 0,
        val albumName: String= "",
        val artistId: Long = 0,
        val artistName: String= "",
        val lyric: String= "",
        var isPlaying: Boolean= false,
        var inAssets: Boolean= false,
        var cover: ByteArray?= null
) : Parcelable {

//    @Ignore
//    private var cover: ByteArray? = null
//    fun getCover(): Uri {
//        val sArtworkUri: Uri = Uri
//                .parse("content://media/external/audio/albumart")
//        return ContentUris.withAppendedId(sArtworkUri,albumId)
//    }

    //    fun getCover(): ByteArray? {
//        return if (cover != null || data.isNullOrEmpty()){
//            cover
//        } else{
//            Log.d("loadCover= ","true")
//            try{
//                val retriever = MediaMetadataRetriever()
//                retriever.setDataSource(this.data)
//                retriever.embeddedPicture
//            }catch (e: Exception){
//                println("Exception of type : $e")
//                null
//            }
//        }
//    }
//    override fun equals(other: Any?): Boolean {
//        if (this === other) return true
//        if (javaClass != other?.javaClass) return false
//
//        other as Track
//
//        if (artistId != other.artistId) return false
//        if (cover != null) {
//            if (other.cover == null) return false
//            if (!cover.contentEquals(other.cover)) return false
//        } else if (other.cover != null) return false
//
//        return true
//    }
//
//    override fun hashCode(): Int {
//        var result = artistId.hashCode()
//        result = 31 * result + (cover?.contentHashCode() ?: 0)
//        return result
//    }
}