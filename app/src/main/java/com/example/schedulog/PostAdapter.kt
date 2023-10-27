package com.example.schedulog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.schedulog.databinding.PostItemBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import timber.log.Timber


/* This class is responsible for holding onto an instance of the view and
 * binding the PostItem(post_item.xml) to the RecyclerView(fragment_feed.xml). */
class PostViewHolder (
    private val binding: PostItemBinding,
    private var fragmentManager: FragmentManager
) : RecyclerView.ViewHolder(binding.root){
    fun bind(postItem: PostItem){

        val writeReviewButton = binding.buttonWriteReview

        writeReviewButton.setOnClickListener {

            val dialogFragment = RatePostDialogFragment.newInstance(postItem)

            dialogFragment.show(fragmentManager, "RatePostDialogFragment")

        }
        binding.postDescription.text = postItem.description
        binding.ratedUserBar.rating = postItem.average_rating
        binding.textValueRating.text = postItem.average_rating.toString()
        val myRatingRef = FirebaseDatabase.getInstance().getReference("post_ratings/post_id_${postItem.post_id + 1}/${FirebaseAuth.getInstance().currentUser?.uid}").child("rating")
        myRatingRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val rating = dataSnapshot.value.toString()
                binding.textValueRating2.text = rating
                // Pass the username to the callback function
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
        Glide.with(binding.root).load(postItem.image_url).into(binding.postImage)    // not sure if binding.root is correct
    }
}


/* This class is responsible for providing the PostViewHolder instances with a PostItem.
 * Also responsible for the communicating between RecyclerView and data. */
class PostListAdapter(
    private var postItems: List<PostItem>,
    private var fragmentManager: FragmentManager
) : RecyclerView.Adapter<PostViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PostViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = PostItemBinding.inflate(inflater, parent, false)

        return PostViewHolder(binding,fragmentManager)
    }

    override fun getItemCount(): Int {
        return postItems.size
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        Timber.tag("PostListAdapter").i(postItems.toString())
        val post = postItems[position]


        holder.bind(post)
    }
    fun setFilteredList(filteredList: ArrayList<PostItem>) {
        this.postItems = filteredList
        notifyDataSetChanged()
    }

    private fun loadAccountRating(binding: PostItemBinding, id : Int ){
        val currentUser = FirebaseAuth.getInstance().currentUser
        val ratedUserId = currentUser?.uid //TODO Replace with the userId with rated user
        val ratingsRef = FirebaseDatabase.getInstance().getReference("post_ratings/post_id_$id")


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
                Timber.tag(AccountProfileFragment.TAG).d(ratings.toString())

                // Calculate the average rating
                if (ratings.isNotEmpty()) {
                    val averageRating = ratings.average()
                    val averageRatingTextView = String.format("%.2f", averageRating)
                    binding.textValueRating.text = averageRatingTextView
                    binding.ratedUserBar.rating = averageRating.toFloat()
                    Timber.tag(AccountProfileFragment.TAG).i("loadAccountRating | %s rating: %s", ratedUserId, averageRatingTextView)
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
    }

}


