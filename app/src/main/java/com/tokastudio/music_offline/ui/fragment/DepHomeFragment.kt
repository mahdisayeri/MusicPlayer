package com.tokastudio.music_offline.ui.fragment


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.google.android.gms.ads.formats.UnifiedNativeAdView
import com.tokastudio.music_offline.Constants
import com.tokastudio.music_offline.R
import com.tokastudio.music_offline.SharedPref
import com.tokastudio.music_offline.databinding.HomeFragmentBinding
import com.tokastudio.music_offline.dialog.RequestTrackDialog

class DepHomeFragment : Fragment(){
    private lateinit var binding: HomeFragmentBinding
    private var nativeAd: UnifiedNativeAd?=null
    private lateinit var mInterstitialAd: InterstitialAd
    companion object {
        private val TAG = DepHomeFragment::class.simpleName
        fun newInstance() = DepHomeFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding=DataBindingUtil.inflate(inflater,R.layout.home_fragment,container,false)
        binding.clickHandler=ClickHandler()
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        loadNativeAd()

        mInterstitialAd = InterstitialAd(context)
        mInterstitialAd.adUnitId = resources.getString(R.string.interstitial_ad_id_homeToTrackList)

        //start interstitial ad if fav count > 1
         if(SharedPref.getPrefFav(requireActivity()).size>1) {
             mInterstitialAd.loadAd(AdRequest.Builder().build())
         }
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        nativeAd?.destroy()

    }

    inner class ClickHandler {
        fun allTracks(view: View?) {
           startTrackList(Constants.ALL_LIST)
        }

        fun offlineMusics(view: View?) {
            startOfflineFragment()
        }

        fun favorites(view: View?) {
            if(SharedPref.getPrefFav(requireActivity()).size>1) {
                if (mInterstitialAd.isLoaded) {
                    mInterstitialAd.show()
                }
            }
            startTrackList(Constants.FAVORITE_LIST)
        }

        fun otherApps(view: View?) {
            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://developer?id=Toka+Studio")))
            } catch (e: Exception) {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/developer?id=Toka+Studio")))
            }
        }

        fun requestTrack(view: View?) {
           activity?.supportFragmentManager?.let { RequestTrackDialog().show(it,"RequestTrackDialog") }
        }
    }

    private fun startTrackList(listName: String){
     //   findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToTrackListFragment(listName))
    }

    private fun startOfflineFragment(){
     //   findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToOfflineFragment())
    }

    private fun loadNativeAd(){
        val adLoader = AdLoader.Builder(context, resources.getString(R.string.native_ad_id_home))
                .forUnifiedNativeAd { unifiedNativeAd ->
                    if(!isVisible){
                        unifiedNativeAd.destroy()
                        nativeAd?.destroy()
                        return@forUnifiedNativeAd
                    }
                    nativeAd = unifiedNativeAd
                    val frameLayout: FrameLayout = binding.frameLayoutAdHome
                    val adView = layoutInflater.inflate(R.layout.native_adview_home, null) as UnifiedNativeAdView
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
