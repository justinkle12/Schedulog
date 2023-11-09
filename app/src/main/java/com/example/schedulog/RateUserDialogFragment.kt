package com.example.schedulog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.schedulog.databinding.FragmentRateUserDialogBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import timber.log.Timber

class RateUserDialogFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentRateUserDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRateUserDialogBinding.inflate(inflater, container, false)
        val view = binding.root

        // Initialize variables
        val ratingBar = binding.ratingBar
        val reviewEditText = binding.reviewEditText
        val submitButton = binding.submitButton
        val userId = arguments?.getString("userId") ?: ""


        // Set a click listener for the submit button
        submitButton.setOnClickListener {
            // Get the rating and review from the input fields
            val rating = ratingBar.rating
            val review = reviewEditText.text.toString()

            // Load username then call send rating function
            // Pass the rating and review to your function to send to the database
            loadUsernameFromUserId({username -> sendUserRating(username, rating, review)}, userId)

            // Close the dialog
            dismiss()
        }

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

                    if (profileUsername != null) {
                        callback(profileUsername)
                    }

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

    private fun sendUserRating(ratedUsername: String, rating: Float, review: String) {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

        if (currentUserId != null && rating in 0.0..5.0) {
                // Get Firebase instance and path to ratings data
                val database = FirebaseDatabase.getInstance()
                val userRatingsRef: DatabaseReference =
                    database.getReference("user_ratings/$ratedUsername/$currentUserId")

                // Processing rater's data
                val timestamp = System.currentTimeMillis()
                val ratingData = mapOf(
                    "rating" to rating,
                    "review" to review,
                    "timestamp" to timestamp
                )

                Timber.tag(TAG).d("Sending user rating: %s", ratingData.toString())

                // Write the rating data to the database, which will overwrite an existing rating if it exists
                userRatingsRef.setValue(ratingData)
            }
    }

    companion object {
        private const val TAG = "RateUserDialogFragment"
        fun newInstance(userId: String): RateUserDialogFragment {
            val fragment = RateUserDialogFragment()
            val args = Bundle()
            args.putString("userId", userId)
            fragment.arguments = args
            return fragment
        }
    }
}
