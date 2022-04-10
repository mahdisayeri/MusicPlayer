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
    private var viewPager: ViewPager2?= null
    private lateinit var sectionsPagerAdapter: SectionsPagerAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding= FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTabLayout()
    }

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
       // viewPager?.currentItem = 1
    }

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
    }
}