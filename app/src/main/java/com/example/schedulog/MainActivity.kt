package com.example.schedulog

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import timber.log.Timber

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Timber.i("onCreate Called")

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

    /** Lifecycle Methods **/
    /** So far only added logging methods. Can add code to interact with lifecycles.**/
    override fun onStart() {
        super.onStart()

        Timber.i("onStart Called")
    }

    override fun onResume() {
        super.onResume()
        Timber.i("onResume Called")
    }

    override fun onPause() {
        super.onPause()
        Timber.i("onPause Called")
    }

    override fun onStop() {
        super.onStop()
        Timber.i("onStop Called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.i("onDestroy Called")
    }

    override fun onRestart() {
        super.onRestart()
        Timber.i("onRestart Called")
    }


    /** Functions and methods **/
    fun testWrite(){
        // Write a message to the database
        val database = Firebase.database
        val myRef = database.getReference("Justin")

        val value = "Hello, World! - Justin"
        myRef.setValue(value)
        Timber.i("%s | Writing | %s", myRef, value)
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
                Timber.i("%s | Reading | %s" , myRef, value)
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.i("firebase: Failed to read value", error.toException())
            }

        })
    }
}