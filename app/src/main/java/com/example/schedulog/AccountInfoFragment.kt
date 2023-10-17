package com.example.schedulog

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.concurrent.CompletableFuture

class AccountInfoFragment : DialogFragment() {

    private lateinit var mAuth: FirebaseAuth
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_account_info, container, false)
        mAuth = FirebaseAuth.getInstance()

        //Populating username and full name from database on load
        fetchCurrentUserUsername().thenAccept { username ->
            if (username != null) {
                // Use the 'username' in your UI or perform any other actions
                val userNameTextView = view.findViewById<TextView>(R.id.textViewUsername)
                userNameTextView.text = "@" + username
            } else {
                Toast.makeText(requireContext(), "Cannot get username", Toast.LENGTH_SHORT).show()
            }
        }

        fetchCurrentUserFirstName().thenAccept { username ->
            if (username != null) {
                // Use the 'username' in your UI or perform any other actions
                val firstNameTextView = view.findViewById<TextView>(R.id.textViewFirstName)
                firstNameTextView.text = username + " "
            } else {
                Toast.makeText(requireContext(), "Cannot get username", Toast.LENGTH_SHORT).show()
            }
        }

        fetchCurrentUserLastName().thenAccept { username ->
            if (username != null) {
                // Use the 'username' in your UI or perform any other actions
                val lastNameTextView = view.findViewById<TextView>(R.id.textViewLastName)
                lastNameTextView.text = username
            } else {
                Toast.makeText(requireContext(), "Cannot get username", Toast.LENGTH_SHORT).show()
            }
        }



        // Find the edit username button and set its click listener
        val editUsernameButton = view.findViewById<Button>(R.id.btnEditUsername)
        editUsernameButton.setOnClickListener {

            val currentUser = FirebaseAuth.getInstance().currentUser
            val uid = currentUser?.uid
            val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().reference
            if (uid != null) {
                val userReference: DatabaseReference = databaseReference.child("users").child(uid)
                userReference.child("username").setValue(view.findViewById<EditText>(R.id.editTextUsername).text.toString())

                fetchCurrentUserUsername().thenAccept { username ->
                    if (username != null) {
                        // Use the 'username' in your UI or perform any other actions
                        val userNameTextView = view.findViewById<TextView>(R.id.textViewUsername)
                        userNameTextView.text = "@" + username
                    } else {
                        Toast.makeText(requireContext(), "Cannot get username", Toast.LENGTH_SHORT).show()
                    }
                }
            }




            // change the user's username into what they entered in the field
            view.findViewById<TextView>(R.id.textViewUsername).text = "@" + view.findViewById<EditText>(R.id.editTextUsername).text
        }

        // Find the edit first name button and set its click listener
        val editFirstNameButton = view.findViewById<Button>(R.id.btnEditFirstName)
        editFirstNameButton.setOnClickListener {
            // Call the edit last name function
            addFirstName(view.findViewById<EditText>(R.id.editTextFirstName).text.toString())

            fetchCurrentUserFirstName().thenAccept { username ->
                if (username != null) {
                    // Use the 'username' in your UI or perform any other actions
                    val firstNameTextView = view.findViewById<TextView>(R.id.textViewFirstName)
                    firstNameTextView.text = username + " "
                } else {
                    Toast.makeText(requireContext(), "Cannot get username", Toast.LENGTH_SHORT).show()
                }
            }

        }

        // Find the edit last name button and set its click listener
        val editLastNameButton = view.findViewById<Button>(R.id.btnEditLastName)
        editLastNameButton.setOnClickListener {
            // Call the edit last name function
            addLastName(view.findViewById<EditText>(R.id.editTextLastName).text.toString())


            fetchCurrentUserLastName().thenAccept { username ->
                if (username != null) {
                    // Use the 'username' in your UI or perform any other actions
                    val lastNameTextView = view.findViewById<TextView>(R.id.textViewLastName)
                    lastNameTextView.text = username
                } else {
                    Toast.makeText(requireContext(), "Cannot get username", Toast.LENGTH_SHORT).show()
                }
            }

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


    private fun addFirstName(firstName: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val uid = currentUser?.uid

        if (uid != null) {
            val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().reference
            val userReference: DatabaseReference = databaseReference.child("users").child(uid)

            // Update the user's profile with first name and last name
            userReference.child("first_name").setValue(firstName)
        }
    }

    private fun addLastName(lastName: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val uid = currentUser?.uid

        if (uid != null) {
            val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().reference
            val userReference: DatabaseReference = databaseReference.child("users").child(uid)

            // Update the user's profile with first name and last name
            userReference.child("last_name").setValue(lastName)
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun fetchCurrentUserUsername(): CompletableFuture<String?> {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val uid = currentUser?.uid
        val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().reference


        val future = CompletableFuture<String?>()

        if (uid != null) {

            val userReference: DatabaseReference = databaseReference.child("users").child(uid)
            val usernameReference: DatabaseReference = userReference.child("username")

            usernameReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val username = dataSnapshot.value?.toString()

                    future.complete(username)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    future.completeExceptionally(databaseError.toException())
                }
            })
        } else {
            future.complete(null)
        }

        return future
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun fetchCurrentUserFirstName(): CompletableFuture<String?> {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val uid = currentUser?.uid
        val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().reference


        val future = CompletableFuture<String?>()

        if (uid != null) {

            val userReference: DatabaseReference = databaseReference.child("users").child(uid)
            val usernameReference: DatabaseReference = userReference.child("first_name")

            usernameReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val firstName = dataSnapshot.value?.toString()

                    future.complete(firstName)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    future.completeExceptionally(databaseError.toException())
                }
            })
        } else {
            future.complete(null)
        }

        return future
    }


    @RequiresApi(Build.VERSION_CODES.N)
    fun fetchCurrentUserLastName(): CompletableFuture<String?> {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val uid = currentUser?.uid
        val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().reference


        val future = CompletableFuture<String?>()

        if (uid != null) {

            val userReference: DatabaseReference = databaseReference.child("users").child(uid)
            val usernameReference: DatabaseReference = userReference.child("last_name")

            usernameReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val lastName = dataSnapshot.value?.toString()

                    future.complete(lastName)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    future.completeExceptionally(databaseError.toException())
                }
            })
        } else {
            future.complete(null)
        }

        return future
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
                        val transaction =
                            requireActivity().supportFragmentManager.beginTransaction()
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

