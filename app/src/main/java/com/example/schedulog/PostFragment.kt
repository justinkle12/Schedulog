package com.example.schedulog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.schedulog.databinding.FragmentPostBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import timber.log.Timber

class PostFragment : Fragment() {

    private var _binding: FragmentPostBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding =
            FragmentPostBinding.inflate(inflater, container, false)
        binding.postGrid.layoutManager = GridLayoutManager(context, 1)

        // Initialize variables
        val postItemList = ArrayList<PostItem>()
        val postListAdapter = PostListAdapter(postItemList)
        val recyclerView = binding.postGrid

        // Set RecyclerView Post adapter
        recyclerView.adapter = postListAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Initialize Firebase reference
        val database = Firebase.database
        val postsRef = database.getReference("posts")

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                postItemList.clear() // Clear the list to avoid duplicates


                for (postSnapshot in dataSnapshot.children) {
                    val postItem = postSnapshot.getValue(PostItem::class.java)

                    if (postItem != null) {
                        postItemList.add(postItem)
                        Timber.tag("PostFragment").i(postItem.toString())
                    }
                }

                // Update UI with the new postList
                postListAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle database errors here
                Timber.e("%s | Error reading post | %s", TAG, databaseError.toString())
            }
        }

        postsRef.addValueEventListener(postListener)

        return binding.root
    }

    companion object {
        private const val TAG = "PostFragment"
    }

}