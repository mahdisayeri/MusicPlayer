package com.tokastudio.music_offline.ui

import android.app.ActivityManager
import android.content.*
import android.database.Cursor
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.preference.PreferenceManager
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.tokastudio.music_offline.*
import com.tokastudio.music_offline.databinding.ActivityMainBinding
import com.tokastudio.music_offline.dialog.ExitDialog
import com.tokastudio.music_offline.dialog.RequestTrackDialog
import com.tokastudio.music_offline.interfaces.TrackControllerB
import com.tokastudio.music_offline.model.CurrentPlayingSong
import com.tokastudio.music_offline.model.Track
import com.tokastudio.music_offline.service.TrackService
import com.tokastudio.music_offline.ui.fragment.ArtistFragmentDirections
import com.tokastudio.music_offline.ui.fragment.MainFragmentDirections

class MainActivity : AppCompatActivity(), TrackControllerB,PermissionListener,SharedPreferences.OnSharedPreferenceChangeListener,
        ExitDialog.ExitDialogListener, RequestTrackDialog.RequestTrackListener {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding
    private var currentPlayingSong: CurrentPlayingSong? = null
    private var boundService = false
    private var isTrackServiceRunning = false
    private var trackService: TrackService? = null

    private lateinit var navController: NavController
    private var permissionIsGranted = false

    /** Defines callbacks for service binding, passed to bindService()  */
    private val connection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName,
                                        service: IBinder) {
            Log.i(TAG, "Service Connected")
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            val binder = service as TrackService.LocalBinder
            viewModel.setTrackService(binder.service)
            boundService = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            Log.i(TAG, "Service DisConnected")
            boundService = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        navController= findNavController(R.id.nav_host_fragment)
        //   fireBaseInstanceId()

        isTrackServiceRunning = isMyServiceRunning(TrackService::class.java)
        if (SharedPref.getIntPref(this, Constants.VERSION_CODE) < BuildConfig.VERSION_CODE) {
            this.deleteDatabase("myDataBase.db")
        }
        SharedPref.setPref(this, Constants.VERSION_CODE, BuildConfig.VERSION_CODE)

       // setDestinationChangeListener()

        viewModel.currentPlayingSong.observe(this) {
            var isNewTrack = false
            if (trackService?.currentTrack?.id != it.track.id) {
                isNewTrack = true
            }
            currentPlayingSong = it
            onTrackUpdateUi(it.track)
            if (it.hideCurrentPlayingLayout) {
                binding.currentPlayingLayout.visibility = View.GONE
            } else {
                binding.currentPlayingLayout.visibility = View.VISIBLE
                if (isNewTrack) {
                    onTrackRelease()
                    onTrackPlay(it.position)
                }
            }
        }

        viewModel.trackService.observe(this) {
            trackService = it
            if (it.isPlaying) {
                it.currentTrack?.let { it1 -> CurrentPlayingSong(it.currentPosition, it1, false) }
                    ?.let { it2 -> viewModel.setCurrentSong(it2) }
            } else if (currentPlayingSong != null) {
                currentPlayingSong?.hideCurrentPlayingLayout = true
                currentPlayingSong?.track?.isPlaying = false
                viewModel.setCurrentSong(currentPlayingSong!!)
            }
        }
        if (permissionIsGranted) {
            fetchMusicFiles()
        } else {
            checkPermission()
        }
        binding.clickHandler = ClickHandler()
    }

    override fun onStart() {
        super.onStart()
        // Bind to LocalService
        val trackServiceIntent = Intent(this, TrackService::class.java)
        if (isTrackServiceRunning) {
            bindService(trackServiceIntent, connection, Context.BIND_AUTO_CREATE)
            Log.i("LogService", "bind Service")
        } else {
            Log.i("LogService", "start Service")
            startService(trackServiceIntent) //Starting the service
            bindService(trackServiceIntent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onResume() {
        super.onResume()
        this.getPreferences(MODE_PRIVATE).registerOnSharedPreferenceChangeListener(this)
    }


    private fun isMyServiceRunning(trackService: Class<*>): Boolean {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (trackService.name == service.service.className) {
                return true
            }
        }
        return false
    }

    override fun onStop() {
        super.onStop()
        unbindService(connection)
        boundService = false
        this.getPreferences(MODE_PRIVATE).unregisterOnSharedPreferenceChangeListener(this)
    }

//    private fun fireBaseInstanceId() {
//        FirebaseMessaging.getInstance().subscribeToTopic("all")
//                .addOnCompleteListener { task ->
//                    var msg = getString(R.string.msg_subscribed)
//                    if (!task.isSuccessful) {
//                        msg = getString(R.string.msg_subscribe_failed)
//                    }
//                    Log.d(TAG, msg)
//                }
//    }

//    override fun onBackPressed() {
//    //  if(supportFragmentManager.backStackEntryCount==0) showExitDialog() else supportFragmentManager.popBackStack()
//    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        Log.d("logTestt","onSharedPreferenceChanged")
        if (key == Constants.PREF_FAV_KEY){
            val favListIds = SharedPref.getPrefFav(this)
            viewModel.setFavListId(favListIds)
            Log.d("logTestt",favListIds.toString())
        }
    }

    inner class ClickHandler {
        fun clickOnCurrentPlayingSong(view: View) {
            val id= navController.currentDestination?.id
            if (currentPlayingSong != null && id!= null) {
                // val bundle = bundleOf("position" to currentPlayingSong?.position, "track" to currentPlayingSong?.track)
                //findNavController(this@MainActivity, R.id.nav_host_fragment).navigate(R.id.action_global_trackPlayingFragment) // ,bundle
                when(id){
                    R.id.mainFragment -> navController.navigate(MainFragmentDirections.actionMainFragmentToTrackPlayingFragment())
                    R.id.artistFragment -> navController.navigate(ArtistFragmentDirections.actionArtistFragmentToTrackPlayingFragment())
                }
            }
        }

        fun clickOnPreviousBtn(view: View) {
            onTrackPrevious()
        }

        fun clickOnPlayPauseBtn(view: View) {
            if (trackService?.isPlaying == true) {
                onTrackPause()
            } else {
                trackService?.currentPosition?.let { onTrackPlay(it) }
            }
        }

        fun clickOnNextBtn(view: View) {
            onTrackNext()
        }
    }

    override fun onBackPressed() {
        val currentDestinationId= navController.currentDestination?.id
        if (currentDestinationId == R.id.mainFragment){
                  finish()
                  return
        }else{
            super.onBackPressed()
        }

    }

    private fun showExitDialog() {
        ExitDialog().show(supportFragmentManager, "ExitDialogFragment")
    }

    override fun onExitBtnClick(dialog: ExitDialog?) {
        dialog!!.dismiss()
        finish()
    }

    override fun onCancelBtnClick(dialog: ExitDialog?) {
        dialog!!.dismiss()
    }

    override fun onRatingBrnClick(dialog: ExitDialog?) {
         openAppRating(this)
        dialog!!.dismiss()
    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName
        fun openAppRating(context: Context) {
            // you can also use BuildConfig.APPLICATION_ID
            val appId = BuildConfig.APPLICATION_ID
            val rateIntent = Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=$appId"))
            var marketFound = false

            // find all applications able to handle our rateIntent
            val otherApps = context.packageManager
                    .queryIntentActivities(rateIntent, 0)
            for (otherApp in otherApps) {
                // look for Google Play application
                if (otherApp.activityInfo.applicationInfo.packageName
                        == "com.android.vending") {
                    val otherAppActivity = otherApp.activityInfo
                    val componentName = ComponentName(
                            otherAppActivity.applicationInfo.packageName,
                            otherAppActivity.name
                    )
                    // make sure it does NOT open in the stack of your activity
                    rateIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    // task reparenting if needed
                    rateIntent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
                    // if the Google Play was already open in a search result
                    //  this make sure it still go to the app page you requested
                    rateIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    // this make sure only the Google Play app is allowed to
                    // intercept the intent
                    rateIntent.component = componentName
                    context.startActivity(rateIntent)
                    marketFound = true
                    break
                }
            }

            // if GP not present on device, open web browser
            if (!marketFound) {
                val webIntent = Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=$appId"))
                context.startActivity(webIntent)
            }
        }
    }

    override fun onSendBtnClick(dialog: DialogFragment, artistName: String, trackName: String) {
        if (artistName.isNotEmpty()) {
            sendEmail(artistName, trackName)
            dialog.dismiss()
        } else {
            Toast.makeText(this, getString(R.string.please_enter_artistName), Toast.LENGTH_LONG).show()
        }
    }

    private fun sendEmail(artistName: String, trackName: String) {
        val to = Constants.EMAIL_ADDRESS
        val emailIntent = Intent(Intent.ACTION_SENDTO)
        emailIntent.data = Uri.parse("mailto:$to")
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.request_track))
        emailIntent.putExtra(Intent.EXTRA_TEXT, "$artistName - $trackName")
        try {
            startActivity(emailIntent)
        } catch (ex: ActivityNotFoundException) {
            Toast.makeText(this,
                    "There is no email client installed.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onTrackPrevious() {
        trackService?.currentTrack?.isPlaying = false
        trackService?.onTrackPrevious()
        trackService?.currentTrack?.isPlaying = true
        trackService?.currentPosition?.let { trackService?.currentTrack?.let { it1 -> CurrentPlayingSong(it, it1, false) } }?.let { viewModel.setCurrentSong(it) }
        onChangePlayPauseButton(true)
    }

    override fun onTrackPlay(pos: Int) {
        if (trackService != null && trackService?.getTrackList()?.isNotEmpty() == true) {
            trackService!!.onTrackPlay(pos)
            trackService?.currentPosition?.let { trackService?.currentTrack?.let { it1 -> CurrentPlayingSong(it, it1, false) } }?.let { viewModel.setCurrentSong(it) }
            // onChangePlayPauseButton(true)
        }
    }

    override fun onTrackPause() {
        trackService?.onTrackPause()
        trackService?.currentPosition?.let { trackService?.currentTrack?.let { it1 -> CurrentPlayingSong(it, it1, false) } }?.let { viewModel.setCurrentSong(it) }
        //  onChangePlayPauseButton(false)
    }

    override fun onTrackNext() {
        trackService?.currentTrack?.isPlaying = false
        trackService?.onTrackNext()
        trackService?.currentTrack?.isPlaying = true
        trackService?.currentPosition?.let { trackService?.currentTrack?.let { it1 -> CurrentPlayingSong(it, it1, false) } }?.let { viewModel.setCurrentSong(it) }
        onChangePlayPauseButton(true)
    }

    override fun onTrackRelease() {
        trackService?.onTrackRelease()
    }

    override fun onChangePlayPauseButton(isPlaying: Boolean) {
        if (isPlaying) {
            binding.playPauseBtn.setImageResource(R.drawable.ic_round_pause_24)
        } else {
            binding.playPauseBtn.setImageResource(R.drawable.ic_round_play_arrow_24)
        }
    }

    override fun onTrackUpdateUi(track: Track?) {
        binding.track = track
        trackService?.isPlaying?.let { onChangePlayPauseButton(it) }
    }

    private fun checkPermission() {
        TedPermission.with(this)
            .setPermissionListener(this)
            .setDeniedMessage(resources.getString(R.string.deniedMessagePermision))
            .setPermissions(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            .check()
    }

    override fun onPermissionGranted() {
        permissionIsGranted = true
        fetchMusicFiles()
    }

    override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
        permissionIsGranted = false
        Toast.makeText(this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
    }

    private fun fetchMusicFiles() {
        val list: MutableList<Track> = mutableListOf()
        val uri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0"
        val sortOrder = MediaStore.Audio.Media.TITLE + " ASC"

        val cursor: Cursor? =  this.contentResolver.query(
            uri,
            null,
            selection,
            null,
            sortOrder)
        if (cursor != null && cursor.moveToFirst()) {
            val id: Int = cursor.getColumnIndex(MediaStore.Audio.Media._ID)
            val title: Int = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val trackNumber: Int = cursor.getColumnIndex(MediaStore.Audio.Media.TRACK)
            val year: Int = cursor.getColumnIndex(MediaStore.Audio.Media.YEAR)
            val duration: Int = cursor.getColumnIndex("duration")
            val data: Int = cursor.getColumnIndex("_data")
            val dateModified: Int = cursor.getColumnIndex(MediaStore.Audio.Media.DATE_MODIFIED)
            val albumId: Int = cursor.getColumnIndex("album_id")
            val albumName: Int = cursor.getColumnIndex("album")
            val artistId: Int = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID)
            val artistName: Int = cursor.getColumnIndex("artist")

            // Now loop through the music files
            do {
                val audioId: Long = cursor.getLong(id)
                val audioTitle: String = cursor.getString(title)
                val audioTrackNumber: Int = cursor.getInt(trackNumber)
                val audioYear: Int = cursor.getInt(year)
                val audioDuration: Long = cursor.getLong(duration)
                val audioData: String = cursor.getString(data)
                val audioDateModified: Long = cursor.getLong(dateModified)
                val audioAlbumId: Long = cursor.getLong(albumId)
                val audioAlbumName: String = cursor.getString(albumName)
                val audioArtistId: Long = cursor.getLong(artistId)
                val audioArtistName: String = cursor.getString(artistName)

                // Add the current music to the list

                list.add(Track(audioId, audioTitle, audioTrackNumber, audioYear,
                    audioDuration, audioData, audioDateModified, audioAlbumId,
                    audioAlbumName, audioArtistId, audioArtistName, "", isPlaying = false, false, getCover(audioData)))
            } while (cursor.moveToNext())
        }
        cursor?.close()
        viewModel.setTracks(list)
    }

    private fun getCover(data: String?): ByteArray? {
        return if (data.isNullOrEmpty()){
            null
        } else{
            Log.d("loadCover= ","true")
            try{
                val retriever = MediaMetadataRetriever()
                retriever.setDataSource(data)
                retriever.embeddedPicture
            }catch (e: Exception){
                println("Exception of type : $e")
                null
            }
        }
    }
}