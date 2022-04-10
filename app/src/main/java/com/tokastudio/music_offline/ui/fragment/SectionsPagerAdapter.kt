package com.tokastudio.music_offline.ui.fragment


import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.tokastudio.music_offline.model.Track

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class SectionsPagerAdapter(fragment: Fragment)
    : FragmentStateAdapter(fragment) {

    private lateinit var fragment: Fragment

    override fun getItemCount(): Int {
      return 3
    }

    override fun createFragment(position: Int): Fragment {
        Log.d("logSectionPager","createFragment")
        when(position){
            0 -> fragment = favoritesFragment()
            1 -> fragment = tracksFragment()
            2 -> fragment = artistsFragment()
        }
        return fragment
    }

    private fun favoritesFragment(): Fragment{
        return FavoritesFragment.newInstance()
    }

    private fun tracksFragment(): Fragment{
       return TracksFragment.newInstance()
    }

    private fun artistsFragment(): Fragment{
      return ArtistsFragment.newInstance()
    }

}