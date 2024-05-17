package com.example.h.repository

import com.example.h.dao.ChatDAO
import com.example.h.data.Chat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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

    suspend fun updateLastSeen(chatID : String, userID : String) {
        val lastSeen = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        val chat = chatDao.getChatByID(chatID)!!
        val newChat : Chat
        if (chat.initiatorUserID == userID) {
            newChat = Chat(chat.chatID, chat.initiatorUserID, chat.receiverUserID, lastSeen, chat.receiverLastSeen)
            chatDao.updateLastSeen(chat)
        } else {
            newChat = Chat(chat.chatID, chat.initiatorUserID, chat.receiverUserID, chat.initiatorLastSeen, lastSeen)
            chatDao.updateLastSeen(chat)
        }

    }

    fun deleteChat(chatID : String) {
        chatDao.deleteChat(chatID)
    }
}