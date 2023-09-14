package com.example.schedulog

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
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

        // Find the delete account button and set its click listener
        val deleteAccountButton = view.findViewById<Button>(R.id.deleteAccountButton)
        deleteAccountButton.setOnClickListener {
            // Call the delete account function
            deleteAccount()
        }

        return view
    }

    private fun logoutUser() {
        mAuth.signOut()
        // You can navigate to another fragment or perform any other necessary actions here
        // For example, you can navigate back to the login screen
        // Replace R.id.fragmentContainer with the ID of the container where you want to display the login fragment
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, EntryFragment())
        transaction.commit()
    }

    private fun deleteAccount() {
        val user = mAuth.currentUser

        if (user != null) {
            user.delete()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Account deletion successful
                        // You can perform actions like displaying a success message to the user
                        displayMessage("Account deleted successfully.")
                    } else {
                        // Account deletion failed
                        // You can handle the error by displaying an error message to the user
                        displayMessage("Account deletion failed: ${task.exception?.message}")
                    }
                }
        } else {
            // currentUser is null, handle the case when the user is not logged in
            displayMessage("User is not logged in.")
        }
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, EntryFragment())
        transaction.commit()

        // Find the schedule button and set its click listener to open the com.example.schedulog.ScheduleFragment
        view?.findViewById<Button>(R.id.scheduleButton)?.setOnClickListener {
            openScheduleFragment()
        }


    }

    private fun displayMessage(message: String) {
        // You can display the message in a TextView or Toast, for example
        // Here's an example using a Toast
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun openScheduleFragment() {
        // Create and navigate to the com.example.schedulog.ScheduleFragment
        val scheduleFragment = ScheduleFragment()
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, scheduleFragment)
        transaction.addToBackStack(null) // Optional: Add to back stack for navigation
        transaction.commit()
    }

}

