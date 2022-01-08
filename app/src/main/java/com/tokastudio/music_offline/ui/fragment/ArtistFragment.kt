package com.tokastudio.music_offline.ui.fragment

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.tokastudio.music_offline.ListItemClickListener
import com.tokastudio.music_offline.adapter.TrackAdapter
import com.tokastudio.music_offline.databinding.FragmentArtistBinding
import com.tokastudio.music_offline.model.CurrentPlayingSong
import com.tokastudio.music_offline.model.Track
import com.tokastudio.music_offline.service.TrackService
import com.tokastudio.music_offline.ui.MainViewModel

class ArtistFragment : Fragment(), ListItemClickListener {

    companion object {
        fun newInstance() = ArtistFragment()
        private val TAG = TracksFragment::class.simpleName
    }

    private lateinit var viewModel: ArtistViewModel
    private val mainViewModel: MainViewModel by activityViewModels()
    private lateinit var binding: FragmentArtistBinding
    private val args: ArtistFragmentArgs by navArgs()
    private lateinit var trackAdapter: TrackAdapter

    private var trackList: List<Track>?= null
    private var trackService: TrackService? = null
    private var currentList: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        trackAdapter = TrackAdapter(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentArtistBinding.inflate(inflater, container, false).apply {
            toolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }

        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(ArtistViewModel::class.java)
        if (!args.tracks.isNullOrEmpty()){
            trackList= args.tracks.toList()
        }

        mainViewModel.trackService.observe(viewLifecycleOwner, {
            trackService = it
        })
        binding.recyclerViewArtistSongs.apply {
            adapter = trackAdapter
        }

        mainViewModel.currentPlayingSong.observe(viewLifecycleOwner, {
            val index = trackList?.indexOf(it.track)
            if (index != -1 && index!= null) {
                trackAdapter.changePlayingTrack(index,it.track.isPlaying)
            }
        })

        if (!trackList.isNullOrEmpty()) {
            currentList = trackList!![0].artistName
            binding.toolbar.title = trackList!![0].artistName
            trackAdapter.setTracks(trackList!!)
        }


    }

    override fun onListItemClick(position: Int, item: Any) {
        if (trackService?.currentPlayingList != currentList) {
            trackService?.currentPlayingList = currentList
            trackList?.let { trackService?.setTrackList(it) }
        }
        val song = item as Track
        mainViewModel.setCurrentSong(CurrentPlayingSong(position, song, false))
    }
}