package com.tokastudio.music_offline.ui.fragment

import android.os.Bundle
import android.util.Log

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.tokastudio.music_offline.Constants
import com.tokastudio.music_offline.interfaces.ListItemClickListener
import com.tokastudio.music_offline.adapter.ArtistAdapter
import com.tokastudio.music_offline.databinding.FragmentArtistsBinding
import com.tokastudio.music_offline.model.Track
import com.tokastudio.music_offline.ui.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * A placeholder fragment containing a simple view.
 */
class ArtistsFragment : Fragment(), ListItemClickListener {

    private lateinit var pageViewModel: PageViewModel
    private lateinit var artistAdapter: ArtistAdapter
    private lateinit var binding: FragmentArtistsBinding
    private val artists = ArrayList<List<Track>>()
    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("logLoad", "ArtistsOnCreate")

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("logLoad", "ArtistsonCreateView")
        binding = FragmentArtistsBinding.inflate(inflater, container, false)
        artistAdapter = ArtistAdapter( requireContext(),this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("logLoad", "ArtistsonViewCreated")
        loadArtists()
    }

    private fun loadArtists() {

        binding.recyclerViewArtists.apply {
            adapter = artistAdapter
        }

        mainViewModel.tracks.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
           //     CoroutineScope(Dispatchers.Main).launch {
            //        delay(Constants.DELAY_LOAD_LIST_TIME_MS)
                    fetchArtist(it)
               }
           // }
        }
    }

    private fun fetchArtist(tracks: List<Track>) {
//        if (artists.isNotEmpty()) {
//            return
//        }
        val sortedSongs = tracks.sortedBy { it.artistId }
        var preArtistId = sortedSongs[0].artistId
        var tempList = ArrayList<Track>()
        for ((index, item) in sortedSongs.withIndex()) {
            if (preArtistId == item.artistId) {
                tempList.add(item)
            } else {
                artists.add(tempList)
                tempList = ArrayList()
                tempList.add(item)
                preArtistId = item.artistId
            }
            if (index == sortedSongs.size - 1) {
                artists.add(tempList)
            }
        }
        artistAdapter.setSongs(artists)
    }

    companion object {
        private val TAG = ArtistsFragment::class.simpleName

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
        val songs = item as ArrayList<Track>
        goToArtistFragment(songs)
    }


    private fun goToArtistFragment(tracks: List<Track>) {
        val directions =
            MainFragmentDirections.actionMainFragmentToArtistFragment(tracks.toTypedArray())
        findNavController().navigate(directions)
    }
}