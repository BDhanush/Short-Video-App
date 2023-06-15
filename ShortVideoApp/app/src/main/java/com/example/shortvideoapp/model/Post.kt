package com.example.shortvideoapp.model

import android.content.ContentValues.TAG
import android.util.Log
import com.example.shortvideoapp.firebasefunctions.databaseURL
import com.google.firebase.database.*

data class Post(var videoURL:String?=null,var thumbnail:String?=null,var uid:String?=null,var title:String?=null,var description:String?=null)
{
    var key:String?=null
    var thumbnail:String?=null
    var upvotes:MutableList<String> = mutableListOf()
    var downvotes:MutableList<String> = mutableListOf()
    var comments:MutableList<String> = mutableListOf()

    constructor(map:Map<String,Any?>) : this(
        videoURL=map["videoURL"] as String,
        thumbnail=map["thumbnail"] as String?,
        uid=map["uid"] as String,
        title=map["title"] as String,
        description=map["description"] as String) {
        if(map["thumbnail"]!=null)
            thumbnail=map["thumbnail"] as String

        if(map["key"]!=null)
            key = map["key"] as String
        videoURL=map["videoURL"] as String

        upvotes=try{
            map["upvotes"] as MutableList<String>
        }catch (e:Exception){
            mutableListOf()
        }
        downvotes=try{
            map["downvotes"] as MutableList<String>
        }catch (e:Exception){
            mutableListOf()
        }
        comments=try{
            map["comments"] as MutableList<String>
        }catch (e:Exception){
            mutableListOf()
        }

    }

}