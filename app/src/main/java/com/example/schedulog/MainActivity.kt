package com.example.schedulog

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.service.autofill.UserData
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
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
    private lateinit var editTextUsername: EditText
    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var registerButton: Button
    private lateinit var displayUsername: TextView
    private lateinit var displayEmail: TextView
    private lateinit var mAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val writeBtn = findViewById<Button>(R.id.button_testwrite)
        val readBtn = findViewById<Button>(R.id.button_testread)
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword); // Add this line
        registerButton = findViewById(R.id.registerButton);
        displayUsername = findViewById(R.id.displayUsername);
        displayEmail = findViewById(R.id.displayEmail);

        mAuth = FirebaseAuth.getInstance();

        registerButton.setOnClickListener(View.OnClickListener {
            val username = editTextUsername.text.toString().trim()
            val email = editTextEmail.text.toString().trim()
            val password = editTextPassword.text.toString().trim()

            mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this@MainActivity, OnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user: FirebaseUser? = mAuth.currentUser

                        // Create a UserData object with the user's information
                        val userData = UserData(username, email)

                        // Write user registration data to the Realtime Database
                        testWrite(userData)

                        displayUserData(username, email)
                    } else {
                        Toast.makeText(this@MainActivity, "Registration failed: " + task.exception?.message, Toast.LENGTH_SHORT).show()
                    }
                })
        })

        //writeBtn.setOnClickListener() {
            //testWrite()
        //}

        readBtn.setOnClickListener() {
            testRead()
        }
    }
    private fun displayUserData(username: String, email: String) {
        displayUsername.text = "Username: $username"
        displayEmail.text = "Email: $email"
    }
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