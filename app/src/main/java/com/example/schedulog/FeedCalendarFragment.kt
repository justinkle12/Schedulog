package com.example.schedulog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.schedulog.databinding.FragmentFeedCalendarBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import timber.log.Timber

class FeedCalendarFragment : Fragment() {
    private var _binding: FragmentFeedCalendarBinding? = null
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
            FragmentFeedCalendarBinding.inflate(inflater, container, false)
        binding.calendarGrid.layoutManager = GridLayoutManager(context, 1)

        // Initialize variables
        val calendarDataList = mutableListOf<CalendarData>()

        // Populate the list with data for each calendar view item **This is only for testing**
        calendarDataList.add(CalendarData(1523516143569, 1))
        calendarDataList.add(CalendarData(1623186143769, 3))
        calendarDataList.add(CalendarData(1624186843769, 3))
        calendarDataList.add(CalendarData(1590316143569, 2))
        calendarDataList.add(CalendarData(1696635353091, 1))

        // Initialize remaining variables
        val calendarListAdapter = CalendarAdapter(calendarDataList)
        val recyclerView = binding.calendarGrid

        // Set RecyclerView Calendar adapter
        recyclerView.adapter = calendarListAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val dividerItemDecoration = DividerItemDecoration(
            recyclerView.context,
            DividerItemDecoration.VERTICAL
        )
        recyclerView.addItemDecoration(dividerItemDecoration)

        // Initialize Firebase reference
        val database = Firebase.database
        val postsRef = database.getReference("")

        //TODO get calendar data from firebase


        return binding.root
    }
}