package com.example.schedulog

import android.Manifest
import android.content.pm.PackageManager
import android.media.AudioManager
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
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.example.schedulog.databinding.FragmentAudioDescriptionDialogBinding
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
    private var isPlaying = false
    private var isStopped = false
    val currentUser = FirebaseAuth.getInstance().currentUser
    val userId = currentUser?.uid //TODO Replace with the userId with rated user
    var postId = arguments?.getString("post_id") ?: ""
    var hostId = arguments?.getString("user_id") ?: ""
    var audioUrl = arguments?.getString("audio_url") ?: ""
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        postId = (arguments?.getString("post_id").toString())
        audioUrl = (arguments?.getString("audio_url").toString())
        hostId = arguments?.getString("user_id").toString()
        binding = FragmentAudioDescriptionDialogBinding.inflate(inflater, container, false)
        val view = binding.root

        permissionGranted = ActivityCompat.checkSelfPermission(requireContext(),permissions[0]) == PackageManager.PERMISSION_GRANTED

        if(!permissionGranted){
            ActivityCompat.requestPermissions(requireActivity(),permissions, REQUEST_CODE)
        }






        // Initialize variables
        var submitButton = binding.submitButton
        submitButton.visibility = View.GONE
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
            startAudio()
            // Get the rating and review from the input fields
            Timber.e("playing audio")

        }




        submitButton.setOnClickListener {
            var fileName : String = userId.toString() + "_" + postId
            var file = Uri.fromFile(File("${dirPath}/Recordings/${fileName}.mp3"))

            val storageRef = Firebase.storage.reference

            storageRef.child("audios/events/$postId/$userId").putFile(file)



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

    @RequiresApi(Build.VERSION_CODES.S)
    private fun pauseRecording(){
        isPaused = true
        binding.recordButton.setImageResource(R.drawable.play_button)
        recorder.stop()
        binding.submitButton.visibility = View.VISIBLE
        binding.playButton.text = "Play"
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun resumeRecording(){
        startRecording()
        isPaused = false
        binding.recordButton.setImageResource(R.drawable.pause_icon)

    }



    @RequiresApi(Build.VERSION_CODES.S)
    private fun startRecording(){
        binding.submitButton.visibility = View.GONE
        var fileName : String = userId.toString() + "_" + postId
        Timber.e("$fileName.mp3 : Output File")
        recorder = MediaRecorder(requireContext())



        //dirPath = "${requireContext().externalCacheDir?.absolutePath}/"
        dirPath = Environment.getExternalStorageDirectory().toString()
        Timber.e(dirPath)
        var simpleDateFormat = SimpleDateFormat("yyyy,MM,hh,mm,ss")
        var date = simpleDateFormat.format(Date())
        filename = "audio_record_$date"

        recorder.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile("${dirPath}/Recordings/${fileName}.mp3")

        }
        recorder.prepare()
        recorder.start()


        binding.recordButton.setImageResource(R.drawable.pause_icon)
        isRecording = true
        isPaused = false
    }

    private fun pauseAudio(){
        isStopped = true


    }


    private fun resumeAudio(){
        isStopped = false

    }






    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun startAudio(){
        val storageRef = Firebase.storage.reference
        //val audioRef = storageRef.child("audios/events/$postId/$userId").toString() + ".mp3"
        var firebaseAudio = Uri.fromFile(File("https://firebasestorage.googleapis.com/v0/b/schedulog.appspot.com/o/audios%2Fevents%2F-Nl0e0xDNg1OJd3qpeCH%2FWChSnRaRqwa6iTIPxZSt5PrNGSc2?alt=media&token=4ea47103-0067-49c1-9c97-af3d98985037"))

        var mp = MediaPlayer()
        var audioPath = "audios/events/$postId/$hostId"
        var audioRef = storageRef.child(audioPath)
        audioRef.downloadUrl.addOnSuccessListener { uri ->
            Timber.e(audioPath)
            Timber.e(uri.toString())

            mp.setDataSource(uri.toString())
            mp.prepare()
            mp.start()

        }



        isPlaying = true
        isStopped = false

        binding.pauseButton.setOnClickListener {
                mp?.stop()
                mp?.reset()
                mp?.release()
            // Get the rating and review from the input fields
            Timber.e("stopping audio")

        }

    }


    companion object {
        private const val TAG = "AudioDescriptionDialogFragment"
        fun newInstance(postItem: PostItem): AudioDescriptionDialogFragment {
            val fragment = AudioDescriptionDialogFragment()
            val args = Bundle()
            args.putString("post_id", postItem.eventKey)
            args.putString("audio_url",postItem.audioUrl)
            args.putString("user_id",postItem.user)
            fragment.arguments = args


            return fragment
        }


    }

}