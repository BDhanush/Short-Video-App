package com.example.shortvideoapp.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class ProfileTabAdapter(
    fragmentActivity: FragmentActivity,
    private val tabs: MutableList<Pair<String,Fragment>>
) : FragmentStateAdapter(fragmentActivity) {


    override fun getItemCount(): Int {
        return tabs.size
    }

    override fun createFragment(position: Int): Fragment {
        return tabs[position].second
    }
}

