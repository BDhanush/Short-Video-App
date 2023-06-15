package com.example.shortvideoapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.shortvideoapp.adapter.TabLayoutAdapter

class ProfilePage : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile_page_layout)

        // Get a reference to the ViewPager2 in profilepage.xml
        val viewPager: ViewPager2 = findViewById(R.id.viewPager)

        // Create an instance of the MyPagerAdapter class and set it as the adapter for the ViewPager2
        val adapter = TabLayoutAdapter(this)
        viewPager.adapter = adapter

        //handle upload button click
        val uploadButton: Button =findViewById(R.id.btnUpload)
        uploadButton.setOnClickListener {
            //starts the activity that adds a new video
            startActivity(Intent(this, AddVideoActivity::class.java))
        }
    }
}
