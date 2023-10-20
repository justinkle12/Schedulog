package com.example.schedulog

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.schedulog.databinding.ListItemEventBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class EventAdapter(
    private val eventList: List<TestFeedFragment.Event>,
    private val onAttendEventClick: (TestFeedFragment.Event) -> Unit
) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    class EventViewHolder(private val binding: ListItemEventBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private fun onAttendEventClick(event: TestFeedFragment.Event) {
            val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid
            val eventKey = event.key // Get the event's key

            if (currentUserUid != null && eventKey != null) {
                val eventRef = FirebaseDatabase.getInstance().reference.child("events").child(eventKey)
                val attendingUsersRef = eventRef.child("attending-users").child(currentUserUid)

                attendingUsersRef.setValue(true)
            }
        }

        fun bind(event: TestFeedFragment.Event) {
            binding.eventTitle.text = event.title
            binding.eventDescription.text = event.description
            binding.eventDate.text = event.date
            binding.eventStartEndTime.text = event.startEndTime
            binding.eventUserID.text = event.user
            binding.attendEventButton.setOnClickListener {
                onAttendEventClick(event)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = ListItemEventBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return EventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = eventList[position]
        holder.bind(event)
    }

    override fun getItemCount(): Int {
        return eventList.size
    }


}
