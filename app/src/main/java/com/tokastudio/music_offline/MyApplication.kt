package com.tokastudio.music_offline

import android.app.Application
import android.util.Log
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener
import com.tokastudio.music_offline.ui.fragment.ArtistViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

class MyApplication : Application() {
    private val tag =MyApplication::class.simpleName
    override fun onCreate() {
        super.onCreate()
        MobileAds.initialize(this){
            Log.d(tag,"adMob initialized")
        }
        val modules= module {
            viewModel { ArtistViewModel() }
        }

        startKoin {
            androidContext(this@MyApplication)
            modules(modules)
        }
    }
}