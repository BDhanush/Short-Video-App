package com.example.shortvideoapp

import android.R.attr.bitmap
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Im
import android.provider.MediaStore
import android.text.TextUtils
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.abedelazizshe.lightcompressorlibrary.CompressionListener
import com.abedelazizshe.lightcompressorlibrary.VideoCompressor
import com.abedelazizshe.lightcompressorlibrary.VideoQuality
import com.abedelazizshe.lightcompressorlibrary.config.Configuration
import com.abedelazizshe.lightcompressorlibrary.config.SaveLocation
import com.abedelazizshe.lightcompressorlibrary.config.SharedStorageConfiguration
import com.bumptech.glide.Glide
import com.example.shortvideoapp.model.Post
import com.google.android.gms.tasks.Task
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.io.File


/*add a new video*/
class AddVideoActivity : AppCompatActivity() {
    //constants to pick video
    private val VIDEO_PICK_GALLERY_CODE = 100
    private val IMAGE_PICK_GALLERY_CODE = 101
    private val VIDEO_PICK_CAMERA_CODE = 102
    //constant to request video recording permission
    private val CAMERA_REQUEST_CODE = 103

    //array for camera request permissions
    private lateinit var cameraPermissions:Array<String>

    private lateinit var progressDialog: ProgressDialog

    //uri and thumbnail of picked video
    private var videoUri: android.net.Uri? = null
    private var videoThumbnail: ByteArray? = null

    private var title:String = ""
    private var description:String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_video)

        //init camera permission array
        cameraPermissions = arrayOf(android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

        //init progressBar
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait")
        progressDialog.setMessage("Uploading Video...")
        progressDialog.setCanceledOnTouchOutside(false)

        val uploadVideoButton:Button=findViewById(R.id.uploadVideoButton)
        val chooseFromGallery:MaterialButton=findViewById(R.id.addVideo)
        val switchView:MaterialButton=findViewById(R.id.switchView)

        //handle uploadVideoButton click
        uploadVideoButton.setOnClickListener {
            //get title
            val titleEnter:EditText=findViewById(R.id.videoTitleInput)
            val descriptionEnter:EditText=findViewById(R.id.descriptionInput)
            title = titleEnter.text.toString().trim()
            description = descriptionEnter.text.toString().trim()
            if (TextUtils.isEmpty(title)) {
                //no title entered
                Toast.makeText(this, "Title is required", Toast.LENGTH_SHORT).show()
            }
            else if (TextUtils.isEmpty(description)) {
                //no description entered
                Toast.makeText(this, "Description is required", Toast.LENGTH_SHORT).show()
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

        //handle chooseFromGallery click
        switchView.setOnClickListener {
            val noPreview:TextView=findViewById(R.id.noPreview)
            val previewVideo:VideoView=findViewById(R.id.previewVideo)
            val previewThumbnail:ImageView=findViewById(R.id.previewThumbnail)

            if (switchView.text == "Preview Thumbnail") {
                switchView.text = "Preview Video"
                chooseFromGallery.text = "Choose Thumbnail"
                noPreview.text = "No Thumbnail Selected"
                previewVideo.visibility = View.GONE
                if (videoUri != null) {
                    noPreview.visibility = View.INVISIBLE
                    previewThumbnail.visibility = View.VISIBLE
                }
            }
            else {
                switchView.text = "Preview Thumbnail"
                chooseFromGallery.text = "Choose Video"
                noPreview.visibility = View.VISIBLE
                noPreview.text = "No Video Selected"
                if (videoUri != null)
                    previewVideo.visibility = View.VISIBLE
                previewThumbnail.visibility = View.INVISIBLE
            }
        }

        //handle switchView click
        chooseFromGallery.setOnClickListener {
            if (chooseFromGallery.text == "Choose Video") {
                videoPickDialog()
            }
            else {
                if (videoUri != null) {
                    imagePickGallery()
                }
                else {
                    Toast.makeText(this, "Please Select A Video First", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun uploadVideoFirebase() {
        //auth
        var auth: FirebaseAuth = Firebase.auth

        //show progress
        progressDialog.show()

        //timestamp
        val timestamp = ""+System.currentTimeMillis()

        //filepath and name in firebase storage
        val filePathAndNameVideo = "Videos/"+title+"_$timestamp"
        val filePathAndNameThumbnail = "Thumbnails/"+title+"_$timestamp"

        //storage reference
        val storageReferenceVideo = FirebaseStorage.getInstance().getReference(filePathAndNameVideo)
        val storageReferenceThumbnail = FirebaseStorage.getInstance().getReference(filePathAndNameThumbnail)

        //compress video before uploading
        VideoCompressor.start(
            context = applicationContext,
            uris = listOf(videoUri!!),
            isStreamable = false,
            sharedStorageConfiguration = SharedStorageConfiguration(
                saveAt = SaveLocation.movies,
                videoName = "compressed_video"
            ),
            configureWith = Configuration(
                quality = VideoQuality.VERY_HIGH,
                isMinBitrateCheckEnabled = false,
                videoBitrateInMbps = 1,
                disableAudio = false,
                keepOriginalResolution = false
                //videoWidth = 720.0,
                //videoHeight = 1280.0
            ),
            listener = object : CompressionListener {
                override fun onProgress(index: Int, percent: Float) {
                    // Update UI with progress value
                    runOnUiThread {
                        // Update progress dialog
                        progressDialog.progress = percent.toInt()
                    }
                }

                override fun onStart(index: Int) {
                    // Compression start
                }

                override fun onSuccess(index: Int, size: Long, path: String?) {
                    // On Compression success
                    //upload compressed video to firebase
                    val compressedVideoUri = Uri.fromFile(path?.let { File(it) })
                    storageReferenceVideo.putFile(compressedVideoUri)
                        .addOnSuccessListener { videoTaskSnapshot ->
                            //get url of uploaded video
                            val uriTaskVideo: Task<Uri> = videoTaskSnapshot.storage.downloadUrl
                            while (!uriTaskVideo.isSuccessful);
                            val downloadVideoUrl = uriTaskVideo.result
                            if (uriTaskVideo.isSuccessful) {
                                //video url is received successfully
                                if (videoThumbnail == null) {
                                    val imageView:ImageView=findViewById(R.id.previewThumbnail)
                                    val thumbnail = Bitmap.createBitmap(
                                        imageView.width,
                                        imageView.height,
                                        Bitmap.Config.ARGB_8888
                                    )
                                    val canvas = Canvas(thumbnail)
                                    imageView.draw(canvas)
                                    val baos = ByteArrayOutputStream()
                                    thumbnail?.compress(Bitmap.CompressFormat.PNG, 100, baos)
                                    videoThumbnail = baos.toByteArray()
                                }
                                //upload thumbnail to firebase
                                storageReferenceThumbnail.putBytes(videoThumbnail!!)
                                    .addOnSuccessListener {thumbnailTaskSnapshot ->
                                        //get url of thumbnail
                                        val uriTaskThumbnail: Task<Uri> = thumbnailTaskSnapshot.storage.downloadUrl
                                        while (!uriTaskThumbnail.isSuccessful);
                                        val downloadThumbnailUrl = uriTaskThumbnail.result
                                        if (uriTaskThumbnail.isSuccessful) {
                                            //video details
                                            val videoDetails = Post(
                                                "$downloadVideoUrl",
                                                "$downloadThumbnailUrl",
                                                auth.currentUser!!.uid,
                                                title,
                                                description
                                            )

                                            //put details into Database
                                            val dbReference =
                                                FirebaseDatabase.getInstance().getReference("posts")
                                            dbReference.child(timestamp).setValue(videoDetails)
                                                .addOnSuccessListener { taskSnapshot ->
                                                    //video details added successfully
                                                    progressDialog.dismiss()
                                                    Toast.makeText(
                                                        this@AddVideoActivity,
                                                        "Video Uploaded",
                                                        Toast.LENGTH_SHORT
                                                    ).show()

                                                    // Delete the compressed video from local storage
                                                    val compressedVideoFile = path?.let { File(it) }
                                                    if (compressedVideoFile != null) {
                                                        if (compressedVideoFile.exists()) {
                                                            compressedVideoFile.delete()
                                                        }
                                                    }
                                                }
                                                .addOnFailureListener { e ->
                                                    //failed adding video details
                                                    progressDialog.dismiss()
                                                    Toast.makeText(
                                                        this@AddVideoActivity,
                                                        "${e.message}",
                                                        Toast.LENGTH_SHORT
                                                    ).show()

                                                    // Delete the compressed video from local storage
                                                    val compressedVideoFile = path?.let { File(it) }
                                                    if (compressedVideoFile != null) {
                                                        if (compressedVideoFile.exists()) {
                                                            compressedVideoFile.delete()
                                                        }
                                                    }
                                                }
                                        }
                                    }
                                    .addOnFailureListener { e ->
                                        //failed adding thumbnail
                                        progressDialog.dismiss()
                                        Toast.makeText(this@AddVideoActivity, "${e.message}", Toast.LENGTH_SHORT).show()

                                        // Delete the compressed video from local storage
                                        val compressedVideoFile = path?.let { File(it) }
                                        if (compressedVideoFile != null) {
                                            if (compressedVideoFile.exists()) {
                                                compressedVideoFile.delete()
                                            }
                                        }
                                    }
                            }
                        }
                        .addOnFailureListener { e ->
                            //failed uploading
                            progressDialog.dismiss()
                            Toast.makeText(this@AddVideoActivity, "${e.message}", Toast.LENGTH_SHORT).show()

                            // Delete the compressed video from local storage
                            val compressedVideoFile = path?.let { File(it) }
                            if (compressedVideoFile != null) {
                                if (compressedVideoFile.exists()) {
                                    compressedVideoFile.delete()
                                }
                            }
                        }
                }

                override fun onFailure(index: Int, failureMessage: String) {
                    // On Failure
                    progressDialog.dismiss()
                    Toast.makeText(this@AddVideoActivity, "Compression failed: $failureMessage", Toast.LENGTH_SHORT).show()
                }

                override fun onCancelled(index: Int) {
                    // On Cancelled
                    progressDialog.dismiss()
                    Toast.makeText(this@AddVideoActivity, "Compression cancelled", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    private fun setVideoToVideoView() {
        //set the picked video to video view

        //video play controls
        val mediaController = MediaController(this)
        val videoView:VideoView=findViewById(R.id.previewVideo)
        val imageView:ImageView=findViewById(R.id.previewThumbnail)

        //set media controller
        mediaController.setAnchorView(videoView)
        mediaController.visibility = View.GONE

        //set videoView
        videoView.visibility = View.VISIBLE
        videoView.setMediaController(mediaController)

        //set imageView with default thumbnail
        Glide.with(this).load(videoUri).into(imageView)

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

    private fun imagePickGallery() {
        val intent = android.content.Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT

        startActivityForResult(Intent.createChooser(intent, "Choose image"), IMAGE_PICK_GALLERY_CODE)
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
            else if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                val thumbnail = MediaStore.Images.Media.getBitmap(this.contentResolver, data!!.data)
                val imageView:ImageView=findViewById(R.id.previewThumbnail)
                imageView.setImageBitmap(thumbnail)

                val baos = ByteArrayOutputStream()
                thumbnail?.compress(Bitmap.CompressFormat.PNG, 100, baos)
                videoThumbnail = baos.toByteArray()
            }
        }
        else {
            //cancelled picking video
            Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}