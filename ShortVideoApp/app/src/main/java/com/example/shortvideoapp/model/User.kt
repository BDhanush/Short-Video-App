package com.example.shortvideoapp.model

data class User(val uid:String?=null,var username:String?=null,var firstName:String?=null,var lastName:String?=null,val email:String?) {
    val followers:MutableList<String> = mutableListOf();
    val following:MutableList<String> = mutableListOf();
    val posts:MutableList<String> = mutableListOf();
    var about:String?=null;
    var profilePicture:String;
    init{
        profilePicture=""
    }
}