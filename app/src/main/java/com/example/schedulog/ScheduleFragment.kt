package com.example.schedulog

import Event
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CalendarView
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ScheduleFragment : Fragment() {

    private lateinit var calendarView: CalendarView
    private lateinit var editText: EditText
    private lateinit var eventDescriptionTextView: TextView
    private var stringDateSelected: String? = null
    private lateinit var databaseReference: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_schedule, container, false)

        calendarView = view.findViewById(R.id.calendarView)
        editText = view.findViewById(R.id.editText)
        eventDescriptionTextView = view.findViewById(R.id.eventDescriptionTextView)

        databaseReference = FirebaseDatabase.getInstance().getReference("Calendar")

        val saveButton = view.findViewById<Button>(R.id.buttonSave)
        val deleteButton = view.findViewById<Button>(R.id.buttonDelete)

        // Add a button click listener to save events
        saveButton.setOnClickListener {
            buttonSaveEvent()
        }

        // Add a button click listener to delete events
        deleteButton.setOnClickListener {
            stringDateSelected?.let { deleteEvent(it) }
        }

        // Update event description when a date is selected
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            stringDateSelected = "$year${month + 1}$dayOfMonth"
            calendarClicked()
        }

        return view
    }

    private fun calendarClicked() {
        databaseReference.child(stringDateSelected ?: "").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val value = snapshot.getValue(Event::class.java)
                if (value != null) {
                    eventDescriptionTextView.text = value.description
                    editText.text.clear() // Clear the EditText box
                } else {
                    eventDescriptionTextView.text = ""
                    editText.text.clear()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle onCancelled if needed
            }
        })
    }

    // Save or update an event
    private fun buttonSaveEvent() {
        val eventDescription = editText.text.toString()
        if (stringDateSelected != null && eventDescription.isNotBlank()) {
            val event = Event(stringDateSelected!!, eventDescription)
            databaseReference.child(stringDateSelected!!).setValue(event)
            editText.text.clear()
            eventDescriptionTextView.text = eventDescription // Update event description immediately
        }
    }

    // Delete an event
    private fun deleteEvent(date: String) {
        databaseReference.child(date).removeValue()
        editText.text.clear()
        eventDescriptionTextView.text = "" // Clear the event description immediately
    }
}
