package com.tokastudio.music_offline.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.tokastudio.music_offline.Constants

class TrackServiceReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        context.sendBroadcast(Intent(Constants.ACTION_SERVICE)
                .putExtra(Constants.ACTION_NAME, intent.action))
    }
}