package com.example.schedulog

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class EventHistoryFragment : Fragment() {
    // Define your data model class
    data class Event(
        val key: String,
        val date: String? = null,
        val description: String? = null,
        val startEndTime: String? = null,
        val title: String? = null,
        val user: String? = null
    ) {
        constructor() : this("", "", "", "", "")
    }

    // Initialize Firebase Database reference
    private val databaseReference = FirebaseDatabase.getInstance().getReference("events")

    // Initialize a list to hold events the user is attending
    private val eventList = mutableListOf<Event>()

    // Initialize the adapter as a property
    private lateinit var adapter: EventHistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_event_history, container, false)
        val recyclerView: RecyclerView = rootView.findViewById(R.id.recyclerViewEventHistory)

        // Set up RecyclerView
        val layoutManager = LinearLayoutManager(context)
        adapter = EventHistoryAdapter(eventList) { event ->
            // Handle the "Attend" event here
            onAttendEvent(event)
        }
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter

        // Load events the current user is attending
        loadAttendingEvents()

        return rootView
    }

    private fun loadAttendingEvents() {
        // Get the current user's UID
        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid

        if (currentUserUid != null) {
            // Reference to the "attending-users" node for the current user
            val attendingUsersRef = FirebaseDatabase.getInstance().reference
                .child("attending-users")
                .child(currentUserUid)

            attendingUsersRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    eventList.clear() // Clear the list

                    for (dataSnapshot in snapshot.children) {
                        val eventKey = dataSnapshot.key

                        // Fetch event details from the "events" node
                        if (eventKey != null) {
                            databaseReference.child(eventKey).addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(eventDataSnapshot: DataSnapshot) {
                                    val event = eventDataSnapshot.getValue(Event::class.java)

                                    if (event != null) {
                                        val eventWithKey = Event(eventKey, event.date, event.description, event.startEndTime, event.title, event.user)
                                        eventList.add(eventWithKey)
                                        adapter.notifyDataSetChanged() // Notify the adapter of the data change
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    // Handle any errors
                                }
                            })
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle any errors
                }
            })
        }
    }

    private fun onAttendEvent(event: Event) {
        // Implement your logic to handle attending events
    }
}
