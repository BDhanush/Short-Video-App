package com.example.shortvideoapp

import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import androidx.viewpager2.widget.ViewPager2
import com.example.shortvideoapp.adapter.VideoItemAdapter
import com.example.shortvideoapp.firebasefunctions.databaseURL
import com.example.shortvideoapp.firebasefunctions.postFromMap
import com.example.shortvideoapp.model.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity() {
    val postDataset= mutableListOf<Post>()
    lateinit var videosViewPager:ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val action: String? = intent?.action
        val data: Uri? = intent?.data
        if(data!=null)
        {
            val intent = Intent(this, DisplayVideoActivity::class.java)
            intent.putExtra("postKey", data.toString().removePrefix("app://shortform.open/post/"))
            // Add any other necessary data to the intent
            startActivity(intent)
        }
        var auth: FirebaseAuth = Firebase.auth


//        val videosURlList= mutableListOf<Post>()
//        videosURlList.add(Post("https://cdn.discordapp.com/attachments/910134893151911946/1056972395543527474/iam-like-what-he-say-fuck-me-for-50-cent-talking-about-floyd-mayweather-reaction-video-meme-vidownload.mp4",
//            auth.currentUser!!.uid,"test","test"));
//        videosURlList.add(Post("https://cdn.discordapp.com/attachments/765130391119593482/1081900110121807872/chatgpt.mp4",
//            auth.currentUser!!.uid,"test","test"));
//        videosURlList.add(Post("https://cdn.discordapp.com/attachments/910134893151911946/1021783588187938826/VID-20220408-WA0001.mp4",
//            auth.currentUser!!.uid,"test","test"));
//        for(i in videosURlList)
//        {
//            postDataset.add(i)
//        }

        videosViewPager = findViewById(R.id.viewPagerVideos)
        readData()

        val swipeRefreshLayout:SwipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener{
            swipeRefreshLayout.isRefreshing = false
            readData()
        }


    }
    fun readData()
    {
        var database = FirebaseDatabase.getInstance(databaseURL).getReference("posts")
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                postDataset.clear()
                // Get Post object and use the values to update the UI
                for(snapshot in dataSnapshot.children) {
                    val postMap=snapshot.value as Map<String,Any?>
                    val post=postFromMap(postMap)
                    post.key=snapshot.key as String
                    postDataset.add(post)
                }

                if (postDataset.isEmpty()) {
                    startActivity(Intent(this@MainActivity, AddVideoActivity::class.java))
                }

                val adapter = VideoItemAdapter(applicationContext,postDataset);
                videosViewPager.adapter = adapter

            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        })
    }
}