package com.example.shortvideoapp.model

import java.sql.Timestamp
import java.util.Date

data class Comment(
    val uid:String?=null,
    val body:String?=null,
    val timestamp:Date?=null,
    var commentKey:String?=null
)
