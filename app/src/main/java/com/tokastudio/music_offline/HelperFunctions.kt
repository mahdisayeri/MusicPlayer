package com.tokastudio.music_offline

import android.content.Context
import android.content.Intent

fun shareApp(context: Context){
    val appId = BuildConfig.APPLICATION_ID
    // val provider = Uri.parse("content://$appId")
    //  val uriFile = provider.buildUpon().appendPath(assetFileName).build()
    try {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "*/*"
        val googlePlayLink = "https://play.google.com/store/apps/details?id=$appId"
        val shareMessage = context.resources.getString(R.string.share_message) + "\n" + googlePlayLink
        //   shareIntent.putExtra(Intent.EXTRA_STREAM, uriFile)
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        context.startActivity(Intent.createChooser(shareIntent, context.resources.getString(R.string.share_with)))
    } catch (e: Exception) {
        e.printStackTrace()
    }
}