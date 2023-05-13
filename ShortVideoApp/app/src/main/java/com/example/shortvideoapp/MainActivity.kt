package com.example.shortvideoapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.shortvideoapp.adapter.ExoPlayerItem
import com.example.shortvideoapp.adapter.VideoItemAdapter
import com.example.shortvideoapp.databinding.ActivityMainBinding
import com.example.shortvideoapp.model.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val exoPlayerItems = ArrayList<ExoPlayerItem>()
    private var currentItemIndex: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        playVideosFromFirebase()
    }

    private fun playVideosFromFirebase() {
        var auth: FirebaseAuth = Firebase.auth
        val videosViewPager:ViewPager2 = findViewById<ViewPager2>(R.id.viewPagerVideos)
        val videosURlList= mutableListOf<Post>()
        val dbReference = FirebaseDatabase.getInstance().getReference("Videos")
        dbReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val id = snapshot.child("id").value.toString()
                    val title = snapshot.child("title").value.toString()
                    val timestamp = snapshot.child("timestamp").value.toString()
                    val videoUri = snapshot.child("videoUri").value.toString()
                    val post = Post(videoUri,auth.currentUser!!.uid,title,title,0,0)
                    videosURlList.add(post)
                }
                if (videosURlList.isEmpty()) {
                    startActivity(Intent(this@MainActivity, ProfilePage::class.java))
                }

                videosViewPager.adapter = VideoItemAdapter(this@MainActivity, videosURlList,  object : VideoItemAdapter.OnVideoPreparedListener {
                    override fun onVideoPrepared(exoPlayerItem: ExoPlayerItem) {
                        exoPlayerItems.add(exoPlayerItem)
                    }
                })

                // Initialize currentItemIndex from SharedPreferences
                val sharedPrefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                currentItemIndex = sharedPrefs.getInt("currentItemIndex", 0)

                videosViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        val previousIndex = exoPlayerItems.indexOfFirst { it.exoPlayer.isPlaying }
                        if (previousIndex != -1) {
                            val player = exoPlayerItems[previousIndex].exoPlayer
                            player.pause()
                            player.playWhenReady = false
                        }
                        val newIndex = exoPlayerItems.indexOfFirst { it.position == position }
                        if (newIndex != -1) {
                            val player = exoPlayerItems[newIndex].exoPlayer
                            player.seekTo(0)
                            player.playWhenReady = true
                        }
                    }
                })
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors
            }
        })
    }

    override fun onPause() {
        super.onPause()

        // Save the current item index to SharedPreferences
        val sharedPrefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()
        editor.putInt("currentItemIndex", binding.viewPagerVideos.currentItem)
        editor.apply()

        pauseCurrentPlayer()
    }

    override fun onResume() {
        super.onResume()

        playCurrentPlayer()
    }

    override fun onStop() {
        super.onStop()

        pauseCurrentPlayer()
    }

    override fun onStart() {
        super.onStart()

        playCurrentPlayer()
    }

    override fun onDestroy() {
        super.onDestroy()

        releaseExoPlayers()
    }

    private fun pauseCurrentPlayer() {
        val index = exoPlayerItems.indexOfFirst { it.position == binding.viewPagerVideos.currentItem }
        if (index != -1) {
            val player = exoPlayerItems[index].exoPlayer
            player.pause()
            player.playWhenReady = false
        }
    }

    private fun playCurrentPlayer() {
        val index = exoPlayerItems.indexOfFirst { it.position == binding.viewPagerVideos.currentItem }
        if (index != -1) {
            val player = exoPlayerItems[index].exoPlayer
            player.seekTo(0)
            player.playWhenReady = true
            player.play()
        }
    }

    private fun releaseExoPlayers() {
        for (item in exoPlayerItems) {
            item.exoPlayer.release()
        }
        exoPlayerItems.clear()
    }
}