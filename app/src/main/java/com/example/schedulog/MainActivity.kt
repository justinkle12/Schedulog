package com.example.schedulog

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val writeBtn = findViewById<Button>(R.id.button_testwrite)
        val readBtn = findViewById<Button>(R.id.button_testread)

        writeBtn.setOnClickListener() {
            testWrite()
        }

        readBtn.setOnClickListener() {
            testRead()
        }
    }

    fun testWrite(){
        // Write a message to the database
        val database = Firebase.database
        val myRef = database.getReference("Justin")

        myRef.setValue("Hello, World! - Justin")
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