package com.example.schedulog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

import com.example.schedulog.databinding.FragmentEntryBinding

data class UserData(val username: String, val email: String)

class EntryFragment : Fragment() {

    private lateinit var binding: FragmentEntryBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEntryBinding.inflate(inflater, container, false)

        // Register button click listener
        binding.buttonEk10.setOnClickListener {
            val registrationFragment = RegistrationFragment()
            registrationFragment.show(requireFragmentManager(), "RegistrationFragment")
        }

        // Login button click listener
        binding.button1.setOnClickListener {
            val loginFragment = LoginFragment()
            loginFragment.show(requireFragmentManager(), "LoginFragment")
        }

        return binding.root
    }

}
