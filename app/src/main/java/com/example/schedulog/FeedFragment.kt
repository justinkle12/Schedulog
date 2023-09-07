package com.example.schedulog

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth

class FeedFragment : Fragment() {

    private lateinit var mAuth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_feed, container, false)
        mAuth = FirebaseAuth.getInstance()

        val textView = view.findViewById<TextView>(R.id.textViewFeed)
        textView.text = "Welcome to the Feed Fragment!"


        // Find the logout button and set its click listener
        val logoutButton = view.findViewById<Button>(R.id.logoutButton)
        logoutButton.setOnClickListener {
            // Call the logout function
            logoutUser()
        }
        return view
    }

    private fun logoutUser() {
        mAuth.signOut()
        // You can navigate to another fragment or perform any other necessary actions here
        // For example, you can navigate back to the login screen
        // Replace R.id.fragmentContainer with the ID of the container where you want to display the login fragment
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, LoginFragment())
        transaction.commit()
    }
}

