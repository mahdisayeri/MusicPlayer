package com.tokastudio.music_offline.view.fragment.offline

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.tokastudio.music_offline.Constants
import com.tokastudio.music_offline.ListItemClickListener
import com.tokastudio.music_offline.adapter.SongAdapter
import com.tokastudio.music_offline.databinding.FragmentSongsBinding
import com.tokastudio.music_offline.model.CurrentPlayingSong
import com.tokastudio.music_offline.model.Song
import com.tokastudio.music_offline.service.TrackService
import com.tokastudio.music_offline.view.activity.SharedViewModel
import com.tokastudio.music_offline.view.fragment.OfflineFragmentDirections
import com.tokastudio.music_offline.view.fragment.OfflineViewModel

/**
 * A placeholder fragment containing a simple view.
 */
class SongsFragment : Fragment(),ListItemClickListener {

    private lateinit var pageViewModel: PageViewModel
  //  private val offlineViewModel: SharedViewModel by activityViewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var songAdapter: SongAdapter
    private lateinit var binding: FragmentSongsBinding

    private var trackList: ArrayList<Song>?= null
    private var trackService: TrackService?= null
    private var currentList: String= Constants.OFFLINE_LIST

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        songAdapter= SongAdapter(this)
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding= FragmentSongsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedViewModel.trackService.observe(viewLifecycleOwner,{
            trackService= it
        })

        pageViewModel = ViewModelProvider(this).get(PageViewModel::class.java).apply {
            setIndex(arguments?.getInt(ARG_SECTION_NUMBER).toString() ?: "1")
            arguments?.getParcelableArrayList<Song>(ARG_SONGS)?.let { setSongs(it) }
        }

        binding.recyclerView.apply {
            adapter= songAdapter
        }

        pageViewModel.songs.observe(viewLifecycleOwner, {
            if (!it.isNullOrEmpty()) {
                pageViewModel.sectionNumber.observe(viewLifecycleOwner, { it1 ->
                    if (!it1.isNullOrEmpty()) {
                        if (it1 == "1") {
                            trackList= it as ArrayList<Song>?
                            songAdapter.setSongs(it)
                        }
                    }
                })
            }
        })
    }

    companion object {
        private val TAG= SongsFragment::class.simpleName
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
        fun newInstance(sectionNumber: Int, songs: ArrayList<Song>): SongsFragment {
            return SongsFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, sectionNumber)
                    putParcelableArrayList(ARG_SONGS, songs)
                }
            }
        }
    }

    override fun onListItemClick(position: Int, item: Any) {
        if(trackService?.currentPlayingList!=currentList){
            trackService?.currentPlayingList=currentList
            trackList?.let { trackService?.setTrackList(it) }
        }
        val song= item as Song
        song.title?.let { Log.d(TAG, it) }
      //  playTrack(item)
        sharedViewModel.setCurrentSong(CurrentPlayingSong(position,song))
        startTrackPlayingFragment(position)
    }

    private fun startTrackPlayingFragment(position: Int?) {
        if (position!= null){
            val directions= trackList?.get(position)?.let { OfflineFragmentDirections.actionOfflineFragmentToTrackPlayingFragment(position, it) }
            if (directions != null) {
                findNavController().navigate(directions)
            }
        }
    }
}