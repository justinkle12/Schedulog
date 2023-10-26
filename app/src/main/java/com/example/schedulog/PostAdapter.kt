package com.example.schedulog

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.schedulog.databinding.PostItemBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import timber.log.Timber

/* This class is responsible for holding onto an instance of the view and
 * binding the PostItem(post_item.xml) to the RecyclerView(fragment_feed.xml). */
class PostViewHolder (
    private val binding: PostItemBinding
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(postItem: PostItem) {
        binding.postDescription.text = postItem.description
        loadUsername(postItem.user_id)
        Glide.with(binding.root).load(postItem.image_url).into(binding.postImage)
    }

    fun loadUsername(userId: String) {
        val usersRef = FirebaseDatabase.getInstance().getReference("users/$userId")

        usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val username = dataSnapshot.child("username").getValue(String::class.java)
                    val firstName = dataSnapshot.child("first_name").getValue(String::class.java)
                    val lastName = dataSnapshot.child("last_name").getValue(String::class.java)

                    val fullName = "$firstName $lastName"

                    binding.username.text = username
                    binding.userFirstLastName.text = fullName

                    Timber.tag("PostViewHolder | loadUsername").d("%s, %s", username, fullName)
                } else {
                    Timber.tag("PostViewHolder | loadUsername").d("The username does not exist in the database")
                    // The username does not exist in the database
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle database error
            }
        })
    }
}


/* This class is responsible for providing the PostViewHolder instances with a PostItem.
 * Also responsible for the communicating between RecyclerView and data. */
class PostListAdapter(
    private var postItems: List<PostItem>
) : RecyclerView.Adapter<PostViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PostViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = PostItemBinding.inflate(inflater, parent, false)
        return PostViewHolder(binding)
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
}


