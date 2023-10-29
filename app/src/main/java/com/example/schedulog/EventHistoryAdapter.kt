package com.example.schedulog

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.schedulog.databinding.ListItemEventHistoryBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class EventHistoryAdapter(
    private val eventList: List<EventHistoryFragment.Event>,
) : RecyclerView.Adapter<EventHistoryAdapter.EventViewHolder>() {

    inner class EventViewHolder(private val binding: ListItemEventHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(event: EventHistoryFragment.Event) {
            binding.eventHistoryTitle.text = event.title
            binding.eventHistoryDescription.text = event.description
            binding.eventHistoryDate.text = event.date
            binding.eventHistoryStartEndTime.text = event.startEndTime
            binding.eventUserID.text = event.user

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = ListItemEventHistoryBinding.inflate(
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
