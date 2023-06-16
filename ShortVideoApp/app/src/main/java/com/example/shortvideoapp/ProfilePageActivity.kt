package com.example.shortvideoapp

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
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
import com.example.shortvideoapp.firebasefunctions.databaseURL
import com.example.shortvideoapp.firebasefunctions.userFromMap
import com.example.shortvideoapp.model.User
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

class ProfilePageActivity : AppCompatActivity() {
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
        val uploadButton: Button =findViewById(R.id.btnUpload)
        val uid=intent.getStringExtra("uid")

        //handle upload button click
        uploadButton.setOnClickListener {
            //starts the activity that adds a new video
            startActivity(Intent(this, AddVideoActivity::class.java))
        }

        var user:User?=null
        var database = FirebaseDatabase.getInstance(databaseURL).getReference("users")

        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                if(uid!=auth.currentUser!!.uid){
                    if(dataSnapshot.child(uid!!).child("followers").child(auth.currentUser!!.uid).exists()){
                        binding.follow.text="Following"
                    }
                }
                val userMap=dataSnapshot.child(uid!!).value as Map<String,Any?>
                user= userFromMap(userMap)
                user!!.uid=uid
                pageBasedOnContext(uid,user!!.about!!,tabs,button,buttonOther)
                val tabsViewPager: ViewPager2 = findViewById(R.id.viewPager)
                val tabsAdapter=ProfileTabAdapter(this@ProfilePageActivity,tabs)
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

                updateData(user!!,view)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(ContentValues.TAG, "loadPost:onCancelled", databaseError.toException())
            }
        })
        binding.btnEditProfile.setOnClickListener {
            Intent(this, EditProfileActivity::class.java).also{
                startActivity(it)
            }
        }
        if(uid!=auth.currentUser!!.uid)
        {
            database.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // Get Post object and use the values to update the UI

                    if(dataSnapshot.child("users/${auth.currentUser!!.uid}/savedPosts").child(auth.currentUser!!.uid).exists()){
                        binding.follow.text="Following"
                    }

                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Getting Post failed, log a message
                    Log.w(ContentValues.TAG, "loadPost:onCancelled", databaseError.toException())
                }
            })
        }

        binding.follow.setOnClickListener {
            if(binding.follow.text=="Follow")
            {
                binding.follow.text="Following"
                database.child("$uid/followers").child(auth.currentUser!!.uid).setValue(true)
                database.child("${auth.currentUser!!.uid}/following").child(uid!!).setValue(true)
            }else{
                binding.follow.text="Follow"
                database.child("$uid/followers").child(auth.currentUser!!.uid).removeValue()
                database.child("${auth.currentUser!!.uid}/following").child(uid!!).removeValue()
            }
        }

    }

    fun updateData(user:User,view: ConstraintLayout){

        binding.profileName.text=user.firstName.toString()+" "+user.lastName.toString()
        binding.collapsingToolbar.title=user.username.toString()
//        binding.textFollowers.text= user.followers.size.toString()
//        binding.textFollowing.text = user.following.size.toString()
//        binding.textPosts.text=user.posts.size.toString()
        var database = FirebaseDatabase.getInstance(databaseURL).getReference("users/${user.uid}")
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                binding.textFollowers.text="Followers: "+(dataSnapshot.child("followers").childrenCount).toString()
                binding.textFollowing.text="Following: "+(dataSnapshot.child("following").childrenCount).toString()
                binding.textPosts.text="Posts: "+(dataSnapshot.child("posts").childrenCount).toString()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(ContentValues.TAG, "loadPost:onCancelled", databaseError.toException())
            }
        })
        if(user.profilePicture!=null) {
            Glide.with(this).load(user.profilePicture!!.toUri())
                .into(object : CustomViewTarget<ConstraintLayout, Drawable>(
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
                        Palette.Builder(bitmap).generate {
                            it?.let { palette ->
                                val dominantColor = palette.getDominantColor(Color.LTGRAY)

//                                binding.collapsingToolbar.setBackgroundColor(dominantColor)
                                binding.collapsingToolbar.setStatusBarScrimColor(dominantColor);
                                binding.collapsingToolbar.setContentScrimColor(dominantColor);

                                var hsv = FloatArray(3)
                                Color.colorToHSV(dominantColor,hsv);

                                val brightness = hsv[2];

                                if (brightness < 0.5) {
                                    binding.collapsingToolbar.setCollapsedTitleTextColor(Color.WHITE)
                                } else {
                                    binding.collapsingToolbar.setCollapsedTitleTextColor(Color.BLACK)
                                }

                            }
                        }
                    }
                })
        }
    }

}
