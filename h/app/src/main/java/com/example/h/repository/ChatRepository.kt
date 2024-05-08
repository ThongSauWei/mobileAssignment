package com.example.h.repository

import com.example.h.dao.ChatDAO
import com.example.h.data.Chat

class ChatRepository(private val chatDao : ChatDAO) {
    fun addChat(chat : Chat) {
        chatDao.addChat(chat)
    }

    fun getChat(initiatorUserID : String, receiverUserID : String, callback : (chat : Chat?, errorMessage : String?) -> Unit) {
        chatDao.getChat(initiatorUserID, receiverUserID) { chat, errorMessage ->
            callback(chat, errorMessage)
        }
    }
}