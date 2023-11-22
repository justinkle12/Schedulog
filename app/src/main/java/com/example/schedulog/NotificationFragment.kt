package com.example.schedulog

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.Calendar
import java.util.Date

class NotificationFragment : Fragment() {

    data class Event(
        val key: String,
        val date: Long = 0,
        val description: String? = null,
        val startEndTime: String? = null,
        val title: String? = null,
        val user: String? = null,
        val isAttending: Boolean = false
    ) {
        constructor() : this("", 0, "", "", "")
    }

    private val databaseReference = FirebaseDatabase.getInstance().getReference("events")
    private val eventList = mutableListOf<Event>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_notification, container, false)

        // Access the title TextView
        val titleTextView: TextView = view.findViewById(R.id.textTitle)

        // Access the notification content container
        val notificationContent: LinearLayout = view.findViewById(R.id.notificationContent)

        // Load events for today
        loadEventsForToday()

        return view
    }

    private fun loadEventsForToday() {
        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid

        if (currentUserUid != null) {
            Log.d("Event", "Current User UID: $currentUserUid") // Added log for current UID

            val eventsRef = FirebaseDatabase.getInstance().reference.child("events")

            val today = Calendar.getInstance()

            eventsRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(eventsSnapshot: DataSnapshot) {
                    eventList.clear() // Clear the list

                    for (eventDataSnapshot in eventsSnapshot.children) {
                        val eventKey = eventDataSnapshot.key

                        // Reference to the "attending-users" node for the current user within the event
                        if (eventKey != null) {
                            val attendingUsersRef = FirebaseDatabase.getInstance().reference
                                .child("events").child(eventKey)
                                .child("attending-users")
                                .child(currentUserUid)

                            attendingUsersRef.addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    val isAttending = snapshot.getValue(Boolean::class.java) ?: false

                                    // Retrieve the Event object corresponding to the eventKey
                                    val event = eventDataSnapshot.getValue(Event::class.java)

                                    if (event != null) {
                                        val eventDate = Calendar.getInstance()
                                        eventDate.timeInMillis = event.date

                                        Log.d(
                                            "Event",
                                            "Event: ${event.title}, Date: ${event.date}, Today: ${
                                                today.get(Calendar.YEAR)
                                            }-${today.get(Calendar.MONTH) + 1}-${today.get(Calendar.DAY_OF_MONTH)}, Event Date: ${
                                                eventDate.get(Calendar.YEAR)
                                            }-${eventDate.get(Calendar.MONTH) + 1}-${eventDate.get(Calendar.DAY_OF_MONTH)}, isAttending: $isAttending"
                                        )

                                        if (isAttending && isSameDay(today, eventDate)) {
                                            eventList.add(event)
                                            Log.d("Event", "Number of events for today: ${eventList.size}")
                                            displayEventsForToday()
                                        }
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    // Handle any errors
                                    Log.e("Event", "Error checking attendance: $error")
                                }
                            })
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle any errors
                    Log.e("Event", "Error loading events: $error")
                }
            })
        } else {
            Log.e("Event", "Current User UID is null")
        }

    }




    /*
        if (eventKey != null) {
            val attendingUsersRef = FirebaseDatabase.getInstance().reference
                .child("events").child(eventKey)
                .child("attending-users")
                .child(currentUserUid)

            attendingUsersRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // Check if the user is attending this event
                    val isAttending = snapshot.getValue(Boolean::class.java) ?: false

                    if (isAttending) {
                        val event = eventDataSnapshot.getValue(EventHistoryFragment.Event::class.java)

        private fun isUserAttendingEvent(event: Event, userId: String, callback: (Boolean) -> Unit) {
            val attendingUsersRef = FirebaseDatabase.getInstance().reference
                .child("events").child(event.key)
                .child("attending-users")
                .child(userId)

            attendingUsersRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // Check if the user is attending this event
                    val isAttending = snapshot.getValue(Boolean::class.java) ?: false
                    callback(isAttending)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle any errors
                    callback(false) // Default to false in case of an error
                }
            })
        }

     */



    private fun displayEventsForToday() {
        val today = Calendar.getInstance()
        today.time = Date()

        val eventsForToday = eventList.filter { event ->
            // Filter events for today
            val eventDate = Calendar.getInstance()
            eventDate.timeInMillis = event.date
            isSameDay(today, eventDate)
        }

        // Log the number of events for today
        Log.d("Event", "Number of events for today (display): ${eventsForToday.size}")

        val titleTextView: TextView = requireView().findViewById(R.id.textTitle)
        val notificationContent: LinearLayout = requireView().findViewById(R.id.notificationContent)

        if (eventsForToday.isNotEmpty()) {
            // User has events for today
            titleTextView.text = "You have an event today"

            // Iterate through events and display messages
            for (event in eventsForToday) {
                val message = "Event: ${event.title}, Time: ${event.startEndTime}"

                // Create a TextView for each event message
                val eventTextView = TextView(requireContext())
                eventTextView.text = message
                eventTextView.textSize = 16f
                eventTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))

                // Add the event message to the notification content container
                notificationContent.addView(eventTextView)
            }
        } else {
            // User has no events for today
            titleTextView.text = "No events today"
        }
    }


    // Function to check if two Calendar instances represent the same day
    private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH)
    }


    private fun isSameDay(cal: Calendar, timestamp: Long): Boolean {
        val eventDate = Calendar.getInstance(cal.timeZone) // Use the same time zone as cal
        eventDate.timeInMillis = timestamp

        return isSameDay(cal, eventDate)
    }



}
