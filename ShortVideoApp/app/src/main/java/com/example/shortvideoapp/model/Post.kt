package com.example.shortvideoapp.model

import android.util.Log
import com.google.firebase.database.*

data class Post(val videoURL:String,val uid:String,val title:String,val description:String,val upvotes:Int,val downvotes:Int)
{
    private val databaseUsername: DatabaseReference = FirebaseDatabase.getInstance("https://shortvideoapp-e7456-default-rtdb.asia-southeast1.firebasedatabase.app/").reference.child("users").child(uid).child("username");
    var username:String?=null;
    init {
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                username = dataSnapshot.value as String;
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
//                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        databaseUsername.addValueEventListener(postListener)
    }
}