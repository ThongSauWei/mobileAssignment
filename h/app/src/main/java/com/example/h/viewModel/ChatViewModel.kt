package com.example.h.viewModel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.navigation.fragment.findNavController
import com.example.h.dao.ChatDAO
import com.example.h.data.Chat
import com.example.h.repository.ChatRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChatViewModel(application : Application) : AndroidViewModel(application) {
    private val chatRepository : ChatRepository

    init {
        val chatDao = ChatDAO()
        chatRepository = ChatRepository(chatDao)
    }

    fun addChat(chat : Chat) {
        viewModelScope.launch (Dispatchers.IO) {
            chatRepository.addChat(chat)
        }
    }

    suspend fun getChat(userID_1 : String, userID_2 : String) : Chat? {
        return chatRepository.getChat(userID_1, userID_2)
    }

    suspend fun getChatByUser(userID : String) : List<Chat> {
        return chatRepository.getChatByUser(userID)
    }

    fun deleteChat(chatID : String) {
        viewModelScope.launch(Dispatchers.IO) {
            chatRepository.deleteChat(chatID)
        }
    }
}