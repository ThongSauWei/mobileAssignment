package com.example.h.repository

import com.example.h.dao.ChatLineDAO
import com.example.h.data.ChatLine

class ChatLineRepository (private val chatLineDao : ChatLineDAO) {

    fun addChatLine(chatLine : ChatLine) {
        chatLineDao.addChatLine(chatLine)
    }

    suspend fun getChatLine(chatID : String) : List<ChatLine> {
        return chatLineDao.getChatLine(chatID)
    }

    fun deleteChatLine(chatLineID : String) {
        chatLineDao.deleteChatLine(chatLineID)
    }
}