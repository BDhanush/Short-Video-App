
package com.example.shortvideoapp

import android.content.ContentValues
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage


class DisplayImageActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    var imageKey:String?=null
    var eventKey:String?=null


    override fun onCreate(savedInstanceState: Bundle?) {

        val imageUrl = intent.getStringExtra("imageUrl")

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_image)

        imageView = findViewById(R.id.imageView)

        if (!imageUrl.isNullOrEmpty()) {
            Glide.with(applicationContext)
                .load(imageUrl.toUri())
                .into(imageView)
        }
        val auth=Firebase.auth


    }
}
