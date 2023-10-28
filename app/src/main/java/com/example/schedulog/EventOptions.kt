import android.app.DatePickerDialog
import android.content.Context
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
import timber.log.Timber
import java.util.Calendar

class EventOptionsFragment : DialogFragment() {

    private var selectedTags: MutableList<String> = mutableListOf()
    private var selectedDateInMillis: Long = 0
    private lateinit var binding: FragmentEventOptionsBinding
    private lateinit var mAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEventOptionsBinding.inflate(inflater, container, false)
        val view = binding.root

        mAuth = FirebaseAuth.getInstance()

        val addTagsButton = binding.addTagsbtn
        addTagsButton.setOnClickListener{
            showTagSelectionDialog()
        }

        val selectDateButton = binding.addDateCalendarButton
        selectDateButton.setOnClickListener{
            context?.let { it1 -> openDatePickerDialog(it1) }
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
                    .push().key ?:""

                // Print the event key for debugging
                Timber.tag(TAG).d("Event Key: %s", eventKey)

                // Create a map to hold event details
                val eventDetails = mapOf(
                    "user" to currentUser.uid,
                    "title" to eventTitle,
                    "description" to eventDescription,
                    "date" to selectedDateInMillis,
                    "startEndTime" to eventStartEndTime,
                    "tags" to selectedTags
                )

                // Store event details in Firebase under the user's UID
                FirebaseDatabase.getInstance().reference
                    .child("events")
                    .child(eventKey)
                    .setValue(eventDetails)

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
        val predefinedTags = listOf("Meetup", "Hackathon", "Expo",                  // Event types
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
                    // selectedTags is added in the createEventButton listener
                }
                .setNegativeButton("Cancel", null)
                .create()
        }?.show()
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
    }
}
