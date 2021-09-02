package com.tokastudio.music_offline.view.fragment

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.google.android.gms.ads.formats.UnifiedNativeAdView
import com.squareup.picasso.Picasso
import com.tokastudio.music_offline.BuildConfig
import com.tokastudio.music_offline.Constants
import com.tokastudio.music_offline.R
import com.tokastudio.music_offline.SharedPref.getPrefFav
import com.tokastudio.music_offline.SharedPref.setPrefFav
import com.tokastudio.music_offline.TrackControllerB
import com.tokastudio.music_offline.databinding.FragmentTrackPlayingBinding
import com.tokastudio.music_offline.model.CurrentPlayingSong
import com.tokastudio.music_offline.model.Song
import com.tokastudio.music_offline.service.TrackService
import com.tokastudio.music_offline.view.activity.SharedViewModel
import java.util.concurrent.TimeUnit

/**
 * A simple [Fragment] subclass.
 * Use the [TrackPlayingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TrackPlayingFragment : Fragment(), TrackControllerB {
    private lateinit var binding: FragmentTrackPlayingBinding
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val args: TrackPlayingFragmentArgs by navArgs()
    private var trackService: TrackService? = null
    private var currentPosition = 0
    private var currentTrack: Song?=null
    private lateinit var favListId: MutableList<Long>
    private var favorite = false
    private var showLyric = false
    private var nativeAd: UnifiedNativeAd?=null

    private var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.extras!!.getString(Constants.ACTION_NAME)) {
                Constants.ACTION_PREVIOUS -> onTrackUpdateUi(trackService?.currentTrack)
                Constants.ACTION_PLAY_PAUSE -> trackService?.isPlaying?.let { onChangePlayPauseButton(it) }
                Constants.ACTION_NEXT -> onTrackUpdateUi(trackService?.currentTrack)
                Constants.ACTION_STOP ->{
                    activity?.finish()}
            }
        }
    }
    private var broadcastReceiverOnComplete: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.extras!!.getString(Constants.ACTION_NAME)) {
                Constants.ACTION_PREVIOUS -> onTrackUpdateUi(trackService?.currentTrack)
                Constants.ACTION_PLAY_PAUSE -> trackService?.isPlaying?.let { onChangePlayPauseButton(it) }
                Constants.ACTION_NEXT -> onTrackUpdateUi(trackService?.currentTrack)
            }
        }
    }
    private val updateMusicTime: Runnable = object : Runnable {
        @SuppressLint("DefaultLocale")
        override fun run() {
            try {
                val getCurrent = trackService?.mediaPlayer?.currentPosition
                binding.startTime.text = String.format("%02d:%02d",
                        getCurrent?.toLong()?.let { TimeUnit.MILLISECONDS.toMinutes(it) },
                        (getCurrent?.toLong()?.let { TimeUnit.MILLISECONDS.toSeconds(it) }?.minus(TimeUnit.MILLISECONDS.toSeconds(TimeUnit.MILLISECONDS.toMinutes(getCurrent.toLong()))))?.rem(60))
                if (getCurrent != null) {
                    binding.seekBar.progress = getCurrent
                }
                Handler().postDelayed(this, 1000) // So that it updates every Second
            } catch (e: Exception) {
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "onCreate")
        currentPosition = args.position
        currentTrack= args.song
        requireActivity().registerReceiver(broadcastReceiver, IntentFilter(Constants.ACTION_SERVICE))
        requireActivity().registerReceiver(broadcastReceiverOnComplete, IntentFilter("onTrackCompletion"))
        favListId = getPrefFav(requireActivity())
        Log.i("favlist",favListId.size.toString())

        sharedViewModel.setCurrentSong(CurrentPlayingSong(args.position,args.song,true))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        Log.i(TAG, "onCreateView")
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_track_playing,
                container, false)
        binding.clickHandler = ClickHandler()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedViewModel.trackService.observe(viewLifecycleOwner,{
            trackService= it
            start()
        })
    }

    private fun start(){
        if (trackService?.isPlaying == true) {
            if (trackService?.currentPosition == currentPosition && trackService?.currentTrack?.id == currentTrack?.id) {
                onTrackUpdateUi(trackService?.currentTrack)
                onChangePlayPauseButton(true)
            } else {
                trackService?.onTrackRelease()
                onTrackPlay(currentPosition)
                onTrackUpdateUi(trackService?.currentTrack)
            }
        } else {
            if (trackService?.currentPosition != currentPosition || trackService?.currentTrack?.id != currentTrack?.id){
                trackService?.onTrackRelease()
            }
            onTrackPlay(currentPosition)
            onTrackUpdateUi(trackService?.currentTrack)
        }
        view?.let { loadNativeAd(it) }
        seekBarHandler()
    }

    override fun onStop() {
        super.onStop()
        sharedViewModel.setCurrentSong(CurrentPlayingSong(args.position,args.song,false))
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "onDestroy")
        nativeAd?.destroy()
        requireActivity().unregisterReceiver(broadcastReceiver)
        requireActivity().unregisterReceiver(broadcastReceiverOnComplete)
    }

    inner class ClickHandler {
        fun backBtn(view: View?) {
            Log.i(TAG, "back")
            assert(fragmentManager != null)
            fragmentManager!!.popBackStack()
        }

        fun favBtn(view: View?) {
            Log.i(TAG, "fav")
            if (favorite) {
                favorite = false
                favListId.remove(trackService?.currentTrack?.id)
            } else {
                trackService?.currentTrack?.id?.let { favListId.add(it) }
                favorite = true
            }
             setPrefFav(requireActivity(),favListId)
             onChangeFavButton(favorite)
        }

        fun shareBtn(view: View?) {
            Log.i(TAG, "share")
            shareApp(trackService?.currentTrack?.data)
        }

        fun showLyricBtn(view: View?) {
            if (showLyric) {
                showLyric = false
                binding.trackCover.visibility = View.VISIBLE
                binding.lyricLayout.visibility = View.INVISIBLE
                binding.showHideLyric.text = resources.getString(R.string.show_lyric)
            } else {
                showLyric = true
                binding.trackCover.visibility = View.INVISIBLE
                binding.lyricLayout.visibility = View.VISIBLE
                binding.showHideLyric.text = resources.getString(R.string.show_poster)
            }
        }

        fun shuffleBtn(view: View?) {
            if (trackService?.isShuffle == true) {
                trackService?.isShuffle = false
                binding.shuffleButton.setImageResource(R.drawable.ic_shuffle_off)
            } else {
                trackService?.isShuffle = true
                binding.shuffleButton.setImageResource(R.drawable.ic_shuffle_on)
            }
        }

        fun previousBtn(view: View?) {
            onTrackPrevious()
        }

        fun playPauseBtn(view: View?) {
            if (trackService?.isPlaying == true) {
                onTrackPause()
            } else {
                onTrackPlay(currentPosition)
            }
        }

        fun nextBtn(view: View?) {
            onTrackNext()
        }

        fun repeatBtn(view: View?) {
            when (trackService?.repeat) {
                0 -> {
                    trackService?.repeat = 1
                    binding.repeatButton.setImageResource(R.drawable.ic_repeat_one)
                }
                1 -> {
                    trackService?.repeat = 2
                    binding.repeatButton.setImageResource(R.drawable.ic_repeat_on)
                }
                2 -> {
                    trackService?.repeat = 0
                    binding.repeatButton.setImageResource(R.drawable.ic_repeat_off)
                }
            }
        }
    }

    private fun seekBarHandler() {
        val seekBarController = SeekBarController()
        binding.seekBar.setOnSeekBarChangeListener(seekBarController)
    }

    override fun onTrackPrevious() {
        trackService?.onTrackPrevious()
        onTrackUpdateUi(trackService?.currentTrack)
    }

    override fun onTrackPlay(pos: Int) {
        if (trackService != null && trackService?.getTrackList()?.isNotEmpty() == true) {
            Log.i(TAG, trackService?.getTrackList()?.size.toString())
            trackService!!.onTrackPlay(pos)
            onChangePlayPauseButton(true)
        }
    }

    override fun onTrackPause() {
        trackService?.onTrackPause()
        onChangePlayPauseButton(false)
    }

    override fun onTrackNext() {
        trackService?.onTrackNext()
        onTrackUpdateUi(trackService?.currentTrack)
    }

    override fun onTrackRelease() {}
    override fun onChangePlayPauseButton(isPlaying: Boolean) {
        if (isPlaying) {
            binding.playPauseButton.setImageResource(R.drawable.ic_pause)
        } else {
            binding.playPauseButton.setImageResource(R.drawable.ic_play)
        }
    }

    fun onChangeFavButton(fav: Boolean) {
        if (fav) {
            binding.trackFavorite.setImageResource(R.drawable.ic_favorite_on)
        } else {
            binding.trackFavorite.setImageResource(R.drawable.ic_favorite)
        }
    }

    @SuppressLint("DefaultLocale")
    override fun onTrackUpdateUi(track: Song?) {
        if (track != null) {
            binding.track = track
            val finalTime = trackService?.mediaPlayer!!.duration.toLong()
            val startTime = trackService?.mediaPlayer!!.currentPosition.toLong()
            binding.seekBar.max = finalTime.toInt()
            binding.startTime.text = String.format("%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(startTime),
                    TimeUnit.MILLISECONDS.toSeconds(startTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(startTime)) % 60)
            binding.endTime.text = String.format("%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(finalTime),
                    TimeUnit.MILLISECONDS.toSeconds(finalTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(finalTime)))
            binding.seekBar.progress = startTime.toInt()
            if (trackService?.isShuffle == true) {
                binding.shuffleButton.setImageResource(R.drawable.ic_shuffle_on)
            } else {
                binding.shuffleButton.setImageResource(R.drawable.ic_shuffle_off)
            }
            when (trackService?.repeat) {
                0 -> binding.repeatButton.setImageResource(R.drawable.ic_repeat_off)
                1 -> binding.repeatButton.setImageResource(R.drawable.ic_repeat_one)
                2 -> binding.repeatButton.setImageResource(R.drawable.ic_repeat_on)
            }

            val trackCover = BitmapDrawable(resources, trackService?.currentTrackCover )
            val uri: Uri? = null
            Picasso.get()
                    .load(uri)
                    .fit()
                    .placeholder(trackCover)
                    .into(binding.trackCover)

            favorite = favListId.contains(trackService?.currentTrack?.id)
            onChangeFavButton(favorite)
            Handler().postDelayed(updateMusicTime, 1000)
        }
    }

    inner class SeekBarController : OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {}
        override fun onStartTrackingTouch(seekBar: SeekBar) {
            if (trackService?.mediaPlayer == null) return
        }

        override fun onStopTrackingTouch(seekBar: SeekBar) {
            if (trackService?.mediaPlayer != null) {
                trackService?.mediaPlayer?.seekTo(seekBar.progress)
            }
        }
    }

    private fun shareApp(assetFileName: String?) {
        val appId =BuildConfig.APPLICATION_ID
        val provider =Uri.parse("content://$appId")
        val uriFile =provider.buildUpon().appendPath(assetFileName).build()

        try {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "*/*"
            val googlePlayLink="https://play.google.com/store/apps/details?id=$appId"
            val shareMessage=resources.getString(R.string.share_message)+"\n"+googlePlayLink
            shareIntent.putExtra(Intent.EXTRA_STREAM,uriFile)
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(Intent.createChooser(shareIntent, resources.getString(R.string.share_with)))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        private val TAG = TrackPlayingFragment::class.java.simpleName
        @JvmStatic
        fun newInstance()= TrackPlayingFragment()
    }

    private fun loadNativeAd(view: View){
        val adLoader = AdLoader.Builder(context, resources.getString(R.string.native_ad_id_trackPlaying))
                .forUnifiedNativeAd { unifiedNativeAd ->
                    if(!isVisible){
                        unifiedNativeAd.destroy()
                        nativeAd?.destroy()
                        return@forUnifiedNativeAd
                    }
                    nativeAd = unifiedNativeAd
                    val frameLayout: FrameLayout = view.findViewById(R.id.frameLayout_ad_trackPlaying)
                    val adView = layoutInflater.inflate(R.layout.native_adview_trackplaying, null) as UnifiedNativeAdView
                    populateUnifiedNativeAdView(unifiedNativeAd, adView)
                    frameLayout.removeAllViews()
                    frameLayout.addView(adView)
                }
                .withAdListener(object : AdListener() {
                    override fun onAdFailedToLoad(errorCode: Int) {
                        Log.d(TAG, "Failed to load native ad: $errorCode")
                    }
                })
                .build()

        adLoader.loadAd(AdRequest.Builder().build())
    }

    private fun populateUnifiedNativeAdView(ad: UnifiedNativeAd ,adView: UnifiedNativeAdView){
        adView.iconView = adView.findViewById(R.id.ad_app_icon)
        adView.headlineView = adView.findViewById(R.id.ad_headLine)
        adView.callToActionView=adView.findViewById(R.id.ad_call_to_action)
        adView.bodyView = adView.findViewById(R.id.ad_body)

        // The headline and mediaContent are guaranteed to be in every UnifiedNativeAd..
        (adView.headlineView as TextView).text = ad.headline

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        if (ad.icon == null) {
            adView.iconView.visibility = View.GONE
        } else {
            (adView.iconView as ImageView).setImageDrawable(ad.icon.drawable)
            adView.iconView.visibility = View.VISIBLE
        }

        if (ad.callToAction == null) {
            adView.callToActionView.visibility = View.GONE
        } else {
            adView.callToActionView.visibility = View.VISIBLE
            (adView.callToActionView as Button).text = nativeAd!!.callToAction
        }

        if (ad.body == null) {
            adView.bodyView.visibility = View.GONE
        } else {
            adView.bodyView.visibility = View.VISIBLE
            (adView.bodyView as TextView).text = ad.body
        }
        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad.
        adView.setNativeAd(ad)
    }
}