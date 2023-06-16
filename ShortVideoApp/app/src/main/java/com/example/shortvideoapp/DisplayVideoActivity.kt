package com.example.shortvideoapp

import android.content.ContentValues
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.VideoView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.shortvideoapp.adapter.VideoItemAdapter
import com.example.shortvideoapp.firebasefunctions.databaseURL
import com.example.shortvideoapp.firebasefunctions.postFromMap
import com.example.shortvideoapp.model.Post
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class DisplayVideoActivity : AppCompatActivity() {
    val postDataset= mutableListOf<Post>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_video)
        val postDataset= mutableListOf<Post>()

        val videosViewPager:ViewPager2=findViewById(R.id.viewPagerVideos)
        val postKey = intent.getStringExtra("postKey")
        var database = FirebaseDatabase.getInstance(databaseURL).getReference("posts/$postKey")
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                postDataset.clear()
                // Get Post object and use the values to update the UI
                val postMap=dataSnapshot.value as Map<String,Any?>
                val post= postFromMap(postMap)
                post.key=postKey
                postDataset.add(post)

                val adapter = VideoItemAdapter(this@DisplayVideoActivity,postDataset);
                videosViewPager.adapter = adapter


            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(ContentValues.TAG, "loadPost:onCancelled", databaseError.toException())
            }
        })

    }
}
