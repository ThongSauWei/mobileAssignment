package com.example.h.repository

import com.example.h.dao.ChatDAO
import com.example.h.data.Chat

class ChatRepository(private val chatDao : ChatDAO) {
    fun addChat(chat : Chat) {
        chatDao.addChat(chat)
    }

    suspend fun getChat(userID_1 : String, userID_2 : String) : Chat? {
        return chatDao.getChat(userID_1, userID_2)
    }

    suspend fun getChatByID(chatID : String) : Chat? {
        return chatDao.getChatByID(chatID)
    }

    suspend fun getChatByUser(userID : String) : List<Chat> {
        return chatDao.getChatByUser(userID)
    }

    fun deleteChat(chatID : String) {
        chatDao.deleteChat(chatID)
    }
}