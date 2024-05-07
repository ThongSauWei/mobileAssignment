package com.example.h.data

data class Chat(
    val chatID : String,
    val initiatorUserID : String,
    val receiverUserID : String,
    val initiatorLastSeen : String,
    val receiverLastSeen : String
)
