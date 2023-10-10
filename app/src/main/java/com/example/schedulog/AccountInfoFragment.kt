package com.example.schedulog

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.FirebaseAuth

class AccountInfoFragment : DialogFragment() {

    private lateinit var mAuth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_account_info, container, false)
        mAuth = FirebaseAuth.getInstance()


        // Find the logout button and set its click listener
        val editUsernameButton = view.findViewById<Button>(R.id.btnEditUsername)
        editUsernameButton.setOnClickListener {

            // change the user's username into what they entered in the field
            view.findViewById<TextView>(R.id.textViewUsername).text = "@" + view.findViewById<EditText>(R.id.editTextUsername).text
        }

        // Find the delete account button and set its click listener
        val editDOBButton = view.findViewById<Button>(R.id.btnEditDOB)
        editDOBButton.setOnClickListener {
            // Call the delete account function
            view.findViewById<TextView>(R.id.textViewDOB).text =  view.findViewById<EditText>(R.id.editTextDOB).text
        }

        // Find the done button and set its click listener
        val doneButton = view.findViewById<Button>(R.id.Donebutton)
        doneButton.setOnClickListener {

            //return user to account profile fragment
            returnUserToAccountProfile()

        }

        val deleteAccountButton = view.findViewById<Button>(R.id.DeleteAccount)
        deleteAccountButton.setOnClickListener{
            deleteAccount()
        }

        return view
    }

    private fun returnUserToAccountProfile() {
        // You can navigate to another fragment or perform any other necessary actions here
        // For example, you can navigate back to the login screen
        // Replace R.id.fragmentContainer with the ID of the container where you want to display the login fragment
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, AccountProfileFragment())
        dismiss()
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
                        val transaction = requireActivity().supportFragmentManager.beginTransaction()
                        transaction.replace(R.id.fragmentContainer, EntryFragment())
                        transaction.commit()
                        Toast.makeText(
                            requireContext(),
                            "Account deleted successfully.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        // Account deletion failed
                        // You can handle the error by displaying an error message to the user
                        Toast.makeText(
                            requireContext(),
                            "Account deletion failed: ${task.exception?.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        } else {
            // currentUser is null, handle the case when the user is not logged in
            Toast.makeText(
                requireContext(),
                "User is not logged in.",
                Toast.LENGTH_SHORT
            ).show()
        }

    }



}