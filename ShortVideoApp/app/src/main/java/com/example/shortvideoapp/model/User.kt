package com.example.shortvideoapp.model

data class User(var username:String?=null,var firstName:String?=null,var lastName:String?=null,var profilePicture:String?=null) {
    var uid:String?=null
    var email:String?=null
    var followers:MutableList<String> = mutableListOf();
    var following:MutableList<String> = mutableListOf();
    var posts:MutableList<String> = mutableListOf();
    var savedPosts:MutableList<String> = mutableListOf();
    var about:String?=null;
    constructor(map:Map<String,Any?>) : this() {
        username=map["username"] as String
        firstName=map["firstName"] as String
        lastName=map["lastName"] as String
        profilePicture=map["profilePicture"] as String?

        if(uid!=null)
            uid=map["uid"] as String
        if(email!=null)
            email=map["email"] as String

        followers=try{
            map["followers"] as MutableList<String>
        }catch (e:Exception){
            mutableListOf()
        }
        following=try{
            map["following"] as MutableList<String>
        }catch (e:Exception){
            mutableListOf()
        }
        posts=try{
            map["posts"] as MutableList<String>
        }catch (e:Exception){
            mutableListOf()
        }
        savedPosts=try{
            map["savedPosts"] as MutableList<String>
        }catch (e:Exception){
            mutableListOf()
        }
        about = if(about!=null) map["about"] as String else ""

    }

}