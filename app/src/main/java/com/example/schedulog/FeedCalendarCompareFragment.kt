package com.example.schedulog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import com.example.schedulog.databinding.FragmentFeedCalendarCompareBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
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

        Timber.e(selectedCalendarDate.toString())
        val currentDay = binding.todayTextView
        val simpleDateFormat = SimpleDateFormat("MM/dd/yyyy")
        val dateString = simpleDateFormat.format(selectedCalendarDate)
        currentDay.text = String.format("Selected Date: %s", dateString)


        // Initialize your RecyclerView and set up its adapter and layout manager
        val recyclerView: RecyclerView = binding.recyclerView
        // Set up other views and adapters as needed

        // Set up the close button click listener
        val closeButton: ImageButton = view.findViewById(R.id.closeButton)
        closeButton.setOnClickListener {
            dismiss() // Dismiss the dialog when the close button is clicked
        }

        // You can use selectedCalendarDate here

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