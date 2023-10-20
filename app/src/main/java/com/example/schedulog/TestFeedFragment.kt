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
import timber.log.Timber

class TestFeedFragment : Fragment() {
    // Define your data model class
    data class Event(
        val key: String,
        val date: String? = null,
        val description: String? = null,
        val startEndTime: String? = null,
        val title: String? = null,
        val user: String? = null
    ) {
        // Default no-argument constructor
        constructor() : this("", "", "", "", "")
    }

    // Initialize Firebase Database reference
    private val databaseReference = FirebaseDatabase.getInstance().getReference("events")

    // Initialize a list to hold events
    private val eventList = mutableListOf<Event>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_test_feed, container, false)
        val recyclerView: RecyclerView = rootView.findViewById(R.id.recyclerView)

        // Set up RecyclerView
        val layoutManager = LinearLayoutManager(context)
        val adapter = EventAdapter(eventList) { event ->
            // Handle the "Attend" event here
            onAttendEvent(event)
        }
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter

        // Set up a ValueEventListener to fetch data from Firebase
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                eventList.clear() // Clear the list

                for (dataSnapshot in snapshot.children) {
                    val eventKey = dataSnapshot.key
                    val event = dataSnapshot.getValue(Event::class.java)

                    // Check if the eventKey and event data are not null
                    if (eventKey != null && event != null) {
                        val eventWithKey = Event(eventKey, event.date, event.description, event.startEndTime, event.title, event.user)
                        eventList.add(eventWithKey)
                    }
                }

                adapter.notifyDataSetChanged() // Notify the adapter of the data change
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle any errors
            }
        })


        return rootView
    }

    private fun onAttendEvent(event: Event) {
        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid

        if (currentUserUid != null) {
            val eventRef = FirebaseDatabase.getInstance().reference.child("events").child(event.key)
            Timber.d("Current User UID: %s", currentUserUid)
            val attendingUsersRef = eventRef.child("attending-users").child(currentUserUid)

            attendingUsersRef.setValue(true)
        }
    }




}
