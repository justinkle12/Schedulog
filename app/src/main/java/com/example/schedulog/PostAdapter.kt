package com.example.schedulog

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.schedulog.databinding.PostItemBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/* This class is responsible for holding onto an instance of the view and
 * binding the PostItem(post_item.xml) to the RecyclerView(fragment_feed.xml). */
class PostViewHolder (
    private val binding: PostItemBinding,
    private val context: Context,
) : RecyclerView.ViewHolder(binding.root) {

    // Initialize buttons for logic
    val shareButton = binding.platformShareButtonBtn
    val attendEventButton = binding.attendEventButtonBtn
    val cancelAttendingEventButton = binding.attendingEventButtonBtn
    val userPictureButton = binding.userPictureBtn
    val userFullnameButton = binding.userFullnameBtn
    val usernameButton = binding.usernameBtn

    fun bind(postItem: PostItem) {
        binding.postDescription.text = postItem.description
        binding.postTitle.text = postItem.title
        loadUsername(postItem.user)
        displayTags(postItem.tags)
        binding.textDate.text = millisecondDateToFormattedDate(postItem.date)
        binding.textTime.text = postItem.startEndTime
        isAttendingButton(postItem.eventKey)
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

    private fun millisecondDateToFormattedDate(milliseconds: Long): String {
        val dateFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
        val date = Date(milliseconds)
        return dateFormat.format(date)
    }

    private fun isAttendingButton(eventKey: String) {
        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid

        if (currentUserUid != null) {
            val eventRef = FirebaseDatabase.getInstance().reference.child("events").child(eventKey)
            Timber.d("Current User UID: %s", currentUserUid)
            val attendingUserRef = eventRef.child("attending-users").child(currentUserUid)

            attendingUserRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Retrieve the value
                        val value = dataSnapshot.value

                        if (value == true){
                            binding.attendingEventButtonText.visibility = View.VISIBLE
                            binding.attendingEventButtonBtn.visibility = View.VISIBLE
                        }
                        else {
                            binding.attendingEventButtonText.visibility = View.GONE
                            binding.attendingEventButtonBtn.visibility = View.GONE
                        }
                    } else {
                        binding.attendingEventButtonText.visibility = View.GONE
                        binding.attendingEventButtonBtn.visibility = View.GONE
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle any errors here
                }
            })


        }
    }
}


/* This class is responsible for providing the PostViewHolder instances with a PostItem.
 * Also responsible for the communicating between RecyclerView and data. */
class PostListAdapter(
    private val context: Context,
    private var postItems: List<PostItem>
) : RecyclerView.Adapter<PostViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PostViewHolder {
        val context = parent.context
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
        val postKey = post.eventKey

        holder.shareButton.setOnClickListener{
            sharePost(it, post)
        }

        holder.attendEventButton.setOnClickListener{
            onAttendEvent(it, postKey)
        }

        holder.cancelAttendingEventButton.setOnClickListener {
            cancelAttendedEvent(it, postKey)
        }

        // Three ways a user can go to another profile
        holder.userPictureButton.setOnClickListener {
            val userProfileFragment = UserProfileFragment.newInstance(post.user)
            userProfileFragment.show((context as AppCompatActivity).supportFragmentManager, "UserProfileFragment")
        }

        holder.userFullnameButton.setOnClickListener {
            val userProfileFragment = UserProfileFragment.newInstance(post.user)
            userProfileFragment.show((context as AppCompatActivity).supportFragmentManager, "UserProfileFragment")
        }

        holder.usernameButton.setOnClickListener {
            val userProfileFragment = UserProfileFragment.newInstance(post.user)
            userProfileFragment.show((context as AppCompatActivity).supportFragmentManager, "UserProfileFragment")
        }

        holder.bind(post)
    }
    fun setFilteredList(filteredList: ArrayList<PostItem>) {
        this.postItems = filteredList
        notifyDataSetChanged()
    }

    /* Note: Posting the reddit requires a fixed subreddit.
    *  Would need to create a method that can select a subreddit :(
    *
    *  Note: Posting on Twitter would require API keys. */
    private fun sharePost(view: View, post: PostItem) {

        val popupMenu = PopupMenu(view.context, view)
        popupMenu.menuInflater.inflate(R.menu.share_menu, popupMenu.menu)

        val message = "Check out this awesome post: ${post.title} ${post.description}"

        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.share_facebook -> shareToFacebook(post.imageURL, message, view)
                R.id.share_twitter -> shareToTwitter(post.imageURL, message, view)
                R.id.share_other -> shareToOther(post.imageURL, message, view)
                // Add other cases for additional platforms
            }
            true
        }
        popupMenu.show()
    }

    private fun shareToFacebook(imageURL: String, message: String, view: View) {
        // Implement Facebook sharing logic
        if(imageURL == ""){
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, message)
            }

            val context = view.context
            context.startActivity(Intent.createChooser(sendIntent, "Share via"))
        }
        else{
            // Implement logic with image through facebook API
        }
    }

    private fun shareToTwitter(imageURL: String, message: String, view: View) {
        // Implement Twitter sharing logic
        if(imageURL == ""){
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, message)
            }

            val context = view.context
            context.startActivity(Intent.createChooser(sendIntent, "Share via"))
        }
        else {
            // Implement logic with image through twitter API

            // Depreciated version of posting tweets using twitter4j and twitter APIv1.1
            /*GlobalScope.launch(Dispatchers.Main){
            val success = withContext(Dispatchers.IO){
                val twitter = (context.applicationContext as SchedulogApplication).getTwitterInstance()

                try {
                    // Create a StatusUpdate object with the message and image URL
                    val statusUpdate = StatusUpdate.of(message)
                    val imageUri = Uri.parse(post.imageURL)
                    //statusUpdate.attachmentUrl(post.imageURL)

                    // Post the tweet
                    val status = twitter.v1().tweets().updateStatus(statusUpdate)


                    if (status.inReplyToStatusId > 0) {
                        // Tweet was successfully posted
                        Timber.d("Tweet posted: ${status.id}")
                    } else {
                        // Handle the case where the tweet was not posted successfully
                        Timber.d("Tweet was not posted: ${status.id}")
                    }

                } catch (e: TwitterException) {
                    // Handle exceptions
                    e.printStackTrace()
                }
            }
        }*/
        }
    }

    private fun shareToOther(imageURL: String, message: String, view: View) {
        // If there is no image then post the message
        // Else, post the image + message
        if(imageURL == ""){
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, message)
            }

            val context = view.context
            context.startActivity(Intent.createChooser(sendIntent, "Share via"))
        }
        else{
            val imageUri = Uri.parse(imageURL)

            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                type = "image/*"
                putExtra(Intent.EXTRA_STREAM, imageUri)
                putExtra(Intent.EXTRA_TEXT, message)
            }

            val context = view.context
            context.startActivity(Intent.createChooser(sendIntent, "Share via"))
        }
    }


    private fun onAttendEvent(view: View, eventKey: String) {
        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid

        if (currentUserUid != null) {
            val eventRef = FirebaseDatabase.getInstance().reference.child("events").child(eventKey)
            Timber.d("Current User UID: %s", currentUserUid)
            val attendingUsersRef = eventRef.child("attending-users").child(currentUserUid)

            attendingUsersRef.setValue(true)
        }
    }

    private fun cancelAttendedEvent(view: View, eventKey: String) {
        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid

        if (currentUserUid != null) {
            val eventRef = FirebaseDatabase.getInstance().reference.child("events").child(eventKey)
            Timber.d("Current User UID: %s", currentUserUid)
            val attendingUsersRef = eventRef.child("attending-users").child(currentUserUid)

            attendingUsersRef.setValue(false)
        }
    }
}


