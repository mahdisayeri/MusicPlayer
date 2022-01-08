package com.tokastudio.music_offline.model.dataBase

import android.app.Application
import android.content.res.AssetFileDescriptor
import android.media.MediaMetadataRetriever
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.tokastudio.music_offline.R
import com.tokastudio.music_offline.model.Track
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.IOException

@Database(entities = [Track::class],version = 1,exportSchema = false)
abstract class MyDataBase : RoomDatabase() {

    abstract fun trackDao() : TrackDao

    companion object{

        private const val TAG="MyDataBase"
        @Volatile
        private var instance : MyDataBase? = null

        fun getDataBase(application: Application ,scope: CoroutineScope ) : MyDataBase {
            return instance ?: synchronized(this){
               instance ?: buildDataBase(application,scope).also { instance =it }
            }
        }

        private fun buildDataBase(application: Application,scope: CoroutineScope) : MyDataBase {
            return Room.databaseBuilder(application, MyDataBase::class.java,"myDataBase.db")
                    .addCallback(MyDataBaseCallBack(scope,application))
                    .build()
        }

    }

    private class MyDataBaseCallBack(private val scope: CoroutineScope,val application: Application): RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            instance?.let { myDataBase ->
                scope.launch {
                    val trackDao = myDataBase.trackDao()
                  //  trackDao.insertTracks(prePopulateData(application))
                }
            }
        }

        private fun prePopulateData(application: Application) : List<Track> {
            Log.d(TAG,"prePopulateData")
            //val trackTitle =application.resources.getStringArray(R.array.track_title)
           // val trackLyric = application.resources.getStringArray(R.array.track_lyric)
            val album = application.resources.getString(R.string.album_name)
            val artist = application.resources.getString(R.string.artist_name)


            val tracks = ArrayList<Track>()
            val metaRetriever = MediaMetadataRetriever()
            val path = "tracks"
            var files = arrayOfNulls<String>(0)
            try {
                files = application.assets.list(path) as Array<String?>
            } catch (e: IOException) {
                e.printStackTrace()
            }
            Log.d(TAG, "size"+files.size)
            for ((i, s) in files.withIndex()) {
                val file = "$path/$s"
                var afd: AssetFileDescriptor? = null
                try {
                    afd = application.assets.openFd(file)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                assert(afd != null)
                metaRetriever.setDataSource(afd!!.fileDescriptor, afd.startOffset, afd.length)

                val duration= metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toInt()?.div(1000)
//                val track = Track(
//                        i.toLong()
//                        , trackTitle[i]
//                        , artist
//                        , file
//                        , duration
//                        , metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM)
//                        ,trackLyric[i]
//                        ,0
//                        ,true
//                )
                val track = Track(
                            i.toLong(),
                        //    trackTitle[i],
                        "",
                            i,
                            0,
                            0,
                            file,
                            0,
                            0,
                            "",
                            0,
                            artist,
                          //  trackLyric[i],
                        "",
                            isPlaying = false,
                            inAssets = true
                    )

                try {
                    afd.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                tracks.add(track)
            }
            metaRetriever.release()
            return tracks

        }

    }

}