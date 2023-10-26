import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.example.schedulog.databinding.FragmentEventOptionsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import timber.log.Timber

class EventOptionsFragment : DialogFragment() {

    private lateinit var binding: FragmentEventOptionsBinding
    private lateinit var mAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEventOptionsBinding.inflate(inflater, container, false)
        val view = binding.root

        mAuth = FirebaseAuth.getInstance()

        binding.createEventButton.setOnClickListener {
            // Get the user input from EditText fields
            val eventTitle = binding.eventTitleText.text.toString()
            val eventDescription = binding.eventDescription.text.toString()
            val eventDate = binding.eventDate.text.toString()
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
                    "date" to eventDate,
                    "startEndTime" to eventStartEndTime
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

    companion object {
        private const val TAG = "AccountProfileFragment"
    }
}
