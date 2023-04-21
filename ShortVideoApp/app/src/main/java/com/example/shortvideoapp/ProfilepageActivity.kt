package com.example.shortvideoapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.shortvideoapp.adapter.ProfileTabAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class ProfilepageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val tabs:MutableList<Pair<String,Fragment>> = mutableListOf(Pair("Posts",GridFragment()),Pair("About",AboutFragment()))

        val tabsViewPager: ViewPager2 = findViewById(R.id.viewPager)
        val tabsAdapter=ProfileTabAdapter(this,tabs)
        val tabLayout: TabLayout= findViewById(R.id.tabLayout)
        tabsViewPager.adapter=tabsAdapter

        TabLayoutMediator(tabLayout,tabsViewPager) {tab,position->
            tab.text=tabs[position].first
        }.attach()

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
