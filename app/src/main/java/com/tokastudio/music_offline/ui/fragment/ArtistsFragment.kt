package com.tokastudio.music_offline.ui.fragment

import android.os.Bundle

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.tokastudio.music_offline.ListItemClickListener
import com.tokastudio.music_offline.adapter.ArtistAdapter
import com.tokastudio.music_offline.databinding.FragmentArtistsBinding
import com.tokastudio.music_offline.model.Track
import com.tokastudio.music_offline.ui.MainViewModel

/**
 * A placeholder fragment containing a simple view.
 */
class ArtistsFragment : Fragment(),ListItemClickListener {

    private lateinit var pageViewModel: PageViewModel
    private lateinit var artistAdapter: ArtistAdapter
    private lateinit var binding: FragmentArtistsBinding
    private val artists= ArrayList<List<Track>>()
    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        artistAdapter= ArtistAdapter(this)
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding= FragmentArtistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadArtists()
    }

    private fun loadArtists(){
//        pageViewModel = ViewModelProvider(this).get(PageViewModel::class.java).apply {
//            setIndex(arguments?.getInt(ARG_SECTION_NUMBER).toString() ?: "1")
//            arguments?.getParcelableArrayList<Track>(ARG_SONGS)?.let { setSongs(it) }
//        }

        binding.recyclerViewArtists.apply {
            adapter= artistAdapter
        }

        mainViewModel.tracks.observe(viewLifecycleOwner,{
            if (!it.isNullOrEmpty()) {
                    fetchArtist(it)
            }
        })

//        pageViewModel.songs.observe(viewLifecycleOwner, {
//            if (!it.isNullOrEmpty()) {
//                pageViewModel.sectionNumber.observe(viewLifecycleOwner, { it1 ->
//                    if (!it1.isNullOrEmpty()) {
//                        if (it1 == "3") {
//                            fetchArtist(it)
//                            // trackList= it as ArrayList<Song>?
//                            // artistAdapter.setSongs(it)
//                        }
//                    }
//                })
//            }
//        })
    }

    private fun fetchArtist(tracks: List<Track>){
        if (artists.isNotEmpty()){
            return
        }
        val sortedSongs= tracks.sortedBy { it.artistId }
        var preArtistId= sortedSongs[0].artistId
        var tempList= ArrayList<Track>()
        for((index,item) in sortedSongs.withIndex()){
            if (preArtistId == item.artistId ){
                tempList.add(item)
            }else{
                artists.add(tempList)
                tempList= ArrayList()
                tempList.add(item)
                preArtistId= item.artistId
            }
            if (index == sortedSongs.size-1){
                artists.add(tempList)
            }
        }
        artistAdapter.setSongs(artists)
    }

    companion object {
        private val TAG= ArtistsFragment::class.simpleName
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
        fun newInstance()= ArtistsFragment()
//        {
//            return ArtistsFragment().apply {
//                arguments = Bundle().apply {
//                    putInt(ARG_SECTION_NUMBER, sectionNumber)
//                    putParcelableArrayList(ARG_SONGS, tracks)
//                }
//            }
//        }
    }

    override fun onListItemClick(position: Int, item: Any) {
        val songs= item as ArrayList<Track>
        goToArtistFragment(songs)
    }


    private fun goToArtistFragment(tracks: List<Track>){
       val directions= MainFragmentDirections.actionMainFragmentToArtistFragment(tracks.toTypedArray())
       findNavController().navigate(directions)
    }
}