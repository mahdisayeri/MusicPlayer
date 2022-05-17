package com.tokastudio.music_offline.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.color.MaterialColors
import com.tokastudio.music_offline.R
import com.tokastudio.music_offline.interfaces.ListItemClickListener
import com.tokastudio.music_offline.databinding.ListItemTrackBinding
import com.tokastudio.music_offline.model.Track
import com.tokastudio.music_offline.utilitis.getColorFromAttr

class TrackAdapter(private val context: Context,private val listItemClickListener: ListItemClickListener) : RecyclerView.Adapter<TrackAdapter.MyViewHolder>() {

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

    fun resetPlayingTrack(){
        playingPos?.let { notifyItemChanged(it) }
    }

    inner class MyViewHolder(private val binding: ListItemTrackBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Track) {
         //   binding.cardView.animation= AnimationUtils.loadAnimation(context,R.anim.fade_animation)
            binding.title.text= item.title
            binding.artistName.text= item.artistName
            if (item.isPlaying){
                val colorAccent= context.getColorFromAttr(R.attr.colorAccent)
                binding.title.setTextColor(colorAccent)
                binding.artistName.setTextColor(colorAccent)
                binding.vuMeter.visibility= View.VISIBLE
                playingPos = adapterPosition
            }else{
                val colorOnPrimary= context.getColorFromAttr(R.attr.colorOnPrimary)
                binding.title.setTextColor(colorOnPrimary)
                binding.artistName.setTextColor(colorOnPrimary)
                binding.vuMeter.visibility= View.INVISIBLE
            }
            loadImage(binding.image,item.getCover())

            binding.cardView.setOnClickListener {
                listItemClickListener.onListItemClick(adapterPosition, item)
            }
        }

        private fun loadImage(view: ImageView, image: ByteArray?){
            Glide.with(view.context)
                .load(image)
                .fitCenter()
                .placeholder(R.drawable.music_icon_placeholder)
                .into(view)
        }
    }
}