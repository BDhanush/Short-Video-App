package com.example.shortvideoapp.model

data class User(val uid:String?=null,val username:String?=null,val firstName:String?=null,val lastName:String?=null,val email:String?) {
    val followers:MutableList<String> = mutableListOf();
    val following:MutableList<String> = mutableListOf();
    val posts:MutableList<String> = mutableListOf();
    val about:String?=null;
}