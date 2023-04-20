package com.example.shortvideoapp.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.shortvideoapp.AboutFragment
import com.example.shortvideoapp.GridFragment

class ProfileTabAdapter(val fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {


    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return  when (position) {
            0 -> GridFragment()
            1 -> AboutFragment()
            else -> throw IllegalArgumentException("Invalid position: $position")
        }
    }
}
