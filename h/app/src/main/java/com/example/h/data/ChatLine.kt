package com.example.h.data

data class ChatLine(
    var chatLineID : String,
    val senderID : String,
    val dateTime : String,
    val chatID : String,
    val content : String
) {
    constructor() : this("", "", "", "", "")
}
