package com.tokastudio.music_offline.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.tokastudio.music_offline.R
import com.tokastudio.music_offline.databinding.TrackListItemSimpleBinding
import com.tokastudio.music_offline.model.Track
import com.tokastudio.music_offline.adapter.TrackAdapter.MusicViewHolder
import com.tokastudio.music_offline.model.Song
import kotlin.collections.ArrayList

class TrackAdapter internal constructor(context: Context?,private val itemClickListener: ItemClickListener) : RecyclerView.Adapter<MusicViewHolder>() {
    private var trackList= arrayListOf<Song>()
    private val inflater: LayoutInflater= LayoutInflater.from(context)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicViewHolder {
        val binding: TrackListItemSimpleBinding = DataBindingUtil.inflate(inflater, R.layout.track_list_item_simple, parent, false)
        return MusicViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MusicViewHolder, position: Int) {
        val item = trackList[position]
        holder.itemSimpleBinding.track = item
        holder.itemView.setOnClickListener(View.OnClickListener {
            itemClickListener.onItemListClick(position)
        })
    }

    override fun getItemCount(): Int {
        return trackList.size
    }

   internal fun setTrackList(tracks: ArrayList<Song>){
        this.trackList=tracks
        notifyDataSetChanged()
    }

    inner class MusicViewHolder(val itemSimpleBinding: TrackListItemSimpleBinding) : RecyclerView.ViewHolder(itemSimpleBinding.root)

    interface ItemClickListener{
        fun onItemListClick(position: Int)
    }

}