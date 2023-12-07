package com.example.schedulog

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
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
import android.widget.Toast
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




class AudioDescriptionListenerDialogFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentAudioDescriptionListenerDialogBinding
    private var permissions = arrayOf(Manifest.permission.RECORD_AUDIO)
    private var permissionGranted = false



    private var isPlaying = false
    private var isPaused = false
    val currentUser = FirebaseAuth.getInstance().currentUser

    var postId = arguments?.getString("user_id") ?: ""
    var hostId = arguments?.getString("user_id") ?: ""

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        postId = (arguments?.getString("post_id").toString())
        hostId = (arguments?.getString("user_id").toString())
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
            startAudio()

            Timber.e("playing audio")

        }

        return view
    }






    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun startAudio(){
        val storageRef = Firebase.storage.reference
        var mp = MediaPlayer()
        var audioPath = "audios/events/$postId/$hostId"
        var audioRef = storageRef.child(audioPath)
        audioRef.downloadUrl.addOnSuccessListener { uri ->
            Timber.e(audioPath)
            Timber.e(uri.toString())

            mp.setDataSource(uri.toString())
            mp.prepare()
            mp.start()

        }.addOnFailureListener{e ->
            Toast.makeText(requireContext(),"No audio for this event.",Toast.LENGTH_SHORT
            ).show()
            dismiss()
        }

        binding.pauseButton.setOnClickListener {
            mp?.stop()
            mp?.reset()
            mp?.release()
            // Get the rating and review from the input fields
            Timber.e("stopping audio")

        }

    }

    companion object {
        private const val TAG = "AudioDescriptionListenerDialogFragment"
        fun newInstance(postItem: PostItem): AudioDescriptionListenerDialogFragment {
            val fragment = AudioDescriptionListenerDialogFragment()
            val args = Bundle()
            args.putString("post_id", postItem.eventKey)
            args.putString("user_id",postItem.user)
            fragment.arguments = args


            return fragment
        }


    }

}