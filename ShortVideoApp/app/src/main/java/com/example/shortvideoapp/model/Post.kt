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
}