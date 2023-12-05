package com.example.schedulog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.schedulog.databinding.FragmentRatePostDialogBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import timber.log.Timber

class RatePostDialogFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentRatePostDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRatePostDialogBinding.inflate(inflater, container, false)
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
            val currentUser = FirebaseAuth.getInstance().currentUser
            val ratedUserId = currentUser?.uid //TODO Replace with the userId with rated user
            val postId = (arguments?.getString("post_id"))

            // Pass the rating and review to your function to send to the database
            sendPostRating(postId.toString(), rating, review)            // TODO fix hard coding. Need to get ratedUserId

            // Set and Get average_rating from database and get average
            val database = FirebaseDatabase.getInstance()
            val everyRatingRef: DatabaseReference = database.getReference("post_ratings/$postId")
            everyRatingRef.addValueEventListener(object : ValueEventListener {
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
                    Timber.tag(AccountProfileFragment.TAG).d(ratings.toString())

                    // Calculate the average rating
                    if (ratings.isNotEmpty()) {

                        val eventsRef = database.reference.child("events")

                        // Now, you can retrieve all events
                        eventsRef.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                    val averageRating = ratings.average()
                                    val averageRatingTextView = String.format("%.2f", averageRating)
                                    val averageRatingRef = database.getReference("events/${postId}/average_rating")
                                    averageRatingRef.setValue(averageRating)
                                    Timber.tag(AccountProfileFragment.TAG).i("loadAccountRating | %s rating: %s", ratedUserId, averageRatingTextView)

                            }
                            override fun onCancelled(error: DatabaseError) {
                                println("Firebase Database Error: $error")
                            }
                        })
                    } else {
                        // Handle the case where there are no ratings
                        Timber.tag(AccountProfileFragment.TAG).i("loadAccountRating | %s has no ratings.", ratedUserId)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle database error
                    Timber.tag(AccountProfileFragment.TAG).e("Database error: %s", databaseError)
                }
            })


            // Close the dialog
            dismiss()
        }

        return view
    }

    private fun sendPostRating(ratedPostId: String, rating: Float, review: String) {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

        if (currentUserId != null && rating in 0.0..5.0) {
            // Get Firebase instance and path to ratings data
            val database = FirebaseDatabase.getInstance()
            val userRatingsRef: DatabaseReference =
                database.getReference("post_ratings/$ratedPostId/$currentUserId")

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
        private const val TAG = "RatePostDialogFragment"
        fun newInstance(postItem: PostItem): RatePostDialogFragment {
            val fragment = RatePostDialogFragment()
            val args = Bundle()
            args.putString("post_id", postItem.eventKey)
            fragment.arguments = args


            return fragment
        }
    }
}