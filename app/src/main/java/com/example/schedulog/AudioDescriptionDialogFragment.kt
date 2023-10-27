package com.example.schedulog

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore.Audio
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.example.schedulog.databinding.FragmentAudioDescriptionDialogBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import timber.log.Timber
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date


const val REQUEST_CODE = 200
class AudioDescriptionDialogFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentAudioDescriptionDialogBinding
    private var permissions = arrayOf(Manifest.permission.RECORD_AUDIO)
    private var permissionGranted = false


    private lateinit var recorder : MediaRecorder
    private var dirPath = ""
    private var filename = ""
    private var isRecording = false
    private var isPaused = false

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAudioDescriptionDialogBinding.inflate(inflater, container, false)
        val view = binding.root

        permissionGranted = ActivityCompat.checkSelfPermission(requireContext(),permissions[0]) == PackageManager.PERMISSION_GRANTED

        if(!permissionGranted){
            ActivityCompat.requestPermissions(requireActivity(),permissions, REQUEST_CODE)
        }






        // Initialize variables
        var submitButton = binding.submitButton
        var playButton = binding.playButton
        var recordButton = binding.recordButton

        recordButton.setOnClickListener {
            // Get the rating and review from the input fields
            when{
                isPaused -> resumeRecording()
                isRecording -> pauseRecording()
                else -> startRecording()
            }
            Timber.e("recording audio")

        }

        // Set a click listener for the submit button
        playButton.setOnClickListener {
            // Get the rating and review from the input fields
            Timber.e("playing audio")

        }
        submitButton.setOnClickListener {
            // Get the rating and review from the input fields
            Timber.e("submitting audio")

        }



        return view
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == REQUEST_CODE){
            permissionGranted = grantResults[0] == PackageManager.PERMISSION_GRANTED
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun pauseRecording(){
        recorder.pause()
        isPaused = true
        binding.recordButton.setImageResource(R.drawable.play_button)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun resumeRecording(){
        recorder.resume()
        isPaused = false
        binding.recordButton.setImageResource(R.drawable.pause_icon)
    }


    @RequiresApi(Build.VERSION_CODES.S)
    private fun startRecording(){

        recorder = MediaRecorder(requireContext())
        dirPath = "${requireContext().externalCacheDir?.absolutePath}/"

        var simpleDateFormat = SimpleDateFormat("yyyy,MM,hh,mm,ss")
        var date = simpleDateFormat.format(Date())
        filename = "audio_record_$date"

        recorder.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile("$dirPath$filename.mp3")

            try{
                prepare()
            }catch (e :IOException){}

            start()


        }

        binding.recordButton.setImageResource(R.drawable.pause_icon)
        isRecording = true
        isPaused = false

    }

    companion object {
        private const val TAG = "AudioDescriptionDialogFragment"
        fun newInstance(): AudioDescriptionDialogFragment {
            return AudioDescriptionDialogFragment()
        }


    }

}
