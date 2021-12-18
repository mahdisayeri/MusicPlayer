package com.tokastudio.music_offline.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tokastudio.music_offline.ListItemClickListener
import com.tokastudio.music_offline.databinding.ListItemTrackBinding
import com.tokastudio.music_offline.model.Song

class SongAdapter(private val listItemClickListener: ListItemClickListener): RecyclerView.Adapter<SongAdapter.MyViewHolder>() {

    private var songs: List<Song>?= null
    private var playingPos: Int?= null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
       return MyViewHolder(ListItemTrackBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        songs?.get(position)?.let { holder.bind(it, listItemClickListener) }
    }

    override fun getItemCount(): Int {
        return songs?.size ?: 0
    }

    fun setSongs(songs: List<Song>){
        this.songs= songs
        notifyDataSetChanged()
    }

    inner class MyViewHolder(private val binding: ListItemTrackBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(item: Song, listItemClickListener: ListItemClickListener){
            binding.listItem= item
            if (item.isPlaying) playingPos= adapterPosition

            binding.setClickListener {
                if (playingPos != null){
                    if (playingPos == adapterPosition){
                      //  songs?.get(adapterPosition)?.isPlaying = songs?.get(adapterPosition)?.isPlaying != true
                      //  notifyItemChanged(adapterPosition)
                    }else{
                        songs?.get(playingPos!!)?.isPlaying= false

                        notifyItemChanged(playingPos!!)

                        songs?.get(adapterPosition)?.isPlaying= true
                        binding.equalizer.animateBars()
                        notifyItemChanged(adapterPosition)
                    }
                }else{
                    playingPos=adapterPosition
                    songs?.get(adapterPosition)?.isPlaying= true
                    binding.equalizer.animate()
                    notifyItemChanged(adapterPosition)
                }
                listItemClickListener.onListItemClick(adapterPosition, item)
            }
        }
    }
}