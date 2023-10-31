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
import java.util.Calendar

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
        val postItemList = ArrayList<PostItem>()
        val postListAdapter = PostListAdapter(postItemList)
        val recyclerView = binding.calendarGrid

        // Initialize Calendar
        val calendarView = binding.calendarView
        val todayInMillis = System.currentTimeMillis()
        calendarView.date = todayInMillis

        // Set RecyclerView for feedCalendar adapter
        recyclerView.adapter = postListAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val dividerItemDecoration = DividerItemDecoration(
            recyclerView.context,
            DividerItemDecoration.VERTICAL
        )
        recyclerView.addItemDecoration(dividerItemDecoration)

        // Initialize Firebase reference
        val database = Firebase.database
        val postsRef = database.getReference("events")

        // Listen for updates on selecting calendar dates
        calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            // When the user selects a different date, this callback is triggered

            // Create a Calendar instance and set it to the selected date
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth, 0, 0 ,0)

            // Get the selected date in milliseconds
            val selectedDateInMillis = calendar.timeInMillis

            // Update the CalendarView to display the selected date
            calendarView.date = selectedDateInMillis

            // Calculate the start and end of today (in milliseconds)
            val startOfDate = calendarView.date
            val endOfDate = calendarView.date + (24 * 60 * 60 * 1000) - 1 // Adding milliseconds for one day

            val query = postsRef.orderByChild("date")
                .startAt(startOfDate.toDouble())
                .endAt(endOfDate.toDouble())

            postItemList.clear() // Clear the list to avoid duplicates

            // Perform the query to find posts with the selected day's timestamp
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (postSnapshot in dataSnapshot.children) {

                        val postKey = postSnapshot.key
                        val postItem = postSnapshot.getValue(PostItem::class.java)

                        if (postItem != null) {
                            postItemList.add(postItem)
                            Timber.tag(TAG).i(postItem.toString())
                        }
                    }

                    postItemList.reverse()

                    // Update UI with the new postList
                    postListAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle database errors here
                    Timber.e("%s | Error reading post | %s", TAG, databaseError.toString())
                }

            })
        }


        // Listen for updates on social postings
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                postItemList.clear() // Clear the list to avoid duplicates

                for (postSnapshot in dataSnapshot.children) {
                    val postItem = postSnapshot.getValue(PostItem::class.java)

                    val startOfDate = calendarView.date
                    val endOfDate = calendarView.date + (24 * 60 * 60 * 1000) - 1 // Adding milliseconds for one day
                    if (postItem != null) {
                        if (postItem.date in (startOfDate + 1) until endOfDate) {
                            postItemList.add(postItem)
                            Timber.tag(TAG).i(postItem.toString())
                        }
                    }
                }

                postItemList.reverse()

                // Update UI with the new postList
                postListAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle database errors here
                Timber.e("%s | Error reading post | %s", TAG, databaseError.toString())
            }
        }

        postsRef.addValueEventListener(postListener)

        return binding.root
    }

    companion object {
        private const val TAG = "FeedCalendarFragment"
    }
}