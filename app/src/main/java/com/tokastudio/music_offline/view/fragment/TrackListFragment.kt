package com.tokastudio.music_offline.view.fragment

import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.squareup.picasso.Picasso
import com.tokastudio.music_offline.R
import com.tokastudio.music_offline.SharedPref
import com.tokastudio.music_offline.Constants
import com.tokastudio.music_offline.databinding.FragmentTrackListBinding
import com.tokastudio.music_offline.service.TrackService
import com.tokastudio.music_offline.adapter.TrackAdapter
import com.tokastudio.music_offline.model.Song
import com.tokastudio.music_offline.view.activity.SharedViewModel
import com.tokastudio.music_offline.viewmodels.TrackListViewModel

/**
 * A simple [Fragment] subclass.
 * Use the [TrackListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TrackListFragment : Fragment() , TrackAdapter.ItemClickListener{

    private lateinit var binding: FragmentTrackListBinding
    private val args: TrackListFragmentArgs by navArgs()
    private var trackList= arrayListOf<Song>()
    private var trackService: TrackService?= null
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var currentList: String
    private lateinit var viewModel: TrackListViewModel
    private val sharedViewModel: SharedViewModel by activityViewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "onCreate")
        currentList = args.playingListName
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        Log.i(TAG, "onCreate")
        sharedViewModel.trackService.observe(viewLifecycleOwner,{
            trackService= it
        })
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_track_list, container, false)
        binding.clickHandler=OnClickHandler()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i(TAG, "onViewCreated")
        trackAdapter =  TrackAdapter(context,this)
        binding.trackListRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.trackListRecyclerView.setHasFixedSize(true)
        binding.trackListRecyclerView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        binding.trackListRecyclerView.adapter = trackAdapter

        trackList.clear()
        viewModel = ViewModelProvider(this).get(TrackListViewModel::class.java)
        viewModel.getTracks().observe(viewLifecycleOwner, androidx.lifecycle.Observer { tracks ->
                if(currentList == Constants.ALL_LIST){
                    trackList.addAll(tracks)
                }else if (currentList == Constants.FAVORITE_LIST){
                  val favListId =SharedPref.getPrefFav(requireActivity())
                    for (id in favListId) {
                        for (track in tracks) {
                            if (id == track.id) {
                                trackList.add(track)
                            }
                        }
                    }
                }

                if (trackList.isEmpty()) {
                    binding.emptyList.visibility = View.VISIBLE
                }else{
                    binding.emptyList.visibility = View.INVISIBLE
                    if (trackService?.isPlaying == true && trackService?.currentPlayingList==currentList) {
                        loadTrackCover()
                        binding.trackListControlPanel.visibility = View.VISIBLE
                        binding.playingTrack= trackService?.currentTrack
                    } else {
                        binding.trackListControlPanel.visibility = View.GONE
                    }
                    trackAdapter.setTrackList(trackList)
                }
        })


    }


    private fun loadTrackCover(){
        val trackCover = BitmapDrawable(resources, trackService?.currentTrackCover )
        val uri: Uri? = null
        Picasso.get()
                .load(uri)
                .fit()
                .placeholder(trackCover)
                .into(binding.playingTrackCover)
    }

  inner class OnClickHandler{
        fun onControlPanel(view: View){
            startTrackPlayingFragment(trackService?.currentPosition)
        }
    }

    override fun onItemListClick(position: Int) {
        if(trackService?.currentPlayingList!=currentList){
            trackService?.currentPlayingList=currentList
            trackService?.setTrackList(trackList)
        }
        startTrackPlayingFragment(position)
    }

    private fun startTrackPlayingFragment(position: Int?) {
       if (position!= null){
           val directions= TrackListFragmentDirections.actionTrackListFragmentToTrackPlayingFragment(position,trackList[position])
           findNavController().navigate(directions)
       }
    }

    companion object {
        // TODO: Rename parameter arguments, choose names that match
        private val TAG = TrackListFragment::class.java.simpleName

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param playingList Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MusicListFragment.
         */
        @JvmStatic
        fun newInstance()= TrackListFragment()
    }
}