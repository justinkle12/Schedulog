package com.example.schedulog

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
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
    private val binding: PostItemBinding,
    private val context: Context,
) : RecyclerView.ViewHolder(binding.root) {

    val shareButton = binding.platformShareButtonBtn
    fun bind(postItem: PostItem) {
        binding.postDescription.text = postItem.description
        binding.postTitle.text = postItem.title
        loadUsername(postItem.user)
        displayTags(postItem.tags)
        Glide.with(binding.root).load(postItem.imageURL).into(binding.postImage)
    }

    private fun loadUsername(userId: String) {
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

    private fun displayTags(selectedTags: MutableList<String>){
        binding.tagsContainer.removeAllViews()      // clear child views(tags) to prevent duplicates
        for (tag in selectedTags) {
            val tagTextView = TextView(context)
            tagTextView.text = tag

            // Set margin or padding to create spacing
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams.leftMargin = 16

            tagTextView.layoutParams = layoutParams
            tagTextView.textSize = 12F
            tagTextView.setBackgroundResource(R.drawable.rectangle_tag_light_blue)   // Optional: Set a background drawable
            tagTextView.setPadding(8, 4, 8, 4)                  // Optional: Set padding
            tagTextView.setTextColor(ContextCompat.getColor(context, R.color.black)) // Optional: Set text color

            // Add click listener to handle tag selection or other actions for future implementation
            tagTextView.setOnClickListener {
                // Handle tag selection or other actions
            }

            // Add the TextView to the layout
            binding.tagsContainer.addView(tagTextView)
        }
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
        val context=  parent.context
        val inflater = LayoutInflater.from(parent.context)
        val binding = PostItemBinding.inflate(inflater, parent, false)
        return PostViewHolder(binding, context)
    }

    override fun getItemCount(): Int {
        return postItems.size
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        Timber.tag("PostListAdapter").i(postItems.toString())
        val post = postItems[position]

        holder.shareButton.setOnClickListener{
            sharePost(it, position)
        }

        holder.bind(post)
    }
    fun setFilteredList(filteredList: ArrayList<PostItem>) {
        this.postItems = filteredList
        notifyDataSetChanged()
    }

    fun sharePost(view: View, position: Int) {
        val post = postItems[position] // Get the post data for the clicked item

        val postMessage = "Check out this awesome post: ${post.title} ${post.description}"

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, postMessage)
        }

        val context = view.context
        context.startActivity(Intent.createChooser(sendIntent, "Share via"))
    }

}


