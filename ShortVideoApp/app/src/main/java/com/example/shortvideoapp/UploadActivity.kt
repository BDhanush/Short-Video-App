package com.example.shortvideoapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton

/*display list of videos*/
class UploadActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)

        //actionBar title
        title = "Videos"

        //handle upload button click
        val uploadButton:FloatingActionButton=findViewById(R.id.uploadButtonFab)
        uploadButton.setOnClickListener {
            //starts the activity that adds a new video
            startActivity(Intent(this, AddVideoActivity::class.java))
        }

        //handle reelsPage button click
        val reelsPageButton:FloatingActionButton=findViewById(R.id.reelsPageButtonFab)
        reelsPageButton.setOnClickListener {
            //starts the activity that adds a new video
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}