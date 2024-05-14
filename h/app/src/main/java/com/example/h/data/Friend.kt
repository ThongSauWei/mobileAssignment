package com.example.h.data

data class Friend(
    var friendID : String,
    val requestUserID : String,
    val receiveUserID : String,
    val status : String
) {
    constructor() : this("", "", "", "")
}
