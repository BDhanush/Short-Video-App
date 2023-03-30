package com.example.shortvideoapp

import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.MediaController
import android.widget.ProgressBar
import android.widget.Toast
import android.widget.VideoView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.tasks.Task
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.net.URI

/*add a new video*/
class AddVideoActivity : AppCompatActivity() {

    //actionBar
    private lateinit var actionBar: ActionBar

    //constants to pick video
    private val VIDEO_PICK_GALLERY_CODE = 100
    private val VIDEO_PICK_CAMERA_CODE = 101
    //constant to request video recording permission
    private val CAMERA_REQUEST_CODE = 102

    //array for camera request permissions
    private lateinit var cameraPermissions:Array<String>

    private lateinit var progressDialog: ProgressDialog

    //uri of picked video
    private var videoUri: android.net.Uri? = null

    private var title:String = "";

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_video)

        //init actionBar
        actionBar = supportActionBar!!
        //title
        actionBar.title = "Add New Video"
        //add back button
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.setDisplayShowHomeEnabled(true)

        //init camera permission array
        cameraPermissions = arrayOf(android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

        //init progressBar
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait")
        progressDialog.setMessage("Uploading Video...")
        progressDialog.setCanceledOnTouchOutside(false)


        val uploadVideoButton:Button=findViewById(R.id.uploadVideoButton)
        val pickGalleryFab:FloatingActionButton=findViewById(R.id.pickGalleryFab)
        //handle uploadVideoButton click
        uploadVideoButton.setOnClickListener {
            //get title
            val titleEnter:EditText=findViewById(R.id.titleEnter)
            title = titleEnter.text.toString().trim()
            if (TextUtils.isEmpty(title)) {
                //no title entered
                Toast.makeText(this, "Title is required", Toast.LENGTH_SHORT).show()
            }
            else if (videoUri == null) {
                //video is not picked
                Toast.makeText(this, "Pick A Video", Toast.LENGTH_SHORT).show()
            }
            else {
                //title and video provided
                uploadVideoFirebase()
            }
        }

        //handle pickGalleryFab click
        pickGalleryFab.setOnClickListener {
            videoPickDialog()
        }
    }

    private fun uploadVideoFirebase() {
        //show progress
        progressDialog.show()

        //timestamp
        val timestamp = ""+System.currentTimeMillis()

        //filepath and name in firebase storage
        val filePathAndName = "Videos/video_$timestamp"

        //storage reference
        val storageReference = FirebaseStorage.getInstance().getReference(filePathAndName)
        //upload video using uri of video
        storageReference.putFile(videoUri!!)
            .addOnSuccessListener { taskSnapshot ->
                //get url of uploaded video
                val uriTask:Task<Uri> = taskSnapshot.storage.downloadUrl
                while (!uriTask.isSuccessful);
                val downloadUrl = uriTask.result
                if (uriTask.isSuccessful) {
                    //video url is received successfully

                    //video details
                    val hashMap = HashMap<String, Any>()
                    hashMap["id"] = "$timestamp"
                    hashMap["title"] = "$title"
                    hashMap["timestamp"] = "$timestamp"
                    hashMap["videoUri"] = "$downloadUrl"

                    //put details into Database
                    val dbReference = FirebaseDatabase.getInstance().getReference("Videos")
                    dbReference.child(timestamp).setValue(hashMap)
                        .addOnSuccessListener { taskSnapshot ->
                            //video details added successfully
                            progressDialog.dismiss()
                            Toast.makeText(this, "Video Uploaded", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            //failed adding video details
                            progressDialog.dismiss()
                            Toast.makeText(this, "${e.message}", Toast.LENGTH_SHORT).show()
                        }

                }
            }
            .addOnFailureListener { e ->
                //failed uploading
                progressDialog.dismiss()
                Toast.makeText(this, "${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setVideoToVideoView() {
        //set the picked video to video view

        //video play controls
        val mediaController = MediaController(this)
        val videoView:VideoView=findViewById(R.id.videoView)
        mediaController.setAnchorView(videoView)

        //set media controller
        videoView.setMediaController(mediaController)

        //set video uri
        videoView.setVideoURI(videoUri)
        videoView.requestFocus()
        videoView.setOnPreparedListener {
            it.isLooping = true
            //by default play automatically
            videoView.start()
            //by default do not play automatically
            //videoView.pause()
        }
    }

    private fun videoPickDialog() {
        //options for where to upload from
        val options = arrayOf("Camera", "Gallery")
        //alert dialog
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Pick Video From").setItems(options) { _, i->
            //handle item clicks
            if (i==0) {
                //camera clicked
                if (!checkCameraPermissions()) {
                    //permission not given, request
                    requestCameraPermissions()
                }
                else {
                    //permission given
                    videoPickCamera()
                }
            }
            else {
                //gallery clicked
                videoPickGallery()
            }
        }
        .show()
    }

    private fun requestCameraPermissions() {
        ActivityCompat.requestPermissions(this, cameraPermissions, CAMERA_REQUEST_CODE)
    }

    private fun checkCameraPermissions():Boolean {
        val result1 = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        val result2 = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

        return result1 && result2
    }

    private fun videoPickGallery() {
        val intent = android.content.Intent()
        intent.type = "video/*"
        intent.action = Intent.ACTION_GET_CONTENT

        startActivityForResult(Intent.createChooser(intent, "Choose video"), VIDEO_PICK_GALLERY_CODE)
    }

    private fun videoPickCamera() {
        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        startActivityForResult(intent, VIDEO_PICK_CAMERA_CODE)
    }

    override fun onSupportNavigateUp(): Boolean {
        //goto previous activity
        onBackPressedDispatcher.onBackPressed()
        return super.onSupportNavigateUp()
    }

    /*handle permission results*/
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode) {
            CAMERA_REQUEST_CODE ->
                if (grantResults.size > 0) {
                    //check permission granted or not
                    val cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    val storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED
                    if (cameraAccepted && storageAccepted) {
                        videoPickCamera()
                    }
                    else {
                        Toast.makeText(this, "Permissions Denied", Toast.LENGTH_SHORT).show()
                    }
                }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    /*handle video pick results*/
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            //video is picked from camera or gallery
            if (requestCode == VIDEO_PICK_CAMERA_CODE) {
                videoUri = data!!.data
                setVideoToVideoView()
            }
            else if (requestCode == VIDEO_PICK_GALLERY_CODE) {
                videoUri = data!!.data
                setVideoToVideoView()
            }
        }
        else {
            //cancelled picking video
            Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}