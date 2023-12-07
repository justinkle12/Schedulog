package com.example.schedulog

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.ContactsContract.Directory
import android.provider.MediaStore.Audio
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.example.schedulog.databinding.FragmentAudioDescriptionListenerDialogBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date



class AudioDescriptionListenerDialogFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentAudioDescriptionListenerDialogBinding
    private var permissions = arrayOf(Manifest.permission.RECORD_AUDIO)
    private var permissionGranted = false


    private lateinit var recorder : MediaRecorder

    private var isPlaying = false
    private var isPaused = false
    val currentUser = FirebaseAuth.getInstance().currentUser
    val userId = currentUser?.uid //TODO Replace with the userId with rated user
    var postId = arguments?.getString("user_id") ?: ""

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        postId = (arguments?.getString("post_id").toString())
        binding = FragmentAudioDescriptionListenerDialogBinding.inflate(inflater, container, false)
        val view = binding.root

        permissionGranted = ActivityCompat.checkSelfPermission(requireContext(),permissions[0]) == PackageManager.PERMISSION_GRANTED

        if(!permissionGranted){
            ActivityCompat.requestPermissions(requireActivity(),permissions, REQUEST_CODE)
        }






        // Initialize variables

        var playButton = binding.playButton



        // Set a click listener for the submit button
        playButton.setOnClickListener {
            // Get the rating and review from the input fields
            when{
                isPaused -> resumeRecording()
                isPlaying -> pauseRecording()
                else -> startRecording()
            }
            Timber.e("recording audio")
            Timber.e("playing audio")

        }



        return view
    }


    @RequiresApi(Build.VERSION_CODES.S)
    private fun pauseRecording(){
        isPaused = true
        binding.playButton.text = "Play"

    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun resumeRecording(){
        startRecording()
        isPaused = false
        binding.playButton.text = "Pause"
    }



    @RequiresApi(Build.VERSION_CODES.S)
    private fun startRecording(){
        binding.playButton.text = "Pause"
        isPlaying = true
        isPaused = false

    }

    companion object {
        private const val TAG = "AudioDescriptionListenerDialogFragment"
        fun newInstance(postItem: PostItem): AudioDescriptionListenerDialogFragment {
            val fragment = AudioDescriptionListenerDialogFragment()
            val args = Bundle()
            args.putString("post_id", postItem.eventKey)
            fragment.arguments = args


            return fragment
        }


    }

}