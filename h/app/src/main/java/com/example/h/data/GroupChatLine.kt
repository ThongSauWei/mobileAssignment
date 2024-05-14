package com.example.h.data

data class GroupChatLine(
    var groupChatLineID : String,
    val senderID : String,
    val dateTime : String,
    val groupID : String,
    val content : String
) {
    constructor() : this("", "", "", "", "")
}
