package com.example.h.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.h.dao.ChatLineDAO
import com.example.h.data.ChatLine
import com.example.h.repository.ChatLineRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChatLineViewModel(application : Application) : AndroidViewModel(application) {
    val chatLineRepository : ChatLineRepository

    init {
        val chatLineDao = ChatLineDAO()
        chatLineRepository = ChatLineRepository(chatLineDao)
    }

    fun addChatLine(chatLine : ChatLine) {
        viewModelScope.launch(Dispatchers.IO) {
            chatLineRepository.addChatLine(chatLine)
        }
    }

    suspend fun getChatLine(chatID : String) : List<ChatLine> {
        return chatLineRepository.getChatLine(chatID)
    }

    fun deleteChatLine(chatLineID : String) {
        viewModelScope.launch(Dispatchers.IO) {
            chatLineRepository.deleteChatLine(chatLineID)
        }
    }
}