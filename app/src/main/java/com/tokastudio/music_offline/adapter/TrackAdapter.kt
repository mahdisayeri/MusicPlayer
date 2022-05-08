package com.tokastudio.music_offline.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tokastudio.music_offline.ListItemClickListener
import com.tokastudio.music_offline.databinding.ListItemTrackBinding
import com.tokastudio.music_offline.model.Track

class TrackAdapter(private val listItemClickListener: ListItemClickListener) : RecyclerView.Adapter<TrackAdapter.MyViewHolder>() {

    private var tracks= listOf<Track>()
    private var playingPos: Int? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(ListItemTrackBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        tracks[position].let { holder.bind(it) }
    }

    override fun getItemCount(): Int {
        return tracks.size ?: 0
    }


    fun setTracks(tracks: List<Track>) {
        this.tracks = tracks
        notifyDataSetChanged()
    }

    fun changePlayingTrack(pos: Int,isPlaying: Boolean){
        if (playingPos != null ) {
            if (playingPos != pos && playingPos!! < tracks.size) {
                tracks[playingPos!!].isPlaying = false
                notifyItemChanged(playingPos!!)
                tracks[pos].isPlaying = true
            }else{
                tracks[pos].isPlaying= isPlaying
            }
            notifyItemChanged(pos)
        }
        else {
            playingPos = pos
            tracks[pos].isPlaying = true
            notifyItemChanged(pos)
        }
    }

    inner class MyViewHolder(private val binding: ListItemTrackBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Track) {
            binding.listItem = item
            if (item.isPlaying) playingPos = adapterPosition
            binding.setClickListener {
               // changePlayingTrack(adapterPosition)
                listItemClickListener.onListItemClick(adapterPosition, item)
            }
        }
    }
}