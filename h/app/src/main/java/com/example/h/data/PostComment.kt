package com.example.h.data

data class PostComment(
    var postCommentID : String,
    val postID : String,
    val userID : String,
    val dateTime : String,
    val content : String
) {
    constructor() : this("", "", "", "", "")
}
