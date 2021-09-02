package com.tokastudio.music_offline.view.fragment.offline

import android.os.Bundle

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.tokastudio.music_offline.ListItemClickListener
import com.tokastudio.music_offline.adapter.ArtistAdapter
import com.tokastudio.music_offline.databinding.FragmentArtistsBinding
import com.tokastudio.music_offline.model.Song
import com.tokastudio.music_offline.view.fragment.OfflineFragmentDirections

/**
 * A placeholder fragment containing a simple view.
 */
class ArtistsFragment : Fragment(),ListItemClickListener {

    private lateinit var pageViewModel: PageViewModel
   // private val offlineViewModel: OfflineViewModel by activityViewModels()
   // private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var artistAdapter: ArtistAdapter
    private lateinit var binding: FragmentArtistsBinding
    private val artists= ArrayList<List<Song>>()

   // private var switchFragment: SwitchFragment?= null

  //  private var trackList: ArrayList<Song>?= null
  //  private var trackService: TrackService?= null
  //  private var currentList: String= Constants.OFFLINE_LIST

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
        pageViewModel = ViewModelProvider(this).get(PageViewModel::class.java).apply {
            setIndex(arguments?.getInt(ARG_SECTION_NUMBER).toString() ?: "1")
            arguments?.getParcelableArrayList<Song>(ARG_SONGS)?.let { setSongs(it) }
        }

        binding.recyclerViewArtists.apply {
            adapter= artistAdapter
        }

        pageViewModel.songs.observe(viewLifecycleOwner, {
            if (!it.isNullOrEmpty()) {
                pageViewModel.sectionNumber.observe(viewLifecycleOwner, { it1 ->
                    if (!it1.isNullOrEmpty()) {
                        if (it1 == "2") {
                            fetchArtist(it)
                            // trackList= it as ArrayList<Song>?
                            // artistAdapter.setSongs(it)
                        }
                    }
                })
            }
        })
    }

    private fun fetchArtist(songs: List<Song>){
        val sortedSongs= songs.sortedBy { it.artistId }
        var preArtistId= sortedSongs[0].artistId
        var tempList= ArrayList<Song>()
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
        fun newInstance(sectionNumber: Int, songs: ArrayList<Song>): ArtistsFragment {
            return ArtistsFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, sectionNumber)
                    putParcelableArrayList(ARG_SONGS, songs)
                }
            }
        }
    }

    override fun onListItemClick(position: Int, item: Any) {
        val songs= item as ArrayList<Song>
        goToArtistFragment(songs)
    }


    private fun goToArtistFragment(songs: List<Song>){
       val directions= OfflineFragmentDirections.actionOfflineFragmentToArtistFragment(songs.toTypedArray())
       findNavController().navigate(directions)
    }
}