package com.tokastudio.music_offline.ui.fragment

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.tokastudio.music_offline.Constants
import com.tokastudio.music_offline.interfaces.ListItemClickListener
import com.tokastudio.music_offline.SharedPref
import com.tokastudio.music_offline.adapter.TrackAdapter
import com.tokastudio.music_offline.databinding.FragmentFavoritesBinding
import com.tokastudio.music_offline.model.CurrentPlayingSong
import com.tokastudio.music_offline.model.Track
import com.tokastudio.music_offline.service.TrackService
import com.tokastudio.music_offline.ui.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FavoritesFragment : Fragment(), ListItemClickListener {

    companion object {

    }

    private val mainViewModel: MainViewModel by activityViewModels()
    private lateinit var binding: FragmentFavoritesBinding
    private lateinit var viewModel: FavoritesViewModel
    private var trackService: TrackService? = null
    private lateinit var trackAdapter: TrackAdapter
    private var currentList: String = Constants.FAVORITE_LIST
    private var trackList= listOf<Track>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        trackAdapter = TrackAdapter(requireContext(),this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(FavoritesViewModel::class.java)

        mainViewModel.trackService.observe(viewLifecycleOwner) {
            trackService = it
        }
        binding.recyclerView.apply {
            adapter = trackAdapter
        }

        mainViewModel.tracks.observe(viewLifecycleOwner) { it1 ->
          //  CoroutineScope(Dispatchers.Main).launch {
           //     delay(Constants.DELAY_LOAD_LIST_TIME_MS)
                val favTracks = ArrayList<Track>()
                val favListIds = SharedPref.getPrefFav(requireActivity())
                for (item in favListIds) {
                    val track = it1.find { it.id == item }
                    if (track != null) {
                        favTracks.add(track)
                    }
                }
                viewModel.setFavList(favTracks)

                mainViewModel.currentPlayingSong.observe(viewLifecycleOwner) {
                    val index = trackList.indexOf(it.track)
                    if (index != -1 && index < trackList.size) {
                        trackAdapter.changePlayingTrack(index, it.track.isPlaying)
                    }
                }

                viewModel.favList.observe(viewLifecycleOwner) {
                    checkList(it)
                    if (!it.isNullOrEmpty()) {
                        trackList = it as ArrayList<Track>
                        trackAdapter.setTracks(it)
                    }
                }
         //   }
        }
    }

    private fun checkList(list: List<Track>?){
        if (list.isNullOrEmpty()){
            binding.recyclerView.visibility= View.INVISIBLE
            binding.emptyList.visibility= View.VISIBLE
        }else{
            binding.recyclerView.visibility= View.VISIBLE
            binding.emptyList.visibility= View.INVISIBLE
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