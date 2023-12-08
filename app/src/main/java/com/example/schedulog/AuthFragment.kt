package com.example.schedulog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.example.schedulog.databinding.FragmentAuthBinding
import com.example.schedulog.databinding.FragmentLoginBinding

import com.google.firebase.auth.FirebaseAuth
import timber.log.Timber

class AuthFragment : DialogFragment() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var binding: FragmentAuthBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_auth, container, false)
        mAuth = FirebaseAuth.getInstance()

        // Set click listener for the login button
        binding.authButton.setOnClickListener {

                //return user back to login fragment
                navigateToLoginFragment()

        }
        binding.resendButton.setOnClickListener {
            mAuth.currentUser?.sendEmailVerification()
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Timber.tag(RegistrationFragment.TAG).d("Email sent.")

                    }else (Timber.tag(RegistrationFragment.TAG).d("Email has not been sent."))

                }
        }

        return binding.root
    }


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

    //sends user back to the login fragment
    private fun navigateToLoginFragment() {
        val loginFragment = LoginFragment()
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, loginFragment)
        transaction.addToBackStack(null) // If you want to add this transaction to the back stack
        transaction.commit()
    }

    companion object {
        private const val TAG = "AuthFragment"
    }

}
