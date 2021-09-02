package com.tokastudio.music_offline.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tokastudio.music_offline.ListItemClickListener
import com.tokastudio.music_offline.databinding.ListItemArtistBinding
import com.tokastudio.music_offline.databinding.ListItemSongBinding
import com.tokastudio.music_offline.model.Song

class ArtistAdapter(private val listItemClickListener: ListItemClickListener): RecyclerView.Adapter<ArtistAdapter.MyViewHolder>() {

    private var songs: List<List<Song>>?= null
   // private var playingPos: Int?= null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
       return MyViewHolder(ListItemArtistBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        songs?.get(position)?.let { holder.bind(it, listItemClickListener) }
    }

    override fun getItemCount(): Int {
        return songs?.size ?: 0
    }

    fun setSongs(songs: List<List<Song>>){
        this.songs= songs
        notifyDataSetChanged()
    }

    inner class MyViewHolder(private val binding: ListItemArtistBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(item: List<Song>, listItemClickListener: ListItemClickListener){
            binding.listItem= item[0]

            binding.setClickListener {
                listItemClickListener.onListItemClick(adapterPosition, item)
            }
        }
    }
}