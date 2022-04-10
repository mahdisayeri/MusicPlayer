package com.tokastudio.music_offline.ui.fragment

import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.tokastudio.music_offline.R
import com.tokastudio.music_offline.model.Track
import com.tokastudio.music_offline.ui.MainViewModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SplashScreenFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SplashScreenFragment : Fragment(),PermissionListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private val mainViewModel: MainViewModel by activityViewModels()
    private var permissionIsGranted = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_splash_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (permissionIsGranted) {
            getSongs()
        } else {
            checkPermission()
        }
    }


    private fun getSongs() {
        val list: MutableList<Track> = musicFiles()
        mainViewModel.setTracks(list)
        findNavController().navigate(SplashScreenFragmentDirections.actionSplashScreenFragmentToMainFragment())
    }

    private fun musicFiles(): MutableList<Track> {
        val list: MutableList<Track> = mutableListOf()
        val uri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0"
        val sortOrder = MediaStore.Audio.Media.TITLE + " ASC"
        val cursor: Cursor? = requireContext().contentResolver.query(
                uri,
                null,
                selection,
                null,
                sortOrder)
        if (cursor != null && cursor.moveToFirst()) {
            val id: Int = cursor.getColumnIndex(MediaStore.Audio.Media._ID)
            val title: Int = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val trackNumber: Int = cursor.getColumnIndex(MediaStore.Audio.Media.TRACK)
            val year: Int = cursor.getColumnIndex(MediaStore.Audio.Media.YEAR)
            val duration: Int = cursor.getColumnIndex("duration")
            val data: Int = cursor.getColumnIndex("_data")
            val dateModified: Int = cursor.getColumnIndex(MediaStore.Audio.Media.DATE_MODIFIED)
            val albumId: Int = cursor.getColumnIndex("album_id")
            val albumName: Int = cursor.getColumnIndex("album")
            val artistId: Int = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID)
            val artistName: Int = cursor.getColumnIndex("artist")

            // Now loop through the music files
            do {
                val audioId: Long = cursor.getLong(id)
                val audioTitle: String = cursor.getString(title)
                val audioTrackNumber: Int = cursor.getInt(trackNumber)
                val audioYear: Int = cursor.getInt(year)
                val audioDuration: Long = cursor.getLong(duration)
                val audioData: String = cursor.getString(data)
                val audioDateModified: Long = cursor.getLong(dateModified)
                val audioAlbumId: Long = cursor.getLong(albumId)
                val audioAlbumName: String = cursor.getString(albumName)
                val audioArtistId: Long = cursor.getLong(artistId)
                val audioArtistName: String = cursor.getString(artistName)

                // Add the current music to the list

                list.add(Track(audioId, audioTitle, audioTrackNumber, audioYear,
                        audioDuration, audioData, audioDateModified, audioAlbumId,
                        audioAlbumName, audioArtistId, audioArtistName, "", isPlaying = false, false))
            } while (cursor.moveToNext())
        }
        cursor?.close()
        return list
    }

    private fun checkPermission() {
        TedPermission.with(context)
                .setPermissionListener(this)
                .setDeniedMessage("If you reject permission,you can not use this service \n\n Please turn on permissions at Setting => Permission")
                .setPermissions(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                .check()
    }

    override fun onPermissionGranted() {
        permissionIsGranted = true
        getSongs()
    }

    override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
        permissionIsGranted = false
        Toast.makeText(context, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SplashScreenFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                SplashScreenFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}