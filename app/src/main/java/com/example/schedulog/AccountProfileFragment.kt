package com.example.schedulog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.schedulog.databinding.FragmentAccountProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import timber.log.Timber

class AccountProfileFragment : DialogFragment() {

    private lateinit var database: FirebaseDatabase
    private lateinit var binding: FragmentAccountProfileBinding
    private lateinit var mAuth: FirebaseAuth



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAccountProfileBinding.inflate(inflater, container, false)
        val view = binding.root

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        // Load and display the current user's account username
        loadAccountUsername()

        // Load and display the rating for the current user
        loadAccountRating()

        // Find the logout button and set its click listener
        val logoutButton = binding.logoutButtonBtn
        logoutButton.setOnClickListener {
            // Call the logout function
            logoutUser()
        }

        // In the AccountProfileFragment, add a click listener for the "Event History" button
        val eventHistoryButton = binding.eventHistoryButtonBtn
        eventHistoryButton.setOnClickListener {
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragmentContainer, EventHistoryFragment())
            transaction.addToBackStack(null) // Optional: Add the fragment to the back stack
            transaction.commit()
        }

        //Find the Manage account info button and set its click listener
        val accountInfoButton = binding.manageAccButtonBtn
        accountInfoButton.setOnClickListener {
            // Call the logout function
            navigateUserToAccountInfo()
        }

        //Bind Write a Review button and set its click listener
        val writeReviewButton = binding.buttonWriteReview
        writeReviewButton.setOnClickListener {
            val dialogFragment = RateUserDialogFragment.newInstance()
            dialogFragment.show(parentFragmentManager, "RateUserDialogFragment")
        }

        fetchCurrentUserUsername { username ->
            // Use the 'username' in your UI or perform any other actions
            val userNameTextView = binding.username
            userNameTextView.text = username
        }

        return view
    }

    private fun logoutUser() {
        mAuth.signOut()
        // You can navigate to another fragment or perform any other necessary actions here
        // For example, you can navigate back to the login screen
        // Replace R.id.fragmentContainer with the ID of the container where you want to display the login fragment
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, EntryFragment())
        dismiss()
        transaction.commit()
    }

    private fun navigateUserToAccountInfo() {
        // You can navigate to another fragment or perform any other necessary actions here
        // For example, you can navigate back to the login screen
        // Replace R.id.fragmentContainer with the ID of the container where you want to display the login fragment
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, AccountInfoFragment())
        dismiss()
        transaction.commit()
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
                        binding.username.text = username
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

    // Function to fetch the current user's username
    fun fetchCurrentUserUsername(onUsernameFetched: (String) -> Unit) {
        // Get the current user's UID
        val currentUser = FirebaseAuth.getInstance().currentUser
        val uid = currentUser?.uid

        // Create a reference to the Firebase Realtime Database
        val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference()

        // Assuming you have a "users" node and under it, UID and "username" child
        if (uid != null) {
            val userReference: DatabaseReference = databaseReference.child("users").child(uid)
            val usernameReference: DatabaseReference = userReference.child("username")

            // Add a ValueEventListener to fetch the username
            usernameReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val username = dataSnapshot.value.toString()

                    // Pass the username to the callback function
                    onUsernameFetched(username)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle any errors here, if necessary
                }
            })
        }
    }
    private fun loadAccountRating(){
        val ratedUserId = "bryant88" //TODO Replace with the userId with rated user
        val ratingsRef = FirebaseDatabase.getInstance().getReference("user_ratings/$ratedUserId")

        ratingsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val ratings = mutableListOf<Float>()

                // Iterate and add the rating value given to the rated user
                for (ratingSnapshot in dataSnapshot.children) {
                    val rating = ratingSnapshot.child("rating").getValue(Float::class.java)
                    rating?.let {
                        ratings.add(it)
                    }
                }

                // Show list of rating values in logcat
                Timber.tag(TAG).d(ratings.toString())

                // Calculate the average rating
                if (ratings.isNotEmpty()) {
                    val averageRating = ratings.average()
                    val averageRatingTextView = String.format("%.2f", averageRating)
                    binding.textValueRating.text = averageRatingTextView
                    binding.ratedUserBar.rating = averageRating.toFloat()
                    Timber.tag(TAG).i("loadAccountRating | %s rating: %s", ratedUserId, averageRatingTextView)
                } else {
                    // Handle the case where there are no ratings
                    Timber.tag(TAG).i("loadAccountRating | %s has no ratings.", ratedUserId)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle database error
                Timber.tag(TAG).e("Database error: %s", databaseError)
            }
        })
    }

    companion object {
        private const val TAG = "AccountProfileFragment"
    }
}
