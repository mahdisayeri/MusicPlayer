package com.tokastudio.music_offline.adapter

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.tokastudio.music_offline.R
import java.text.DecimalFormat
import java.text.NumberFormat

@SuppressLint("SetTextI18n")
@BindingAdapter("bind:duration")
fun convertDuration(view: TextView, duration: Long){
    if (duration > 0){
        val numberFormat: NumberFormat= DecimalFormat("00")
        val min= duration/60000
        val sec= (duration-(min*60000))/1000
        val minFormat= numberFormat.format(min)
        val secFormat= numberFormat.format(sec)
        view.text = "$minFormat:$secFormat"
    }
}

@BindingAdapter("bind:loadTrackCover")
fun loadTrackCover(view: ImageView, songCover: ByteArray?){
    Glide.with(view.context)
            .load(songCover)
            .fitCenter()
            .placeholder(R.drawable.music_icon_placeholder)
            .into(view)

}