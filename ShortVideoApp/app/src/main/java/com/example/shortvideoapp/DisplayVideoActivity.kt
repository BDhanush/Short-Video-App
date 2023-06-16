package com.example.shortvideoapp

import android.net.Uri
import android.os.Bundle
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity


class DisplayVideoActivity : AppCompatActivity() {
    private lateinit var videoView: VideoView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_video)

        videoView = findViewById(R.id.videoView)

        val videoUrl = intent.getStringExtra("videoUrl")
        videoUrl?.let {
            val videoUri = Uri.parse(it)
            videoView.setVideoURI(videoUri)
            videoView.start()
        }
    }
}
