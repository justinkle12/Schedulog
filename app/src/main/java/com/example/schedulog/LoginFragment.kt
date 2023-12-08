package com.example.schedulog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.example.schedulog.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth
import timber.log.Timber

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


        binding.backarrow.setOnClickListener{
            dismiss()
        }

        // Set click listener for the login button
        binding.rectlogin.setOnClickListener {
            val usernameOrEmail = binding.useremailtext.text.toString().trim()
            val password = binding.passtext.text.toString().trim()

            mAuth.signInWithEmailAndPassword(usernameOrEmail, password)
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        //testing purposes only, uncomment to bypass authentication
                        //dismiss()
                        //navigateToFeedFragment()
                        if(mAuth.currentUser?.isEmailVerified == true){

                            // Navigate to the FeedFragment upon successful login and user is verified
                            dismiss()
                            navigateToFeedFragment()

                        }else {

                            dismiss()
                            mAuth.currentUser?.sendEmailVerification()
                                ?.addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Timber.tag(RegistrationFragment.TAG).d("Email sent.")

                                    }else (Timber.tag(RegistrationFragment.TAG).d("Email has not been sent."))

                                }
                            navigateToAuthFragment()

                        }
                    } else {
                        // Handle login failure, e.g., show an error message.
                        Toast.makeText(
                            requireContext(),
                            "Login failed: " + task.exception?.message,
                            Toast.LENGTH_SHORT
                        ).show()
                        
                        Timber.tag(TAG).e("Login failed: %s", task.exception?.message)
                    }
                }
        }

        return binding.root
    }

    //private fun displayMessage(message: String) {
        //binding.displayMessage.text = message
    //}
    override fun onStart() {
        super.onStart()
        Timber.tag(TAG).i("onStart called")

        // Adjust the dialog size here
        val dialog = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.WRAP_CONTENT
            dialog.window?.setLayout(width, height)
        }
    }

    private fun navigateToFeedFragment() {
        val feedFragment = FeedAndCalendarFragment()
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, feedFragment)
        transaction.addToBackStack(null) // If you want to add this transaction to the back stack
        transaction.commit()
    }

    private fun navigateToAuthFragment() {
        val authFragment = AuthFragment()
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, authFragment)
        transaction.addToBackStack(null) // If you want to add this transaction to the back stack
        transaction.commit()
    }

    companion object {
        private const val TAG = "LoginFragment"
    }

}
