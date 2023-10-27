package com.example.schedulog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.schedulog.databinding.FragmentRateUserDialogBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
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

        // Set a click listener for the submit button
        submitButton.setOnClickListener {
            // Get the rating and review from the input fields
            val rating = ratingBar.rating
            val review = reviewEditText.text.toString()

            // Pass the rating and review to your function to send to the database
            sendUserRating("bryant88", rating, review)            // TODO fix hard coding. Need to get ratedUserId
            /*sendUserRating(ratedUserId, rating, review)*/                 // Original method

            // Close the dialog
            dismiss()
        }

        return view
    }

    private fun sendUserRating(ratedUserId: String, rating: Float, review: String) {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

        if (currentUserId != null && rating in 0.0..5.0) {
                // Get Firebase instance and path to ratings data
                val database = FirebaseDatabase.getInstance()
                val userRatingsRef: DatabaseReference =
                    database.getReference("user_ratings/$ratedUserId/$currentUserId")

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
        fun newInstance(): RateUserDialogFragment {
            return RateUserDialogFragment()
        }


    }
}
