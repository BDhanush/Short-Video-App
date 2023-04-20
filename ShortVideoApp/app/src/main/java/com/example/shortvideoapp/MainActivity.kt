package com.example.shortvideoapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.example.shortvideoapp.adapter.VideoItemAdapter
import com.example.shortvideoapp.model.Post

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val videosViewPager:ViewPager2 = findViewById<ViewPager2>(R.id.viewPagerVideos)

        val videosURlList= mutableListOf<Post>()
        videosURlList.add(Post("https://cdn.discordapp.com/attachments/910134893151911946/1056972395543527474/iam-like-what-he-say-fuck-me-for-50-cent-talking-about-floyd-mayweather-reaction-video-meme-vidownload.mp4", "", "", "", 0, 0));
        videosURlList.add(Post("https://cdn.discordapp.com/attachments/765130391119593482/1081900110121807872/chatgpt.mp4", "", "", "", 0, 0));
        videosURlList.add(Post("https://cdn.discordapp.com/attachments/910134893151911946/1021783588187938826/VID-20220408-WA0001.mp4", "", "", "", 0, 0));
        videosViewPager.adapter = VideoItemAdapter(this,videosURlList);


    }
}