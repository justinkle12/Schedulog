package com.example.schedulog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.schedulog.databinding.FragmentFriendSystemBinding
import com.example.schedulog.databinding.FragmentPendingBinding
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
class PendingFragment : DialogFragment() {
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    private var _binding: FragmentPendingBinding? = null
    private val binding get() = _binding!!

    private val currentUser = FirebaseAuth.getInstance().currentUser
    private lateinit var friendsRef: DatabaseReference

    private lateinit var adapter: PendingAdapter
    private val friendsList = mutableListOf<Friend>()

    data class Friend(
        val uid: String,
        val fullName: String,
        val username: String
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPendingBinding.inflate(inflater, container, false)
        val view = binding.root

        friendsRef = FirebaseDatabase.getInstance().reference
            .child("users")
            .child(currentUser?.uid.toString())
            .child("friends")
            .child("pending")

        adapter = PendingAdapter(requireContext(), friendsList)
        binding.friendsRecyclerView.adapter = adapter
        binding.friendsRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        binding.backbutton.setOnClickListener {
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragmentContainer, FriendSystemFragment())
            transaction.addToBackStack(null) // Optional: Add the fragment to the back stack
            transaction.commit()
        }

        // Use Coroutine to introduce a delay before loading friends
        loadFriends()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Start the coroutine loop
        startCoroutineLoop()
    }

    private fun startCoroutineLoop() {
        coroutineScope.launch {
            while (isActive) {
                // Do some background work if needed

                // Update the adapter on the main thread
                withContext(Dispatchers.Main) {
                    adapter.notifyDataSetChanged()
                }

                // Delay for 1000 ms (1 second)
                delay(600)
            }
        }
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
                                    val fname = detailsSnapshot.child("first_name").value.toString()
                                    val lname = detailsSnapshot.child("last_name").value.toString()
                                    val username = "@" + detailsSnapshot.child("username").value.toString()
                                    val fullName = "$fname $lname"
                                    val uid = detailsSnapshot.key
                                    val friend = Friend(uid.toString(), fullName, username)
                                    friendsList.add(friend)
                                    Timber.d(friend.username)
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
        coroutineScope.cancel()
    }
}