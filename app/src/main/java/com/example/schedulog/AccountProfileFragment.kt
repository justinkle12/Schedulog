import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.schedulog.EntryFragment
import com.example.schedulog.R
import com.example.schedulog.RateUserDialogFragment
import com.example.schedulog.databinding.FragmentAccountProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import timber.log.Timber

class AccountProfileFragment : Fragment() {

    private lateinit var database: FirebaseDatabase
    private lateinit var binding: FragmentAccountProfileBinding
    private lateinit var mAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAccountProfileBinding.inflate(inflater, container, false)
        val view = binding.root

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        // Load and display the current user's account username
        loadAccountUsername()

        // Find the logout button and set its click listener
        val logoutButton = binding.logoutButton
        logoutButton.setOnClickListener {
            // Call the logout function
            logoutUser()
        }

        // Find the delete account button and set its click listener
        val deleteAccountButton = binding.deleteAccountButton
        deleteAccountButton.setOnClickListener {
            // Call the delete account function
            deleteAccount()
        }

        //Bind Write a Review button and set its click listener
        val writeReviewButton = binding.buttonWriteReview
        writeReviewButton.setOnClickListener {
            val dialogFragment = RateUserDialogFragment.newInstance()
            dialogFragment.show(parentFragmentManager, "RateUserDialogFragment")
        }

        return view

    }
    private fun logoutUser() {
        mAuth.signOut()
        // You can navigate to another fragment or perform any other necessary actions here
        // For example, you can navigate back to the login screen
        // Replace R.id.fragmentContainer with the ID of the container where you want
        // to display the login fragment
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, EntryFragment())
        transaction.commit()
    }

    private fun deleteAccount() {
        val user = mAuth.currentUser

        if (user != null) {
            user.delete()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Account deletion successful
                        // You can perform actions like displaying a success message to the user
                        displayMessage("Account deleted successfully.")
                    } else {
                        // Account deletion failed
                        // You can handle the error by displaying an error message to the user
                        displayMessage("Account deletion failed: ${task.exception?.message}")
                    }
                }
        } else {
            // currentUser is null, handle the case when the user is not logged in
            displayMessage("User is not logged in.")
        }
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, EntryFragment())
        transaction.commit()
    }

    private fun displayMessage(message: String) {
        // You can display the message in a TextView or Toast, for example
        // Here's an example using a Toast
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun loadAccountUsername() {
        val currentUser = mAuth.currentUser

        Timber.tag(TAG).d("Current user: %s", currentUser)

        // Check if the user is authenticated
        if (currentUser != null) {
            val userId = currentUser.uid

            Timber.tag(TAG).d("User ID: %s", userId)

            // Reference to the "users" node in the database
            val usersRef: DatabaseReference = database.reference.child("users")

            // Create a ChildEventListener to listen for changes in the "username" field
            val childEventListener = object : ChildEventListener {
                override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                    val userData = dataSnapshot.value as Map<*, *>?
                    val username = userData?.get("username") as String?

                    if (username != null && userData?.get("email") == currentUser.email) {
                        Timber.tag(TAG).d("Username: %s", username)

                        // Set the retrieved username in the binding
                        binding.accountName.text = username
                    }
                }

                override fun onChildChanged(
                    dataSnapshot: DataSnapshot,
                    previousChildName: String?
                ) {
                    // Handle changes if needed
                }

                override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                    // Handle removal if needed
                    Timber.tag(TAG).i("child removed: %s", dataSnapshot)
                }

                override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {
                    // Handle movement if needed
                    Timber.tag(TAG).i("child moved: %s to %s", dataSnapshot, previousChildName) //uncertain
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle errors if needed
                    Timber.tag(TAG).e("Database error: %s", databaseError)
                }
            }

            // Add the ChildEventListener to the "users" node
            usersRef.addChildEventListener(childEventListener)
        }
    }

    companion object {
        private const val TAG = "AccountProfileFragment"
    }


}
