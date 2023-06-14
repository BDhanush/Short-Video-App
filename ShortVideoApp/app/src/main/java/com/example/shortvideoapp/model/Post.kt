package com.example.shortvideoapp.model

import android.content.ContentValues.TAG
import android.util.Log
import com.example.shortvideoapp.firebasefunctions.databaseURL
import com.google.firebase.database.*

data class Post(val videoURL:String,val uid:String,val title:String,val description:String,val upvotes:Int,val downvotes:Int)
{
    var profilePicture: String?=null;
    var username:String?=null;
    var comments:MutableList<String> = mutableListOf();
    init {
        //get username from database
        val databaseUsername: DatabaseReference = FirebaseDatabase.getInstance(databaseURL).reference.child("users").child(uid).child("username");
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                username = dataSnapshot.value as String;
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        databaseUsername.addValueEventListener(postListener)


    }
}