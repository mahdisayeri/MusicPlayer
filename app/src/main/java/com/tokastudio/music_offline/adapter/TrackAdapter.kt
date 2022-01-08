package com.tokastudio.music_offline.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tokastudio.music_offline.ListItemClickListener
import com.tokastudio.music_offline.databinding.ListItemTrackBinding
import com.tokastudio.music_offline.model.Track

class TrackAdapter(private val listItemClickListener: ListItemClickListener) : RecyclerView.Adapter<TrackAdapter.MyViewHolder>() {

    private var tracks: List<Track>? = null
    private var playingPos: Int? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(ListItemTrackBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        tracks?.get(position)?.let { holder.bind(it, listItemClickListener) }
    }

    override fun getItemCount(): Int {
        return tracks?.size ?: 0
    }

    fun setTracks(tracks: List<Track>) {
        this.tracks = tracks
        notifyDataSetChanged()
    }

    fun changePlayingTrack(pos: Int,isPlaying: Boolean){
        if (playingPos != null) {
            if (playingPos != pos) {
                tracks?.get(playingPos!!)?.isPlaying = false
                notifyItemChanged(playingPos!!)
                tracks?.get(pos)?.isPlaying = true
            }else{
                tracks?.get(pos)?.isPlaying= isPlaying
            }
            notifyItemChanged(pos)
        }
        else {
            playingPos = pos
            tracks?.get(pos)?.isPlaying = true
            notifyItemChanged(pos)
        }
    }

    inner class MyViewHolder(private val binding: ListItemTrackBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Track, listItemClickListener: ListItemClickListener) {
            binding.listItem = item
            if (item.isPlaying) playingPos = adapterPosition
            binding.setClickListener {
               // changePlayingTrack(adapterPosition)
                listItemClickListener.onListItemClick(adapterPosition, item)
            }
        }
    }
}