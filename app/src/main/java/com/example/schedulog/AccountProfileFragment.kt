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
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.example.schedulog.databinding.FragmentAccountProfileBinding

class AccountProfileFragment : DialogFragment() {

    private lateinit var mAuth: FirebaseAuth


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_account_profile, container, false)
        mAuth = FirebaseAuth.getInstance()


        // Find the logout button and set its click listener
        val logoutButton = view.findViewById<Button>(R.id.btnLogout)
        logoutButton.setOnClickListener {
            // Call the logout function
            logoutUser()
        }

        //Find the Manage account info button and set its click listener
        val accountInfoButton = view.findViewById<Button>(R.id.btnManageAccount)
        accountInfoButton.setOnClickListener {
            // Call the logout function
            navigateUserToAccountInfo()
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
        dismiss()
        transaction.commit()
    }

    private fun navigateUserToAccountInfo() {
        // You can navigate to another fragment or perform any other necessary actions here
        // For example, you can navigate back to the login screen
        // Replace R.id.fragmentContainer with the ID of the container where you want to display the login fragment
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, AccountInfoFragment())
        dismiss()
        transaction.commit()
    }


}