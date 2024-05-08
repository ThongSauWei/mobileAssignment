package com.example.h.data

data class PostComment(
    val postCommentID : String,
    val postID : String,
    val userID : String,
    val dateTime : String,
    val content : String
)
