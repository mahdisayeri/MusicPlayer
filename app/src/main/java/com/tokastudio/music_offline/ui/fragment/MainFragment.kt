package com.tokastudio.music_offline.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.tokastudio.music_offline.R
import com.tokastudio.music_offline.databinding.FragmentMainBinding

private val TAB_TITLES = arrayOf(
        R.string.tab_title_favorites,
        R.string.tab_title_tracks,
        R.string.tab_title_artists
)

class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding
//    private var permissionIsGranted= false
    private var viewPager: ViewPager2?= null
    private lateinit var sectionsPagerAdapter: SectionsPagerAdapter
   // private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding= FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
       // viewModel = ViewModelProvider(this).get(OfflineViewModel::class.java)

//        if (permissionIsGranted){
//           getSongs()
//        }else{
//            checkPermission()
//        }

        setTabLayout()
//        mainViewModel.tracks.observe(viewLifecycleOwner,{
//            if (!it.isNullOrEmpty()){
//                setTabLayout(it as ArrayList<Track>)
//            }
//        })

    }

    inner class ClickHandler{

    }

//    private fun getSongs(){
//        val list: MutableList<Track> = musicFiles()
//        setTabLayout(list as ArrayList<Track>)
//    }

    private fun setTabLayout(){
        sectionsPagerAdapter = SectionsPagerAdapter(this)
        viewPager = binding.viewPager
        viewPager?.adapter = sectionsPagerAdapter
        val tabs: TabLayout = binding.tabs
            TabLayoutMediator(tabs, viewPager!!){ tab, position ->
                when(position){
                    0 -> {
                        tab.text = resources.getString(TAB_TITLES[0])
                    }
                    1 -> {
                        tab.text = resources.getString(TAB_TITLES[1])
                    }
                    2 -> {
                        tab.text = resources.getString(TAB_TITLES[2])
                    }
                }
            }.attach()
        viewPager?.currentItem = 0
    }

//    private fun musicFiles(): MutableList<Track>{
//        val list: MutableList<Track> = mutableListOf()
//        val uri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
//        val selection= MediaStore.Audio.Media.IS_MUSIC+ "!= 0"
//        val sortOrder= MediaStore.Audio.Media.TITLE + " ASC"
//
//        val cursor: Cursor?= requireActivity().contentResolver.query(
//                uri,
//                null,
//                selection,
//                null,
//                sortOrder)
//        if (cursor!= null && cursor.moveToFirst()){
//            val id:Int = cursor.getColumnIndex(MediaStore.Audio.Media._ID)
//            val title:Int = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
//            val trackNumber:Int = cursor.getColumnIndex(MediaStore.Audio.Media.TRACK)
//            val year:Int = cursor.getColumnIndex(MediaStore.Audio.Media.YEAR)
//            val duration: Int = cursor.getColumnIndex("duration")
//            val data: Int= cursor.getColumnIndex("_data")
//            val dateModified:Int = cursor.getColumnIndex(MediaStore.Audio.Media.DATE_MODIFIED)
//            val albumId: Int= cursor.getColumnIndex("album_id")
//            val albumName:Int = cursor.getColumnIndex("album")
//            val artistId: Int= cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID)
//            val artistName: Int= cursor.getColumnIndex("artist")
//
//            for (col in cursor.columnNames){
//                Log.d(TAG, "colName= $col")
//            }
//
//            // Now loop through the music files
//            do {
//                val audioId:Long = cursor.getLong(id)
//                val audioTitle:String = cursor.getString(title)
//                val audioTrackNumber:Int = cursor.getInt(trackNumber)
//                val audioYear:Int= cursor.getInt(year)
//                val audioDuration:Long= cursor.getLong(duration)
//                val audioData:String= cursor.getString(data)
//                val audioDateModified:Long= cursor.getLong(dateModified)
//                val audioAlbumId:Long= cursor.getLong(albumId)
//                val audioAlbumName:String= cursor.getString(albumName)
//                val audioArtistId:Long= cursor.getLong(artistId)
//                val audioArtistName:String= cursor.getString(artistName)
//
//                Log.d(TAG, "audioAlbum= $audioAlbumId")
//                Log.d(TAG, "audioAlbumId= $audioAlbumName")
//                Log.d(TAG, "audioAlbumArtist= $audioArtistName")
//
//                // Add the current music to the list
//                list.add(Track(audioId, audioTitle, audioTrackNumber, audioYear,
//                        audioDuration, audioData, audioDateModified, audioAlbumId,
//                        audioAlbumName, audioArtistId, audioArtistName, "", isPlaying = false,false))
//            }while (cursor.moveToNext())
//        }
//        cursor?.close()
//        return list
//    }

//    private fun checkPermission(){
//        TedPermission.with(context)
//                .setPermissionListener(this)
//                .setDeniedMessage("If you reject permission,you can not use this service \n\n Please turn on permissions at Setting => Permission")
//                .setPermissions(android.Manifest.permission.READ_EXTERNAL_STORAGE)
//                .check()
//    }

    companion object {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private const val ARG_PARAM1 = "param1"
        private val TAG = MainFragment::class.java.simpleName

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param playingList Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MusicListFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance()= MainFragment()
//        {
//            val fragment = OfflineFragment()
//            val args = Bundle()
//          //  args.putString(ARG_PARAM1, playingList)
//            fragment.arguments = args
//            return fragment
//        }
    }

//    override fun onPermissionGranted() {
//        permissionIsGranted= true
//        getSongs()
//        //  Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show();
//    }
//
//    override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
//        permissionIsGranted= false
//        Toast.makeText(context, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
//    }
}