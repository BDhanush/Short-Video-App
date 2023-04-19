package com.example.shortvideoapp.model

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

data class Post(val videoURL:String,val uid:String,val title:String,val description:String,val upvotes:Int,val downvotes:Int)
{
    private val databaseUsername: DatabaseReference = FirebaseDatabase.getInstance("https://shortvideoapp-e7456-default-rtdb.asia-southeast1.firebasedatabase.app/").reference.child("users").child(uid);
    val username:String=databaseUsername.getValue(String.class);
}