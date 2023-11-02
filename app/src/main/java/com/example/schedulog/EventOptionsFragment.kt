package com.example.schedulog

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.schedulog.databinding.FragmentEventOptionsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import timber.log.Timber
import java.util.Calendar

class EventOptionsFragment : DialogFragment() {

    private var selectedTags: MutableList<String> = mutableListOf()
    private lateinit var binding: FragmentEventOptionsBinding
    private lateinit var mAuth: FirebaseAuth
    private val storageReference = FirebaseStorage.getInstance().reference
    private var selectedImageUri: Uri? = null
    private var selectedDateInMillis: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEventOptionsBinding.inflate(inflater, container, false)
        val view = binding.root

        mAuth = FirebaseAuth.getInstance()

        val addTagsButton = binding.addTagsbtn
        addTagsButton.setOnClickListener {
            showTagSelectionDialog()
        }

        val selectDateButton = binding.addDateCalendarButton
        selectDateButton.setOnClickListener{
            context?.let { it1 -> openDatePickerDialog(it1) }
        }

        binding.uploadImageButton.setOnClickListener {
            openImagePicker()
        }

        binding.createEventButton.setOnClickListener {
            // Get the user input from EditText fields
            val eventTitle = binding.eventTitleText.text.toString()
            val eventDescription = binding.eventDescription.text.toString()
            val eventStartEndTime = binding.eventStartEndTime.text.toString()

            // Ensure the user is authenticated
            val currentUser = mAuth.currentUser
            if (currentUser != null) {
                // Print the UID for debugging
                Timber.tag(TAG).d("Current User UID: %s", currentUser.uid)

                // Create a unique key for the event
                val eventKey = FirebaseDatabase.getInstance().reference
                    .child("events")
                    .push().key ?: ""

                // Print the event key for debugging
                Timber.tag(TAG).d("Event Key: %s", eventKey)

                // Create a mutable map to hold event details
                val eventDetails = mutableMapOf(
                    "user" to currentUser.uid,
                    "title" to eventTitle,
                    "description" to eventDescription,
                    "date" to selectedDateInMillis,
                    "startEndTime" to eventStartEndTime,
                    "tags" to selectedTags,
                    "eventKey" to eventKey,
                    "imageURL" to "" // Initialize with an empty string
                )

                /*// The user creating the event also has to attend to event
                val eventRef = FirebaseDatabase.getInstance().reference.child("events").child(eventKey)
                Timber.d("user created an event and is attending it")
                val attendingUsersRef = eventRef.child("attending-users").child(currentUser.uid)
                attendingUsersRef.setValue(true)*/

                if (selectedImageUri != null) {
                    // Define the path in Firebase Storage where the image should be stored (e.g., "images/users/UID")
                    val imagePath = "images/users/${currentUser.uid}/${eventKey}.jpg"

                    // Upload the selected image to Firebase Storage
                    val imageRef = storageReference.child(imagePath)
                    selectedImageUri?.let { uri ->
                        // Perform operations with uri (which is non-null here)
                        imageRef.putFile(uri)
                            .addOnSuccessListener { taskSnapshot ->
                                // Image upload successful; get the download URL
                                imageRef.downloadUrl.addOnSuccessListener { uri ->
                                    // Update the event details with the image URL
                                    eventDetails["imageURL"] = uri.toString()

                                    // Store event details in Firebase Realtime Database under the user's UID
                                    FirebaseDatabase.getInstance().reference
                                        .child("events")
                                        .child(eventKey)
                                        .setValue(eventDetails)

                                    // User has to attend their own event. They are the host.
                                    // Have to add this to both if and else statement for some reason.
                                    FirebaseDatabase.getInstance().reference
                                        .child("events")
                                        .child(eventKey)
                                        .child("attending-users")
                                        .child(currentUser.uid)
                                        .setValue(true)

                                    dismiss()
                                }
                            }
                            .addOnFailureListener { e ->
                                // Handle image upload failure
                                Toast.makeText(
                                    requireContext(),
                                    "Image upload failed: $e",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                } else {
                    // No image selected; proceed without uploading an image
                    FirebaseDatabase.getInstance().reference
                        .child("events")
                        .child(eventKey)
                        .setValue(eventDetails)

                    // User has to attend their own event. They are the host.
                    // Have to add this to both if and else statement for some reason.
                    FirebaseDatabase.getInstance().reference
                        .child("events")
                        .child(eventKey)
                        .child("attending-users")
                        .child(currentUser.uid)
                        .setValue(true)
                }

                // Optionally, provide feedback to the user
                // You can show a toast message or navigate to another screen
                Toast.makeText(
                    requireContext(),
                    "Event successfully created!!!",
                    Toast.LENGTH_SHORT
                ).show()
                dismiss()
            }
        }

        return view
    }

    private fun showTagSelectionDialog() {
        // Can put this list in the database for future maintainability
        val predefinedTags = listOf(
            "Meetup", "Hackathon", "Expo",                  // Event types
            "Technology", "Finance", "Sports", "Arts and Culture", "Food and Beverage", // Topics
            "English", "Spanish", "French", "Multilingual",                             // Languages
            "Students", "Developers", "Professionals", "Parents",                       // Audience
            "Panel Discussion", "Q&A", "Hands-On",                                      // Format
            "Charity", "Sustainability", "Mental Health",                               // Social Causes
            "18+", "21+", "All Ages"                                                    // Age Restrictions
        )

        val checkedItems = BooleanArray(predefinedTags.size)

        context?.let {
            AlertDialog.Builder(it)
                .setTitle("Select Tags")
                .setMultiChoiceItems(
                    predefinedTags.toTypedArray(),
                    checkedItems
                ) { _, which, isChecked ->
                    checkedItems[which] = isChecked
                }
                .setPositiveButton("OK") { _, _ ->
                    selectedTags = predefinedTags
                        .filterIndexed { index, _ -> checkedItems[index] }
                        .toMutableList()
                    Timber.tag(TAG).d("Selected Tags | %s", selectedTags)
                    // selectedTags are added in the createEventButton listener
                }
                .setNegativeButton("Cancel", null)
                .create()
        }?.show()
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data?.data
            // Display the selected image to the user if needed
        }
    }

    fun openDatePickerDialog(context: Context) {

        // Initialize Variables
        val monthNames = arrayOf(
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        )

        // Initialize Calendar
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            context,
            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.set(year, month, dayOfMonth, 0, 0, 1)
                selectedDateInMillis = selectedCalendar.timeInMillis

                // You can use selectedDateInMillis as needed
                // For example, display it in a TextView
                // Get the year, month, and day
                val selectedYear = selectedCalendar.get(Calendar.YEAR)
                val selectedMonth = selectedCalendar.get(Calendar.MONTH)
                val selectedDay = selectedCalendar.get(Calendar.DAY_OF_MONTH)
                val stringSelectedMonth = monthNames[selectedMonth]

                val date = "$stringSelectedMonth $selectedDay, $selectedYear"
                binding.eventDate.setText(date)

            },
            currentYear,
            currentMonth,
            currentDay
        )
        datePickerDialog.show()
    }

    companion object {
        private const val TAG = "AccountProfileFragment"
        private const val PICK_IMAGE_REQUEST_CODE = 123
    }
}