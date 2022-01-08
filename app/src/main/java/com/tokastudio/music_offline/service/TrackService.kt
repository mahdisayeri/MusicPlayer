package com.tokastudio.music_offline.service

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.AssetFileDescriptor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.AudioManager
import android.media.AudioManager.*
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.media.MediaPlayer.OnCompletionListener
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.tokastudio.music_offline.Constants
import com.tokastudio.music_offline.R
import com.tokastudio.music_offline.TrackControllerA
import com.tokastudio.music_offline.model.Track
import com.tokastudio.music_offline.ui.MainActivity
import java.io.IOException
import java.util.*


class TrackService : Service(), TrackControllerA ,MediaPlayer.OnErrorListener
        ,MediaPlayer.OnPreparedListener , AudioManager.OnAudioFocusChangeListener, OnCompletionListener {

    companion object {
        private val TAG = TrackService::class.java.simpleName
    }
    private var trackList: List<Track>?= null
    private val binder: IBinder = LocalBinder()
    var mediaPlayer: MediaPlayer? = null
        private set
    private var context: Context? = null
    private var  audioManager: AudioManager? = null
    var currentPlayingList: String? =null

    private var notificationManager: NotificationManager? = null
    var currentTrack: Track? = null
        private set
    var currentPosition = 0
        private set
    var isPlaying = false
        private set
    var repeat = 0
    var isShuffle = false
    var currentTrackCover: Bitmap? = null
    private var mPrevAudioFocusState = 0
    private var mWasPlayingAtFocusLost = false
    private var broadcastReceiver: BroadcastReceiver? = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.extras!!.getString(Constants.ACTION_NAME)) {
                Constants.ACTION_PREVIOUS -> onTrackPrevious()
                Constants.ACTION_PLAY_PAUSE -> if (isPlaying) {
                    onTrackPause()
                } else {
                    onTrackPlay(currentPosition)
                }
                Constants.ACTION_NEXT -> onTrackNext()
                Constants.ACTION_STOP ->  {
                    onTrackPause()
                    stopSelf()
                    stopForeground(true)

                }
            }
        }
    }

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    inner class LocalBinder : Binder() {
        val service: TrackService
            get() =// Return this instance of LocalService so clients can call public methods
                this@TrackService
    }

    override fun onBind(intent: Intent): IBinder? {
        Log.i(TAG, "onBind")
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        audioManager= getSystemService(Context.AUDIO_SERVICE) as AudioManager?
        audioManager?.requestAudioFocus(this,AudioManager.STREAM_MUSIC,AudioManager.AUDIOFOCUS_GAIN)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.i(TAG, "onStartCommand")
       // trackList = intent.getParcelableArrayListExtra(Utils.TRACK_LIST)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChanel()
        }
        currentPosition = 0
        registerReceiver(broadcastReceiver, IntentFilter(Constants.ACTION_SERVICE))
        return START_NOT_STICKY
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        Log.i(TAG, "onTaskRemoved")
        if (!isPlaying) stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "onDestroy")
        audioManager?.abandonAudioFocus(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && notificationManager != null) {
            notificationManager!!.cancelAll()
        }
        if (mediaPlayer != null) {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        }
        currentPlayingList=null
        if (broadcastReceiver != null) unregisterReceiver(broadcastReceiver)
    }

    private fun setupTrack(track: Track){
       initialMediaPlayer()
       if (track.inAssets){
           val afd = track.data?.let { assets.openFd(it) }
           mediaPlayer?.apply {
               reset()
               if (afd != null) {
                   setDataSource(afd.fileDescriptor,afd.startOffset,afd.length)
               }
               setOnPreparedListener(null)
               prepare()
               start()
           }
       } else{
           try {
                mediaPlayer?.apply {
                    reset()
                    setDataSource(track.data)
                    setOnPreparedListener(null)
                    prepare()
                    start()

                }
                } catch (e: IllegalArgumentException) {
                    e.printStackTrace()
                } catch (e: Exception) {
                    println("Exception of type : $e")
                    e.printStackTrace()
                }
       }


    }

    private fun initialMediaPlayer() {
        if(mediaPlayer!=null){
            return
        }
       mediaPlayer=MediaPlayer().apply {
          setWakeMode(applicationContext,PowerManager.PARTIAL_WAKE_LOCK)
           setOnPreparedListener(this@TrackService)
           setOnCompletionListener(this@TrackService)
           setOnErrorListener(this@TrackService)
       }
    }

    fun setTrackList(trackList: List<Track>){
        this.trackList=trackList
    }
    fun getTrackList(): List<Track>? {
        return trackList
    }

    private fun getTrackCover(track: Track): Bitmap? {
        return if (track.inAssets){
            track.data?.let { fetchAssetSongCover(it) }
        }else{
            track.data?.let { fetchStorageSongCover(it) }
        }

    }

    private fun fetchAssetSongCover(path: String): Bitmap?{
        val metaRetriever = MediaMetadataRetriever()
        var afd: AssetFileDescriptor? = null
        try {
            afd = resources.assets.openFd(path)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        assert(afd != null)
        metaRetriever.setDataSource(afd!!.fileDescriptor, afd.startOffset, afd.length)

        val rawArt= metaRetriever.embeddedPicture
        var coverBitmap: Bitmap?=null
        if (rawArt != null) {
            val options = BitmapFactory.Options()
            val bitmap = BitmapFactory.decodeByteArray(rawArt, 0, rawArt.size, options)
            if (bitmap != null) {
                coverBitmap = if (bitmap.height > 220 * 2) {
                    val ratio = bitmap.width / bitmap.height.toFloat()
                    Bitmap.createScaledBitmap(bitmap, (220 * ratio).toInt(), 220, false)
                } else {
                    bitmap
                }
            }
        }
        currentTrackCover=coverBitmap
        return coverBitmap
    }

    private fun fetchStorageSongCover(path: String): Bitmap?{
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(path)
            val coverBytes = retriever.embeddedPicture
            val songCover: Bitmap?= if (coverBytes != null) //se l'array di byte non è vuoto, crea una bitmap
                BitmapFactory.decodeByteArray(coverBytes, 0, coverBytes.size) else null

            currentTrackCover=songCover
            return songCover
//            val trackCover: BitmapDrawable
//            if (songCover!= null){
//                trackCover = BitmapDrawable(view.context.resources, songCover)
//                //val uri: Uri? = null
//                Glide.with(view.context)
//                        .load(trackCover)
//                        .fitCenter()
//                        .diskCacheStrategy(DiskCacheStrategy.NONE)
//                        .apply(RequestOptions.circleCropTransform())
//                        .placeholder(R.drawable.music_placeholder2)
//                        .into(view)
//            }else{
//                view.setImageResource(R.drawable.music_placeholder2)
//            }
    }

    override fun onTrackPrevious() {
        if (trackList?.isNotEmpty() == true) {
            currentPosition--
            if (currentPosition == -1) currentPosition = trackList!!.size - 1
            currentTrack = trackList!![currentPosition]
            setupTrack(trackList!![currentPosition])
            createNotification(this, trackList!![currentPosition]
                    , R.drawable.ic_pause, currentPosition, trackList!!.size)
            mediaPlayer?.start()
        }
    }

    override fun onTrackPlay(pos: Int) {
      if(trackList?.isNotEmpty() == true){
          currentPosition = pos
          isPlaying = true
          currentTrack = trackList?.get(pos)
          if (mediaPlayer == null) trackList?.get(pos)?.let { setupTrack(it) }
          trackList?.size?.let {
              trackList?.get(pos)?.let { it1 ->
                  createNotification(this, it1
                          , R.drawable.ic_pause, pos, it)
              }
          }
          currentTrack?.isPlaying= true
          mediaPlayer?.start()
      }
    }

    override fun onTrackPause() {
        if(trackList?.isNotEmpty() == true) {
            isPlaying = false
            currentTrack = trackList?.get(currentPosition)
            trackList?.get(currentPosition)?.let {
                trackList?.size?.let { it1 ->
                    createNotification(this, it
                            , R.drawable.ic_play, currentPosition, it1)
                }
            }
            currentTrack?.isPlaying= false
            mediaPlayer!!.pause()
        }
    }

    override fun onTrackNext() {
        if (trackList?.isNotEmpty()!!) {
            if (isShuffle) {
                currentPosition = Random().nextInt(trackList!!.size)
            } else {
                currentPosition++
            }
            if (currentPosition == trackList!!.size) currentPosition = 0
            currentTrack = trackList!![currentPosition]
            setupTrack(trackList!![currentPosition])
            createNotification(this, trackList!![currentPosition]
                    , R.drawable.ic_pause, currentPosition, trackList!!.size)
            mediaPlayer!!.start()
        }
    }

    override fun onCompletion(mp: MediaPlayer) {
        when (repeat) {
            0 -> if (currentPosition == (trackList?.size ?: 1) - 1) {
                onTrackPause()
                context!!.sendBroadcast(getIntent(Constants.ACTION_PLAY_PAUSE))
            } else {
                onTrackNext()
                context!!.sendBroadcast(getIntent(Constants.ACTION_NEXT))
            }
            1 -> {
                onTrackPlay(currentPosition)
                context!!.sendBroadcast(getIntent(Constants.ACTION_PLAY_PAUSE))
            }
            2 -> {
                onTrackNext()
                context!!.sendBroadcast(getIntent(Constants.ACTION_NEXT))
            }
            else -> {
            }
        }
    }

    override fun onTrackRelease() {
        if (mediaPlayer != null) {
            mediaPlayer!!.stop()
            mediaPlayer!!.release()
            mediaPlayer = null
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun createChanel() {
        val notificationChannel = NotificationChannel(Constants.CHANNEL_ID
                , "Cod Dev", NotificationManager.IMPORTANCE_LOW)
        notificationManager = getSystemService(NotificationManager::class.java)
        if (notificationManager != null) {
            notificationManager!!.createNotificationChannel(notificationChannel)
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private fun createNotification(context: Context, track: Track, playBtn_raw_id: Int, pos: Int, size: Int) {
        //if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
        //  NotificationManagerCompat notificationManagerCompat=NotificationManagerCompat.from(context);
        val mediaSession = MediaSessionCompat(context, "tag")
        this.context = context
        val largeIcon =  getTrackCover(track)

        var notificationWhen = 0L
        var showWhen = false
        var usesChronometer = false
        var ongoing = false
        if (isPlaying) {
            notificationWhen = System.currentTimeMillis() - (mediaPlayer?.currentPosition ?: 0)
            showWhen = true
            usesChronometer = true
            ongoing = true
        }
          Log.i(TAG, "create notification")
        //createNotification
        val notification = NotificationCompat.Builder(context, Constants.CHANNEL_ID)
        notification
                .setContentTitle(track.title)
                .setContentText(track.artistName)
                .setSmallIcon(R.drawable.ic_music_note)
                .setLargeIcon(largeIcon)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setWhen(notificationWhen)
                .setShowWhen(showWhen)
                .setUsesChronometer(usesChronometer)
                .setContentIntent(contentIntent)
                .setOngoing(ongoing)
                .setChannelId(Constants.CHANNEL_ID)
                .setStyle(androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(0, 1, 2)
                        .setMediaSession(mediaSession.sessionToken))
                .addAction(R.drawable.ic_skip_previous, "previous", getPendingIntent(Constants.ACTION_PREVIOUS))
                .addAction(playBtn_raw_id, "play", getPendingIntent(Constants.ACTION_PLAY_PAUSE))
                .addAction(R.drawable.ic_skip_next, "next", getPendingIntent(Constants.ACTION_NEXT))
                .addAction(R.drawable.ic_close,"close",getPendingIntent(Constants.ACTION_STOP))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notification.setCategory(Notification.CATEGORY_SERVICE)
        }
        startForeground(Constants.NOTIFICATION_ID, notification.build())

        // delay foreground state updating a bit, so the notification can be swiped away properly after initial display
//        Handler(Looper.getMainLooper()).postDelayed({
//            if (!isPlaying) {
//                stopForeground(false)
//            }
//        }, 200L)
    }

    private fun getPendingIntent(action: String): PendingIntent {
        val intent = Intent(this, TrackServiceReceiver::class.java)
        intent.action = action
        return PendingIntent.getBroadcast(context, 0, intent, 0)
    }

    private val contentIntent: PendingIntent
        get() {
            val contentIntent = Intent(this, MainActivity::class.java).apply {
                flags=Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            return PendingIntent.getActivity(context, 0, contentIntent, 0)
        }

    private fun getIntent(action: String): Intent {
        return Intent("onTrackCompletion")
                .putExtra(Constants.ACTION_NAME, action)
    }

    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        mediaPlayer!!.reset()
        return false
    }

    override fun onAudioFocusChange(focusChange: Int) {
        Log.d(TAG,focusChange.toString())
        when (focusChange) {
            AUDIOFOCUS_GAIN -> audioFocusGained()
            AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> duckAudio()
            AUDIOFOCUS_LOSS, AUDIOFOCUS_LOSS_TRANSIENT -> audioFocusLost()
        }
        mPrevAudioFocusState = focusChange
    }

    private fun audioFocusGained(){
        if (mWasPlayingAtFocusLost) {
            if (mPrevAudioFocusState == AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                unDuckAudio()
            } else {
                onTrackPlay(currentPosition) //error
            }
        }
    }

    private fun duckAudio(){
        mediaPlayer?.setVolume(0.3f, 0.3f)
        mWasPlayingAtFocusLost = isPlaying
    }

    private fun audioFocusLost(){
        if (isPlaying) {
            mWasPlayingAtFocusLost = true
            onTrackPause()
        } else {
            mWasPlayingAtFocusLost = false
        }
    }
    private fun unDuckAudio(){
        mediaPlayer?.setVolume(1f, 1f)
    }

    override fun onPrepared(mp: MediaPlayer?) {
        Log.i(TAG, "onPrepared")
    }
}