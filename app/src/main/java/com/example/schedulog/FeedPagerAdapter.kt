package com.example.schedulog

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class FeedPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2 // Two fragments

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> FeedFragment() // The first fragment
            1 -> FeedCalendarFragment() // The second fragment
            else -> throw IllegalArgumentException("Invalid position")
        }
    }
}
