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
    /*variables for selecting images from gallery*/
    private val pickImage = 100
    private var imageUri: Uri? = null
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

        val writeBtn = binding.buttonTestwrite
        val readBtn = binding.buttonTestread

        writeBtn.setOnClickListener {
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, pickImage)      // disregard java deprecation
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
    //TODO use this for event images with a postId
    private fun writeNewImage(userId: String, imageUri: Uri){
        // uses unique imageId TODO fix this code block
        /*var postRef = Firebase.database.getReference("images/users/$userId").push()
        postRef.setValue(imageUri.toString())*/

        // does not use unique imageId  !!this is temporary
        Firebase.database.reference.child("images/users")
        .child(userId)
        .setValue(imageUri.toString())

    }

    fun UploadFileTask(userId: String, imageUri: Uri, imageRef: StorageReference){
        val database = Firebase.database
        val dbImageRef = database.getReference("images/users/$userId")
        val uploadFileTask = imageRef.putFile(imageUri)

        val urlTask = uploadFileTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            imageRef.downloadUrl
        }.addOnCompleteListener{ task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                // put Uri into DB and show in logcat
                writeNewImage(userId, downloadUri)
                Timber.i("%s | Writing | %s", dbImageRef, downloadUri)
            } else {
                Timber.i("Unsuccessful Uri download")
            }
        }
        uploadFileTask.addOnFailureListener {
            Timber.i("Failed to upload image")
        }.addOnSuccessListener { taskSnapshot ->
            taskSnapshot.metadata
        }
    }


    // overriding activity result solely for uploading images as strings to the DB
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == pickImage) {
            Timber.i("Picked image and result OK")
            imageUri = data?.data

            // Upload image after selecting it from phone gallery
            imageUri?.let { testWrite(it) }
        }
    }


    /* Parameters: Uri
    *  Output: void
    *  This method takes an image file path and encodes it to base 64 string.
    *  The string is added to the DB and can be converted back to an image. */
    //TODO add userIds
    fun testWrite(imageUri: Uri){
        val selectedImagePath = getRealPathFromURI(imageUri)
        val storageRef = Firebase.storage.reference

        // path for Cloud storage. TODO change 'me' to userId
        val newImageRef = storageRef.child("images/users/me/$selectedImagePath")

        // convert image string 'selectedImagePath' to bitmap to bytearray for uploading to DB
        val bm = BitmapFactory.decodeFile(selectedImagePath)
        val baos = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos)    // bm is the bitmap object
        val data = baos.toByteArray()

        // Uploads image to cloud storage
        val uploadTask = newImageRef.putBytes(data)
        UploadFileTask("me", imageUri, newImageRef)
    }


    /* Parameters: Uri
    *  Output: String
    *  This method will get the image path from a Uri. */
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


    /* Parameters: None
    *  Output: None
    *  Currently, this method changes the image view from the image that is on the firebase cloud storage. */
    fun testRead(){
        val context = this

        // Reference to an image file in Cloud Storage
        val storageReference = Firebase.storage.reference

        // Reference to an image File in Realtime Database
        val database = Firebase.database
        val myRef = database.getReference("images/users/me")

        // Read from the database
        myRef.addValueEventListener(object: ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                //Use this if not using push on writeNewImage TODO enable this when writeNewImage is updated
                /*val imageValue = snapshot.getValue<HashMap<String, String>>()
                Timber.i(imageValue.toString())*/

                // !!temporary snapshot value
                val value = snapshot.getValue<String>()
                Timber.i("%s | Reading | %s" , myRef, value)

                // Try to update imageView
                try{
                    // Gets image reference on update
                    val gsReference = value?.let { Firebase.storage.getReferenceFromUrl(it) }
                    gsReference?.downloadUrl?.addOnSuccessListener {
                        // downloads url from db
                    }?.addOnFailureListener{
                        Timber.i("Download url unsuccessful")
                    }

                    // Do stuff. In this case, update imageView using Glide
                    val iView = binding.imageView
                    Glide.with(context).load(gsReference).into(iView)
                }
                catch (e: Error){
                    Timber.i("Error when uploading image")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.i("firebase: Failed to read value", error.toException())
            }
        })
    }
}