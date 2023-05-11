package com.example.shortvideoapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.shortvideoapp.adapter.ProfileTabAdapter
import com.example.shortvideoapp.adapter.pageBasedOnContext
import com.example.shortvideoapp.model.User
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ProfilepageActivity(val user: User) : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.test)

        val auth = Firebase.auth
        val tabs:MutableList<Pair<String,Fragment>> = mutableListOf()
        val button:LinearLayout = findViewById(R.id.buttons)
        val buttonOther:LinearLayout = findViewById(R.id.buttonsOther)
        pageBasedOnContext(user.uid,tabs,button,buttonOther)

        val tabsViewPager: ViewPager2 = findViewById(R.id.viewPager)
        val tabsAdapter=ProfileTabAdapter(this,tabs)
        val tabLayout: TabLayout= findViewById(R.id.tabLayout)
        tabsViewPager.adapter=tabsAdapter
        TabLayoutMediator(tabLayout,tabsViewPager) {tab,position->
            tab.text=tabs[position].first
        }.attach()

        //handle upload button click
        val uploadButton:Button =findViewById(R.id.btnUpload)
        uploadButton.setOnClickListener {
            //starts the activity that adds a new video
            startActivity(Intent(this, AddVideoActivity::class.java))
        }

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

            override fun onTabSelected(tab: TabLayout.Tab?) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }
        })
    }
}
