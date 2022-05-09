package com.tokastudio.music_offline.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.tokastudio.music_offline.Constants
import com.tokastudio.music_offline.interfaces.ListItemClickListener
import com.tokastudio.music_offline.adapter.TrackAdapter
import com.tokastudio.music_offline.databinding.FragmentTracksBinding
import com.tokastudio.music_offline.model.CurrentPlayingSong
import com.tokastudio.music_offline.model.Track
import com.tokastudio.music_offline.service.TrackService
import com.tokastudio.music_offline.ui.MainViewModel

/**
 * A placeholder fragment containing a simple view.
 */
class TracksFragment : Fragment(), ListItemClickListener {

    private val mainViewModel: MainViewModel by activityViewModels()
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var binding: FragmentTracksBinding

    private var trackList= listOf<Track>()
    private var trackService: TrackService? = null
    private var currentList: String = Constants.OFFLINE_LIST

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        trackAdapter = TrackAdapter(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentTracksBinding.inflate(inflater, container, false)
        binding.recyclerView.apply {
            adapter = trackAdapter
            setHasFixedSize(true)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

            mainViewModel.trackService.observe(viewLifecycleOwner) {
                trackService = it
            }
            mainViewModel.tracks.observe(viewLifecycleOwner) {
                checkList(it)
             //   CoroutineScope(Dispatchers.Main).launch {
             //       delay(200)
                    trackList= it
                    trackAdapter.setTracks(it)
             //   }

            }

            mainViewModel.currentPlayingSong.observe(viewLifecycleOwner) {
                val index = trackList.indexOf(it.track)
                if (index != -1 && index < trackList.size) {
                    trackAdapter.changePlayingTrack(index, it.track.isPlaying)
                }
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

    companion object {
        private val TAG = TracksFragment::class.simpleName

        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private const val ARG_SECTION_NUMBER = "section_number"
        private const val ARG_SONGS = "songs"

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
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