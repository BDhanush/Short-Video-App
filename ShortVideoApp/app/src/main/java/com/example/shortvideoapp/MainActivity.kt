package com.example.shortvideoapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.VideoView
import androidx.viewpager2.widget.ViewPager2
import com.example.shortvideoapp.adapter.VideoItemAdapter
import com.example.shortvideoapp.model.Video
import com.facebook.shimmer.ShimmerFrameLayout

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val videosViewPager:ViewPager2 = findViewById<ViewPager2>(R.id.viewPagerVideos)


        val videosURlList= mutableListOf<Video>()
        videosURlList.add(Video("https://cdn.discordapp.com/attachments/910134893151911946/1056972395543527474/iam-like-what-he-say-fuck-me-for-50-cent-talking-about-floyd-mayweather-reaction-video-meme-vidownload.mp4"));
        videosURlList.add(Video("https://cdn.discordapp.com/attachments/765130391119593482/1081900110121807872/chatgpt.mp4"));
        videosURlList.add(Video("https://cdn.discordapp.com/attachments/910134893151911946/1021783588187938826/VID-20220408-WA0001.mp4"));
        videosViewPager.adapter = VideoItemAdapter(this,videosURlList);


    }
}