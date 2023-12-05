package com.example.schedulog

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.schedulog.databinding.UserItemBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import timber.log.Timber
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


/* This class is responsible for holding onto an instance of the view and
 * binding the PostItem(post_item.xml) to the RecyclerView(fragment_feed.xml). */
class UserViewHolder (
    private val binding: UserItemBinding,
    private val context: Context,

) : RecyclerView.ViewHolder(binding.root) {
    fun bind(userItem: UserItem) {
        binding.username.text = userItem.user
        binding.textPost.text = "Event : ${userItem.title}"


        loadPost(userItem.eventKey)
        loadUsername(userItem.user)

    }

    private fun loadPost(postId: String) {
        val usersRef = FirebaseDatabase.getInstance().getReference("events/$postId")

        usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val attendingUsersMap =
                        dataSnapshot.child("attending-users").value as? Map<String, Boolean>

                    CoroutineScope(Dispatchers.Main).launch {
                        val usernameList = mutableListOf<String>()

                        if (attendingUsersMap != null) {
                            for (users in attendingUsersMap) {
                                val username = getUsername(users.key)
                                username?.let {
                                    usernameList.add(it)
                                }
                            }

                            Timber.e(usernameList.toString())
                            val attendingUsersText = attendingUsersMap.keys.joinToString(", ")

                            if (!attendingUsersText.isNullOrBlank()) {
                                binding.attendingUsersText.text =
                                    "Attending: \n ${usernameList.joinToString(" \n ")}"
                            } else {
                                // Handle the case where attending-users is empty
                            }
                        } else {
                            Timber.tag("UserViewHolder | loadUsername")
                                .d("The username does not exist in the database")
                        }
                    }

                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle database error
            }
        })
    }

    private suspend fun getUsername(userId: String): String? {
        return CoroutineScope(Dispatchers.IO).async {
            val usersRef = FirebaseDatabase.getInstance().getReference("users/$userId")

            try {
                val dataSnapshot = usersRef.get().await()

                if (dataSnapshot.exists()) {
                    val username = dataSnapshot.child("username").getValue(String::class.java)
                    val firstName = dataSnapshot.child("first_name").getValue(String::class.java)
                    val lastName = dataSnapshot.child("last_name").getValue(String::class.java)

                    val fullName = "@$username - $firstName $lastName"
                    return@async fullName
                }
            } catch (e: Exception) {
                Timber.tag("UserViewHolder | loadUsername").e(e, "Error loading username")
            }

            return@async null
        }.await()
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

                    binding.username.text =  "@$username"
                    binding.userFirstLastName.text = "Created By: $fullName"

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
class UserListAdapter(
    private var userItems: List<UserItem>
) : RecyclerView.Adapter<UserViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UserViewHolder {
        val context=  parent.context
        val inflater = LayoutInflater.from(parent.context)
        val binding = UserItemBinding.inflate(inflater, parent, false)
        return UserViewHolder(binding, context)
    }

    override fun getItemCount(): Int {
        return userItems.size
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        Timber.tag("UserListAdapter").i(userItems.toString())
        val user = userItems[position]
        holder.bind(user)
    }
    fun setFilteredList(filteredList: ArrayList<UserItem>) {
        this.userItems = filteredList
        notifyDataSetChanged()
    }
}


