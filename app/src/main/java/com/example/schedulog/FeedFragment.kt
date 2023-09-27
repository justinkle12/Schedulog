package com.example.schedulog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
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

        return view
    }

}

