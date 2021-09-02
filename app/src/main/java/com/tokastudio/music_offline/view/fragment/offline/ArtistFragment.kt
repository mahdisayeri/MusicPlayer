package com.tokastudio.music_offline.view.fragment.offline

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.tokastudio.music_offline.Constants
import com.tokastudio.music_offline.ListItemClickListener
import com.tokastudio.music_offline.R
import com.tokastudio.music_offline.adapter.SongAdapter
import com.tokastudio.music_offline.databinding.FragmentArtistBinding
import com.tokastudio.music_offline.model.CurrentPlayingSong
import com.tokastudio.music_offline.model.Song
import com.tokastudio.music_offline.service.TrackService
import com.tokastudio.music_offline.view.activity.SharedViewModel

class ArtistFragment : Fragment(),ListItemClickListener {

    companion object {
        fun newInstance() = ArtistFragment()
        private val TAG= SongsFragment::class.simpleName
    }
    private lateinit var viewModel: ArtistViewModel
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var binding: FragmentArtistBinding
    private val args: ArtistFragmentArgs by navArgs()
    private lateinit var songAdapter: SongAdapter

   // private var trackList: ArrayList<Song>?= null
    private var trackService: TrackService?= null
    private var currentList: String= Constants.OFFLINE_LIST

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        songAdapter= SongAdapter(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding= DataBindingUtil.inflate(inflater,R.layout.fragment_artist,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedViewModel.trackService.observe(viewLifecycleOwner,{
            trackService= it
        })
        binding.recyclerViewArtistSongs.apply {
            adapter= songAdapter
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ArtistViewModel::class.java)
        songAdapter.setSongs(args.songs.toList())
    }

    override fun onListItemClick(position: Int, item: Any) {
        if(trackService?.currentPlayingList!=currentList){
            trackService?.currentPlayingList=currentList
           trackService?.setTrackList(args.songs.toList())
        }
        val song= item as Song
        song.title?.let { Log.d(TAG, it) }
        //  playTrack(item)
        sharedViewModel.setCurrentSong(CurrentPlayingSong(position,song))
        startTrackPlayingFragment(position)
    }

    private fun startTrackPlayingFragment(position: Int?) {
        if (position!= null){
            val directions= ArtistFragmentDirections.actionArtistFragmentToTrackPlayingFragment(position, args.songs[position])
                findNavController().navigate(directions)
        }
    }
}