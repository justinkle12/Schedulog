package com.example.schedulog

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Path
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.io.File


class MainActivity : AppCompatActivity() {
    /*variables for selecting images from gallery*/
    private val pickImage = 100
    private var imageUri: Uri? = null
    private var imagePath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Timber.i("onCreate Called")

        //setContentView(R.layout.activity_events)

        setContentView(R.layout.activity_main)

        val writeBtn = findViewById<Button>(R.id.button_testwrite)
        val readBtn = findViewById<Button>(R.id.button_testread)

        writeBtn.setOnClickListener {
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, pickImage)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == pickImage) {
            imageUri = data?.data
            imagePath = imageUri?.let { getRealPathFromURI(it) }
            Timber.i("imagePath: %s", imagePath)

            testWrite(imagePath)
        }
    }

    fun testWrite(selectedImagePath: String?){
        // Write a message to the database
        val database = Firebase.database
        val myRef = database.getReference("images")
        // get image from phone

        // convert image to bitmap to bytearray for storing into DB
        val bm = BitmapFactory.decodeFile(selectedImagePath)
        val baos = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos) // bm is the bitmap object

        val b = baos.toByteArray()

        val encodedImage: String = Base64.encodeToString(b, Base64.DEFAULT)

        val value = encodedImage

        // put into DB and show in logcat
        myRef.setValue(value)
        Timber.i("%s | Writing | %s", myRef, value)
    }

    private fun getRealPathFromURI(contentURI: Uri): String? {
        val cursor = contentResolver.query(contentURI, null, null, null, null)
        return if (cursor == null) { // Source is Dropbox or other similar local file path
            contentURI.path
        } else {
            cursor.moveToFirst()
            val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            cursor.getString(idx)
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
                Timber.i("%s | Reading | %s" , myRef, value)
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.i("firebase: Failed to read value", error.toException())
            }

        })
    }
}