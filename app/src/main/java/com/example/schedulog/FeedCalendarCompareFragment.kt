package com.example.schedulog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.schedulog.databinding.FragmentFeedCalendarCompareBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import timber.log.Timber
import java.text.SimpleDateFormat

class FeedCalendarCompareFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentFeedCalendarCompareBinding
    private var selectedCalendarDate: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFeedCalendarCompareBinding.inflate(inflater, container, false)
        val view = binding.root

        // Retrieve the selected calendar date from arguments
        arguments?.let {
            selectedCalendarDate = it.getLong(ARG_CALENDAR_DATE, 0)
        }

        //initialize
        var userItemList = ArrayList<UserItem>()
        val userListAdapter = UserListAdapter(userItemList)
        val userGrid = binding.recyclerView
        val recyclerView: RecyclerView = binding.recyclerView



        val currentDay = binding.todayTextView
        val simpleDateFormat = SimpleDateFormat("MM/dd/yyyy")
        val dateString = simpleDateFormat.format(selectedCalendarDate)
        currentDay.text = String.format("Selected Date: %s", dateString)


        // Initialize your RecyclerView and set up its adapter and layout manager

        recyclerView.adapter = userListAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val dividerItemDecoration = DividerItemDecoration(
            recyclerView.context,
            DividerItemDecoration.VERTICAL
        )
        recyclerView.addItemDecoration(dividerItemDecoration)

        //setup database ref
        val database = Firebase.database
        val userRef = database.getReference("events")

        val userListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                userItemList.clear() // Clear the list to avoid duplicates

                for (userSnapshot in dataSnapshot.children) {
                    val userItem = userSnapshot.getValue(UserItem::class.java)

                    if (userItem != null && userItem.date % selectedCalendarDate < 86400000) {
                       userItemList.add(userItem)
                        Timber.tag(FeedFragment.TAG).i(userItem.toString())
                    }
                }

                // Update UI with the new postList
                userListAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle database errors here
                Timber.e("%s | Error reading post | %s", FeedFragment.TAG, databaseError.toString())
            }
        }



        // Set up the close button click listener
        val closeButton: ImageButton = view.findViewById(R.id.closeButton)
        closeButton.setOnClickListener {
            dismiss() // Dismiss the dialog when the close button is clicked
        }

        // You can use selectedCalendarDate here
        userRef.addValueEventListener(userListener)
        return view
    }

    companion object {
        private const val TAG = "FeedCalendarCompareFragment"

        private const val ARG_CALENDAR_DATE = "arg_calendar_date"

        fun newInstance(calendarDate: Long): FeedCalendarCompareFragment {
            return FeedCalendarCompareFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_CALENDAR_DATE, calendarDate)
                }
            }
        }
    }
}