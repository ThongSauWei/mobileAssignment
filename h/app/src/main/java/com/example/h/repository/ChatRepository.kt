package com.example.h.repository

import com.example.h.dao.ChatDAO
import com.example.h.data.Chat

class ChatRepository(private val chatDao : ChatDAO) {
    fun addChat(chat : Chat) {
        chatDao.addChat(chat)
    }

    suspend fun getChat(initiatorUserID : String, receiverUserID : String) : Chat? {
        return chatDao.getChat(initiatorUserID, receiverUserID)
    }
}