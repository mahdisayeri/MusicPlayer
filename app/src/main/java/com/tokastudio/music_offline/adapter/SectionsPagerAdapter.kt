package com.tokastudio.music_offline.adapter


import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.tokastudio.music_offline.ui.fragment.ArtistsFragment
import com.tokastudio.music_offline.ui.fragment.FavoritesFragment
import com.tokastudio.music_offline.ui.fragment.TracksFragment

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
        when(position){
            0 -> fragment = FavoritesFragment()
            1 -> fragment = TracksFragment()
            2 -> fragment = ArtistsFragment()
        }
        return fragment
    }
}