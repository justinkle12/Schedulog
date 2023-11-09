package com.example.schedulog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.schedulog.databinding.FragmentUserProfileBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import timber.log.Timber

class UserProfileFragment : DialogFragment() {

    private lateinit var database: FirebaseDatabase
    private lateinit var binding: FragmentUserProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserProfileBinding.inflate(inflater, container, false)
        val view = binding.root

        // Initialize Firebase
        database = FirebaseDatabase.getInstance()

        // Load and display the current user's account username then load accountRating using the username
        val userId = arguments?.getString("userId") ?: ""
        loadUsernameFromUserId({username -> loadAccountRating(username)}, userId)

        /*
        // **Disabled feature because it only loads event history for current user**
        // In the AccountProfileFragment, add a click listener for the "Event History" button
        val eventHistoryButton = binding.eventHistoryButtonBtn
        eventHistoryButton.setOnClickListener {
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragmentContainer, EventHistoryFragment())
            transaction.addToBackStack(null) // Optional: Add the fragment to the back stack
            transaction.commit()
        }
        */

        //Bind Write a Review button and set its click listener
        val writeReviewButton = binding.buttonWriteReview
        writeReviewButton.setOnClickListener {
            val dialogFragment = RateUserDialogFragment.newInstance(userId)
            dialogFragment.show(parentFragmentManager, "RateUserDialogFragment")
        }

        fetchCurrentUserUsername ({username -> binding.username.text = username}, userId)

        return view
    }

    private fun loadUsernameFromUserId(callback: (String) -> Unit, userId: String) {

        Timber.tag(TAG).d("loading username from profileId: %s", userId)
        // Reference to the "users" node in the database
        val userRef = FirebaseDatabase.getInstance().getReference("users/$userId")
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val profileUsername = dataSnapshot.child("username").getValue(String::class.java)
                    val firstName = dataSnapshot.child("first_name").getValue(String::class.java)
                    val lastName = dataSnapshot.child("last_name").getValue(String::class.java)

                    val fullName = "$firstName $lastName"

                    if (profileUsername != null) {
                        binding.username.text = profileUsername
                        binding.userFirstLastName.text = fullName

                        callback(profileUsername)
                    }

                    Timber.tag(TAG).i("loadUsername %s, %s", profileUsername, fullName)
                } else {
                    Timber.tag(TAG).d("The username does not exist in the database")
                    // The username does not exist in the database
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle database error
            }
        })
    }

    // Function to fetch the current user's username
    fun fetchCurrentUserUsername(onUsernameFetched: (String) -> Unit, userId: String) {

        // Create a reference to the Firebase Realtime Database
        val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference()

        // Assuming you have a "users" node and under it, UID and "username" child
        val userReference: DatabaseReference = databaseReference.child("users").child(userId)
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

    private fun loadAccountRating(username: String){
        val ratingsRef = FirebaseDatabase.getInstance().getReference("user_ratings/$username")
        Timber.tag(TAG).d(ratingsRef.toString())

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
                Timber.tag(TAG).d("Ratings List | %s", ratings.toString())

                // Calculate the average rating
                if (ratings.isNotEmpty()) {
                    val averageRating = ratings.average()
                    val averageRatingTextView = String.format("%.2f", averageRating)
                    binding.textValueRating.text = averageRatingTextView
                    binding.ratedUserBar.rating = averageRating.toFloat()
                    Timber.tag(TAG).i("loadAccountRating | %s rating: %s", username, averageRatingTextView)
                } else {
                    // Handle the case where there are no ratings
                    Timber.tag(TAG).i("loadAccountRating | %s has no ratings.", username)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle database error
                Timber.tag(TAG).e("Database error: %s", databaseError)
            }
        })
    }

    companion object {
        private const val TAG = "UserProfileFragment"

        fun newInstance(userId: String): UserProfileFragment {
            val fragment = UserProfileFragment()
            val args = Bundle()
            args.putString("userId", userId)
            fragment.arguments = args
            return fragment
        }
    }
}
