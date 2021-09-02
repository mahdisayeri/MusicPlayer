package com.tokastudio.music_offline

import android.app.Application
import android.util.Log
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener

class MyApplication : Application() {
    private val tag =MyApplication::class.simpleName
    override fun onCreate() {
        super.onCreate()
        MobileAds.initialize(this){
            Log.d(tag,"adMob initialized")
        }
    }
}