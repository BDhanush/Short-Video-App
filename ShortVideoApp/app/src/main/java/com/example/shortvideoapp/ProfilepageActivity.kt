package com.example.shortvideoapp

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.palette.graphics.Palette
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomViewTarget
import com.bumptech.glide.request.transition.Transition
import com.example.shortvideoapp.adapter.ProfileTabAdapter
import com.example.shortvideoapp.adapter.pageBasedOnContext
import com.example.shortvideoapp.databinding.ActivityProfileBinding
import com.example.shortvideoapp.model.User
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ProfilepageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val auth = Firebase.auth
        val tabs:MutableList<Pair<String,Fragment>> = mutableListOf()
        val button:LinearLayout = findViewById(R.id.buttons)
        val buttonOther:LinearLayout = findViewById(R.id.buttonsOther)
        pageBasedOnContext(intent.getStringExtra("uid"),tabs,button,buttonOther)

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
    fun updateData(user:User,view: ConstraintLayout){

            Glide.with(this).load(user.profilePicture!!.toUri()).into(object : CustomViewTarget<ConstraintLayout, Drawable>(
                view
            ) {
                override fun onLoadFailed(errorDrawable: Drawable?) {
                    // error handling
                }

                override fun onResourceCleared(placeholder: Drawable?) {
                    // clear all resources
                }

                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable>?
                ) {
                    binding.profileImage.setImageDrawable(resource)
                    binding.profileImage.buildDrawingCache()
                    val bitmap: Bitmap = binding.profileImage.getDrawingCache()
                    Palette.Builder(bitmap).generate { it?.let { palette ->
                        val dominantColor = palette.getDominantColor(Color.LTGRAY)

                        binding.collapsingToolbar.setBackgroundColor(dominantColor)
                        binding.collapsingToolbar.setStatusBarScrimColor(palette.getDarkMutedColor(dominantColor));
                        binding.collapsingToolbar.setContentScrimColor(palette.getMutedColor(dominantColor));

                    }
                    }
                }
            })
        }

}
