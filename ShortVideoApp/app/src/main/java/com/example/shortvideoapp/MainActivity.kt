package com.example.shortvideoapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.example.shortvideoapp.adapter.VideoItemAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.example.shortvideoapp.model.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        playVideosFromFirebase()
    }

    private fun playVideosFromFirebase() {
        val videos: MutableList<Post> = mutableListOf()
        var auth: FirebaseAuth = Firebase.auth

        val videosViewPager:ViewPager2 = findViewById<ViewPager2>(R.id.viewPagerVideos)
        val dbReference = FirebaseDatabase.getInstance().getReference("Videos")
        dbReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val id = snapshot.child("id").value.toString()
                    val title = snapshot.child("title").value.toString()
                    val timestamp = snapshot.child("timestamp").value.toString()
                    val videoUri = snapshot.child("videoUri").value.toString()
                    val video = Post(videoUri, auth.currentUser!!.uid,"test","test",0,0)
                    videos.add(video)
                }
                videosViewPager.adapter = VideoItemAdapter(this@MainActivity, videos)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors
            }
        })
    }
}