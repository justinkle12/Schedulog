package com.example.schedulog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.schedulog.databinding.FragmentFeedAndCalendarBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import timber.log.Timber


class FeedAndCalendarFragment : Fragment() {
    private var _binding: FragmentFeedAndCalendarBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding =
            FragmentFeedAndCalendarBinding.inflate(inflater, container, false)

        // Set viewPager adapter
        val pagerAdapter = FeedPagerAdapter(this)
        binding.viewPager.adapter = pagerAdapter

        // Connect TabLayout with ViewPager
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Feed" // Text for the first tab
                1 -> "Calendar" // Text for the second tab
                else -> ""
            }
        }.attach()

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                Timber.tag(TAG).d("Tab Selected %s", tab.toString())
                // Handle tab selection here, if needed
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // Handle tab unselection here, if needed
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                Timber.tag(TAG).d("Tab Resumed %s", tab.toString() )
            }
        })

        return binding.root
    }

    companion object {
        private const val TAG = "FeedAndCalendarFragment"
    }
}