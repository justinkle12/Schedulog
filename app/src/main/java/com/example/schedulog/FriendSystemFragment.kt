package com.example.schedulog

import FriendAdapter
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.schedulog.databinding.FragmentFriendSystemBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

class FriendSystemFragment : Fragment() {
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    private var _binding: FragmentFriendSystemBinding? = null
    private val binding get() = _binding!!

    private val currentUser = FirebaseAuth.getInstance().currentUser
    private lateinit var friendsRef: DatabaseReference

    private lateinit var adapter: FriendAdapter
    private val friendsList = mutableListOf<Friend>()

    data class Friend(
        val username: String,
        val email: String
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFriendSystemBinding.inflate(inflater, container, false)
        val view = binding.root

        friendsRef = FirebaseDatabase.getInstance().reference
            .child("users")
            .child(currentUser?.uid.toString())
            .child("friends")

        adapter = FriendAdapter(friendsList)
        binding.friendsRecyclerView.adapter = adapter
        binding.friendsRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        binding.addFriendButton.setOnClickListener {
            val friendUsername = binding.usernameInput.text.toString().trim()
            if (friendUsername.isNotEmpty()) {
                addFriendByUsername(friendUsername)
            } else {
                binding.usernameInputLayout.error = "Please enter a username"
            }
        }

        // Use Coroutine to introduce a delay before loading friends
        coroutineScope.launch {
            delay(2000) // Delay for 1 second (adjust as needed)
            loadFriends() // Load friends after the delay
        }

        return view
    }

    private fun addFriendByUsername(username: String) {
        // Check if the username exists in the database
        val usersRef = FirebaseDatabase.getInstance().reference.child("users")
        usersRef.orderByChild("username").equalTo(username).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // Username found, get the friend's UID
                    val friendUID = snapshot.children.first().key
                    if (friendUID != null) {
                        // Add the friend to the current user's friends list
                        friendsRef.child(friendUID).setValue(true)
                        binding.usernameInputLayout.error = null // Clear any previous error
                        binding.usernameInput.text?.clear() // Clear the input field
                    }
                } else {
                    binding.usernameInputLayout.error = "Username not found"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle errors here
            }
        })
    }

    private fun loadFriends() {
        friendsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                friendsList.clear()
                for (friendSnapshot in snapshot.children) {
                    // Get the friend's UID (which is also the key)
                    val friendUID = friendSnapshot.key
                    if (friendUID != null) {
                        // Fetch additional details of the friend if needed
                        val friendDetailsRef = FirebaseDatabase.getInstance().reference
                            .child("users")
                            .child(friendUID)
                        friendDetailsRef.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(detailsSnapshot: DataSnapshot) {
                                if (detailsSnapshot.exists()) {
                                    // You can get the email and other details here
                                    val email = detailsSnapshot.child("email").value.toString()
                                    val username = detailsSnapshot.child("username").value.toString()
                                    val friend = Friend(username, email)
                                    friendsList.add(friend)
                                    Timber.d(friend.email)
                                    Timber.d(friend.username)

                                    // Notify the adapter that the data has changed
                                    adapter.notifyDataSetChanged()
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                // Handle errors here
                            }
                        })
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle errors here
            }
        })
        Timber.d("loadFriends() has been called!!!")
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}