package com.tokastudio.music_offline.ui.activity

import android.app.ActivityManager
import android.content.*
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.navigation.Navigation.findNavController
import com.tokastudio.music_offline.BuildConfig
import com.tokastudio.music_offline.R
import com.tokastudio.music_offline.Constants
import com.tokastudio.music_offline.SharedPref
import com.tokastudio.music_offline.databinding.ActivityMainBinding
import com.tokastudio.music_offline.dialog.ExitDialog
import com.tokastudio.music_offline.dialog.RequestTrackDialog
import com.tokastudio.music_offline.model.CurrentPlayingSong
import com.tokastudio.music_offline.model.Song
import com.tokastudio.music_offline.service.TrackService

class MainActivity : AppCompatActivity() ,ExitDialog.ExitDialogListener,RequestTrackDialog.RequestTrackListener{

    private val viewModel: SharedViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding
    private var currentPlayingSong: CurrentPlayingSong?= null
    private var boundService = false
    private var isTrackServiceRunning = false

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
        binding= DataBindingUtil.setContentView(this,R.layout.activity_main)
        binding.clickHandler= ClickHandler()
//       setContentView(R.layout.activity_main)
    //   fireBaseInstanceId()

        isTrackServiceRunning=isMyServiceRunning(TrackService::class.java)
       if(SharedPref.getIntPref(this,Constants.VERSION_CODE)<BuildConfig.VERSION_CODE) {
           this.deleteDatabase("myDataBase.db")
       }
        SharedPref.setPref(this,Constants.VERSION_CODE,BuildConfig.VERSION_CODE)


        viewModel.currentPlayingSong.observe(this, {
            currentPlayingSong = it
            binding.track = it.song
            if (it.hideCurrentPlayingLayout){
                binding.currentPlayingLayout.visibility= View.GONE
            } else{
                binding.currentPlayingLayout.visibility= View.VISIBLE
            }
        })

        viewModel.trackService.observe(this,{
            if (it.isPlaying){
                it.currentTrack?.let { it1 -> CurrentPlayingSong(it.currentPosition, it1,false) }?.let { it2 -> viewModel.setCurrentSong(it2) }
            }else{
                val song= Song(1232,null,232324,12132,123,null, 234324,12312,null,35443,null,null,false,false)
             viewModel.setCurrentSong(CurrentPlayingSong(1,song,true))
            }
        })

    }

    override fun onStart() {
        super.onStart()
        // Bind to LocalService
        val trackServiceIntent = Intent(this, TrackService::class.java)
        if (isTrackServiceRunning) {
            bindService(trackServiceIntent, connection, Context.BIND_AUTO_CREATE)
            Log.i(TAG, "bind Service")
        } else {
            Log.i(TAG, "start Service")
            // SharedPref.setPref(activity, Utils.CURRENT_PLAYING_LIST, choseList!!)
           // trackServiceIntent.putParcelableArrayListExtra(Utils.TRACK_LIST, trackList)
            startService(trackServiceIntent) //Starting the service
            bindService(trackServiceIntent, connection, Context.BIND_AUTO_CREATE)
        }
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
        Log.i(TAG, "onStop List")
        unbindService(connection)
        boundService = false
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

    inner class ClickHandler(){
        fun clickOnCurrentPlayingSong(view: View){
            if (currentPlayingSong != null) {
            val bundle = bundleOf("position" to currentPlayingSong?.position,"song" to currentPlayingSong?.song)
                findNavController(this@MainActivity,R.id.nav_host_fragment).navigate(R.id.action_global_trackPlayingFragment,bundle)
            }
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
       if(artistName.isNotEmpty()) {
           sendEmail(artistName, trackName)
           dialog.dismiss()
       }else{
           Toast.makeText(this,getString(R.string.please_enter_artistName),Toast.LENGTH_LONG).show()
       }
    }
    private fun sendEmail(artistName: String,trackName: String) {
        Log.i("Send email", "")
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
}