package com.example.schedulog

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.example.schedulog.databinding.FragmentRegistrationBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.example.schedulog.EntryFragment
import com.google.firebase.auth.ktx.auth
import timber.log.Timber


class RegistrationFragment : DialogFragment() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var binding: FragmentRegistrationBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_registration, container, false)
        mAuth = FirebaseAuth.getInstance()

        binding.backarrow.setOnClickListener{
            dismiss()
        }

        // Set click listener for the register button
        binding.rectsignup.setOnClickListener {
            try {
                val username = binding.usernametext.text.toString().trim()
                val email = binding.emailtext.text.toString().trim()
                val password = binding.passtext.text.toString().trim()
                val passconf = binding.passconftext.text.toString().trim()

                if(password == passconf) {
                    mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(requireActivity()) { task ->
                            if (task.isSuccessful) {
                                val user: FirebaseUser? = mAuth.currentUser

                                // Create a UserData object with the user's information
                                val userData = UserData(username, email)

                                // Write user registration data to the Realtime Database
                                testWrite(userData)
                                Toast.makeText(
                                    requireContext(),
                                    binding.usernametext.text.toString() + "\n"
                                            + binding.emailtext.text.toString() + "\n"
                                            + "has been successfully registered",
                                    Toast.LENGTH_SHORT
                                ).show()
                                dismiss()
                                val loginFragment = LoginFragment()
                                loginFragment.show(requireFragmentManager(), "LoginFragment")
                                //displayUserData(username, email)

                                //Send user a verification email
                                mAuth.currentUser?.sendEmailVerification()
                                    ?.addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            Timber.tag(TAG).d("Email sent.")

                                        }else (Timber.tag(TAG).d("Email has not been sent."))

                                    }

                                //displayUserData(username, email)


                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    "Registration failed: " + task.exception?.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                }else{
                    Toast.makeText(
                        requireContext(),
                        "Passwords don't match!!!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }catch (e: IllegalArgumentException){
                Toast.makeText(
                    requireContext(),
                    "Fill in all details!!!",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }
        return binding.root
    }
    //private fun displayUserData(username: String, email: String) {
        //binding.usernametext.text = "Username: $username"
        //binding.emailtext.text = "Email: $email"
    //}

    override fun onStart() {
        super.onStart()

        // Adjust the dialog size here
        val dialog = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.WRAP_CONTENT
            dialog.window?.setLayout(width, height)
        }
    }

    // ... Rest of your code
    fun testWrite(userData: com.example.schedulog.UserData) {
        // Write user registration data to the database
        val database = Firebase.database
        val usersRef = database.getReference("users") // "users" is the node where user information will be stored

        // Create a unique key for the user
        val userId = usersRef.push().key

        // Use the unique key to set the user's data in the database
        if (userId != null) {
            usersRef.child(userId).setValue(userData)
        }
    }

    companion object {
        private const val TAG = "RegistrationFragment"
    }
}