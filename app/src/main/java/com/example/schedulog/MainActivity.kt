package com.example.schedulog

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.schedulog.databinding.ActivityMainBinding
import com.example.schedulog.databinding.SelectImageBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import timber.log.Timber
import java.io.ByteArrayOutputStream

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.i("onCreate Called")

        binding = ActivityMainBinding.inflate(layoutInflater)
        //setContentView(R.layout.activity_events) for scrollable view

        setContentView(binding.root)

        if (savedInstanceState == null) {
            // adding postFragment to activity_main.xml for testing purposes
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            val postFragment = PostFragment() // Create an instance of your PostFragment
            fragmentTransaction.add(R.id.container, postFragment)
            fragmentTransaction.commit()
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


    }