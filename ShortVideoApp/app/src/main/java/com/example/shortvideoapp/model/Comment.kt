package com.example.shortvideoapp.model

import java.util.Date

data class Comment(
    val id: String,
    val userId: String,
    val username: String?=null,
    val profilePictureUrl: String?=null,
    val text: String?=null,
    val timestamp: Date?=null
)