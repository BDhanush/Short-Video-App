package com.example.shortvideoapp.model

import android.content.ContentValues.TAG
import android.util.Log
import com.example.shortvideoapp.firebasefunctions.databaseURL
import com.google.firebase.database.*

data class Post(val videoURL:String,val uid:String,val title:String,val description:String)
{
    var key:String?=null
    var upvotes:MutableList<String> = mutableListOf()
    var downvotes:MutableList<String> = mutableListOf()
    var comments:MutableList<String> = mutableListOf()
}