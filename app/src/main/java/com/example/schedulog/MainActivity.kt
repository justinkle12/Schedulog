package com.example.schedulog

import LoginFragment
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.service.autofill.UserData
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import com.example.schedulog.databinding.ActivityMainBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

data class UserData(val username: String, val email: String)



class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this // Required for LiveData in layouts

        // Register button click listener
        binding.registerButton.setOnClickListener {
            val registrationFragment = RegistrationFragment()
            registrationFragment.show(supportFragmentManager, "RegistrationFragment")
        }

        // Login button click listener
        binding.loginButton.setOnClickListener {
            val loginFragment = LoginFragment()
            loginFragment.show(supportFragmentManager, "LoginFragment")
        }
    }



    fun testRead(){
        val database = Firebase.database
        val myRef = database.getReference("Justin")
        // Read from the database
        myRef.addValueEventListener(object: ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                val value = snapshot.getValue<String>()
                Log.d("firebase", "Value is: " + value)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("firebase", "Failed to read value.", error.toException())
            }

        })
    }
}