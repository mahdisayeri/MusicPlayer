package com.tokastudio.music_offline.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.tokastudio.music_offline.interfaces.ListItemClickListener
import com.tokastudio.music_offline.adapter.TrackAdapter
import com.tokastudio.music_offline.databinding.FragmentArtistBinding
import com.tokastudio.music_offline.model.CurrentPlayingSong
import com.tokastudio.music_offline.model.Track
import com.tokastudio.music_offline.service.TrackService
import com.tokastudio.music_offline.ui.MainViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class ArtistFragment : Fragment(), ListItemClickListener {

    companion object {
        fun newInstance() = ArtistFragment()
        private val TAG = TracksFragment::class.simpleName
    }

    private val viewModel: ArtistViewModel by viewModel()
    private val mainViewModel: MainViewModel by activityViewModels()
    private lateinit var binding: FragmentArtistBinding
    private val args: ArtistFragmentArgs by navArgs()
    private lateinit var trackAdapter: TrackAdapter

    private var trackList= listOf<Track>()
    private var trackService: TrackService? = null
    private var currentList: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!args.tracks.isNullOrEmpty()){
            viewModel.setTracks(args.tracks.toList())
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentArtistBinding.inflate(inflater, container, false).apply {
            toolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }

        }
        trackAdapter = TrackAdapter(requireContext(),this)
        binding.recyclerViewArtistSongs.apply {
            adapter = trackAdapter
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainViewModel.trackService.observe(viewLifecycleOwner) {
            trackService = it
        }

        viewModel.tracks.observe(viewLifecycleOwner){
            if (it.isNotEmpty() && trackList.isEmpty()){
                trackList= it
                currentList =it[0].artistName
                binding.toolbar.title = it[0].artistName
                trackAdapter.setTracks(it)
            }
        }

        mainViewModel.currentPlayingSong.observe(viewLifecycleOwner) {
            val index = trackList.indexOf(it.track)
            if (index != -1) {
                trackAdapter.changePlayingTrack(index, it.track.isPlaying)
            }
        }
    }

    override fun onListItemClick(position: Int, item: Any) {
        if (trackService?.currentPlayingList != currentList) {
            trackService?.currentPlayingList = currentList
            trackService?.setTrackList(trackList)
        }
        val song = item as Track
        mainViewModel.setCurrentSong(CurrentPlayingSong(position, song, false))
    }
}