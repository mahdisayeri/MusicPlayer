package com.tokastudio.music_offline.adapter

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.media.MediaMetadataRetriever
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.tokastudio.music_offline.R
import java.text.DecimalFormat
import java.text.NumberFormat

@SuppressLint("SetTextI18n")
@BindingAdapter("bind:duration")
fun convertDuration(view: TextView,duration: Long){
    if (duration > 0){
        val numberFormat: NumberFormat= DecimalFormat("00")
        val min= duration/60000
        val sec= (duration-(min*60000))/1000
        val minFormat= numberFormat.format(min)
        val secFormat= numberFormat.format(sec)
        view.text = "$minFormat:$secFormat"
    }
}

@BindingAdapter("bind:loadCircleSongCover")
fun loadCircleSongCover(view: ImageView,songCover: Bitmap?){
    var trackCover: BitmapDrawable?= null
    if (songCover!= null){
        trackCover = BitmapDrawable(view.context.resources, songCover)
    }
        //val uri: Uri? = null
        Glide.with(view.context)
                .load(trackCover)
                .fitCenter()
                .apply(RequestOptions.circleCropTransform())
                .placeholder(R.drawable.music_placeholder2)
                .into(view)

}

@BindingAdapter("bind:loadSquareSongCover")
fun loadSquareSongCover(view: ImageView,songCover: Bitmap?){
    val trackCover: BitmapDrawable
    if (songCover!= null){
        trackCover = BitmapDrawable(view.context.resources, songCover)
        //val uri: Uri? = null
        Glide.with(view.context)
                .load(trackCover)
                .fitCenter()
                .placeholder(R.drawable.music_placeholder2)
                .into(view)
    }else{
        view.setImageResource(R.drawable.music_placeholder2)
    }
}