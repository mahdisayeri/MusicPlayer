package com.tokastudio.music_offline.model
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "tracks")
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
        var inAssets: Boolean,
): Parcelable{

    @Ignore
    private var cover: Bitmap?= null

    fun getCover(): Bitmap?{
        if (cover!= null && data!= null)
            return cover
           val retriever = MediaMetadataRetriever()
           retriever.setDataSource(this.data)
           val coverBytes = retriever.embeddedPicture
           cover= if (coverBytes != null) //se l'array di byte non è vuoto, crea una bitmap
               BitmapFactory.decodeByteArray(coverBytes, 0, coverBytes.size) else null
        return cover
       }
}
