package com.tokastudio.music_offline.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.tokastudio.music_offline.Constants
import com.tokastudio.music_offline.ListItemClickListener
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

    private lateinit var pageViewModel: PageViewModel

    private val mainViewModel: MainViewModel by activityViewModels()
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var binding: FragmentTracksBinding

    private var trackList: ArrayList<Track>? = null
    private var trackService: TrackService? = null
    private var currentList: String = Constants.OFFLINE_LIST

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("logTest","onCreate TracksFragment")

        trackAdapter = TrackAdapter(this)
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        Log.d("logTest","onCreateView TracksFragment")

        binding = FragmentTracksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("logTest","onViewCreated TracksFragment")

        mainViewModel.trackService.observe(viewLifecycleOwner, {
            trackService = it
        })

//        pageViewModel = ViewModelProvider(this).get(PageViewModel::class.java).apply {
//            setIndex(arguments?.getInt(ARG_SECTION_NUMBER).toString() ?: "1")
//            arguments?.getParcelableArrayList<Track>(ARG_SONGS)?.let { setSongs(it) }
//        }

        binding.recyclerView.apply {
            adapter = trackAdapter
        }

//        pageViewModel.songs.observe(viewLifecycleOwner, {
//            if (!it.isNullOrEmpty()) {
//                pageViewModel.sectionNumber.observe(viewLifecycleOwner, { it1 ->
//                    if (!it1.isNullOrEmpty()) {
//                        if (it1 == "2") {
//                            trackList = it as ArrayList<Track>?
//                            trackAdapter.setTracks(it)
//                        }
//                    }
//                })
//            }
//        })

        mainViewModel.tracks.observe(viewLifecycleOwner,{
            if (!it.isNullOrEmpty()){
                trackList = it as ArrayList<Track>?
                trackAdapter.setTracks(it)
            }
        })


        mainViewModel.currentPlayingSong.observe(viewLifecycleOwner, {
            val index = trackList?.indexOf(it.track)
            if (index != null && index != -1) {
             //   val audioSessionID= trackService?.mediaPlayer?.audioSessionId
                //if (audioSessionID != -1&& audioSessionID!= null ){
                    trackAdapter.changePlayingTrack(index,it.track.isPlaying)
             //   }


            }
        })

    }

    override fun onResume() {
        super.onResume()
        Log.d("logTest","onResume TracksFragment")
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
        @JvmStatic
        fun newInstance()= TracksFragment()
//        {
//            return TracksFragment().apply {
//                arguments = Bundle().apply {
//                    putInt(ARG_SECTION_NUMBER, sectionNumber)
//                    putParcelableArrayList(ARG_SONGS, tracks)
//                }
//            }
//        }
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