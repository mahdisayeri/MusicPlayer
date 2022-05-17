package com.tokastudio.music_offline.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.tokastudio.music_offline.R
import com.tokastudio.music_offline.interfaces.ListItemClickListener
import com.tokastudio.music_offline.databinding.ListItemArtistBinding
import com.tokastudio.music_offline.model.Track

class ArtistAdapter(private val context: Context, private val listItemClickListener: ListItemClickListener) : RecyclerView.Adapter<ArtistAdapter.MyViewHolder>() {

    private var tracks: List<List<Track>>? = null
    // private var playingPos: Int?= null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(ListItemArtistBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        tracks?.get(position)?.let { holder.bind(it, listItemClickListener) }
    }

    override fun getItemCount(): Int {
        return tracks?.size ?: 0
    }

    fun setSongs(tracks: List<List<Track>>) {
        this.tracks = tracks
        notifyDataSetChanged()
    }

    inner class MyViewHolder(private val binding: ListItemArtistBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(items: List<Track>, listItemClickListener: ListItemClickListener) {
            binding.cardView.animation= AnimationUtils.loadAnimation(context, R.anim.fade_animation)
            binding.listItem = items[0]
            binding.tracksCount = if(items.size <= 1) "${items.size} track" else "${items.size} tracks"

            binding.setClickListener {
                listItemClickListener.onListItemClick(adapterPosition, items)
            }
        }
    }
}