package com.example.schedulog

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils.replace
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.schedulog.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this // Required for LiveData in layouts

        if (savedInstanceState == null) {
            // Only add the EntryFragment if it's not already added (prevents overlapping fragments)
            val fragmentManager: FragmentManager = supportFragmentManager
            val transaction: FragmentTransaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.fragmentContainer, EntryFragment())
            transaction.addToBackStack(null) // Optional, for navigation
            transaction.commit()
        }
    }
}