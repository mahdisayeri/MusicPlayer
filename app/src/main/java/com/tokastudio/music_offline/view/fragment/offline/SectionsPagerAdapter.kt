package com.tokastudio.music_offline.view.fragment.offline


import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.tokastudio.music_offline.model.Song

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class SectionsPagerAdapter(fragment: Fragment, private var songs: ArrayList<Song>)
    : FragmentStateAdapter(fragment) {

    private lateinit var fragment: Fragment

    override fun getItemCount(): Int {
      return 2
    }

    override fun createFragment(position: Int): Fragment {
        when(position){
            0 -> fragment = songsFragment(position)
            1 -> fragment = artistsFragment(position)
        }
        return fragment
    }

    private fun songsFragment(position: Int): Fragment{
       return SongsFragment.newInstance(position + 1, songs)
    }

    private fun artistsFragment(position: Int): Fragment{
      return ArtistsFragment.newInstance(position + 1, songs)
    }

}