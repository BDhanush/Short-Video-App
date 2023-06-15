package com.example.shortvideoapp.firebasefunctions

import com.example.shortvideoapp.model.Post
import com.example.shortvideoapp.model.User

const val databaseURL="https://shortform-6fdba-default-rtdb.asia-southeast1.firebasedatabase.app/"


fun userFromMap(map:Map<String,Any?>): User
{
    return User(map)
}

fun postFromMap(map:Map<String,Any?>): Post
{
    return Post(map)
}
