package com.example.schedulog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.DialogFragment
import com.example.schedulog.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginFragment : DialogFragment() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        mAuth = FirebaseAuth.getInstance()

        // Set click listener for the login button
        binding.loginButton.setOnClickListener {
            val usernameOrEmail = binding.editTextUsernameOrEmail.text.toString().trim()
            val password = binding.editTextPassword.text.toString().trim()


            mAuth.signInWithEmailAndPassword(usernameOrEmail, password)
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        // Navigate to the FeedFragment upon successful login
                        navigateToFeedFragment()
                        //MainActivity().handleDrawerLocking(true)
                        dismiss()
                    } else {
                        // Handle login failure, e.g., show an error message.
                        displayMessage("Login failed: " + task.exception?.message)
                    }
                }
        }

        return binding.root
    }

    private fun displayMessage(message: String) {
        binding.displayMessage.text = message
    }
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

    private fun navigateToFeedFragment() {
        val feedFragment = FeedFragment()
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, feedFragment)
        transaction.addToBackStack(null) // If you want to add this transaction to the back stack
        transaction.commit()
    }
}
