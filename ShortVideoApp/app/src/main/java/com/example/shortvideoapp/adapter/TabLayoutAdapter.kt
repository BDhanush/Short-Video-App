package com.example.shortvideoapp.adapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.shortvideoapp.FirstFragment
import com.example.shortvideoapp.SecondFragment

class TabLayoutAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return 2 // Return the number of tabs
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> FirstFragment()
            1 -> SecondFragment()
            else -> throw IllegalArgumentException("Invalid position: $position")
        }
    }
}
