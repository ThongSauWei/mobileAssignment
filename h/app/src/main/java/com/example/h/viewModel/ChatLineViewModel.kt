package com.example.h.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.h.dao.ChatLineDAO
import com.example.h.data.ChatLine
import com.example.h.repository.ChatLineRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChatLineViewModel(application : Application) : AndroidViewModel(application) {
    val chatLineRepository : ChatLineRepository

    private val _chatLineList = MutableLiveData<List<ChatLine>>()
    val chatLineList : LiveData<List<ChatLine>> get() = _chatLineList

    private val _lastChatLineList = MutableLiveData<List<ChatLine>>()
    val lastChatLineList : LiveData<List<ChatLine>> get() = _lastChatLineList

    init {
        val chatLineDao = ChatLineDAO()
        chatLineRepository = ChatLineRepository(chatLineDao)
    }

    fun addChatLine(chatLine : ChatLine) {
        viewModelScope.launch(Dispatchers.IO) {
            chatLineRepository.addChatLine(chatLine)
            getChatLine(chatLine.chatID)
        }
    }

    fun getChatLine(chatID : String) {
        viewModelScope.launch {
            val newList = chatLineRepository.getChatLine(chatID)
            _chatLineList.postValue(newList)
        }
    }

    suspend fun getLastChat(chatID : String) : ChatLine? {
        return chatLineRepository.getLastChat(chatID)
    }

    fun fetchLastChatList(chatIDList : List<String>) {
        viewModelScope.launch {
            val newList = mutableListOf<ChatLine>()
            chatIDList.forEach {
                val chatLine = getLastChat(it)
                newList.add(chatLine!!)
            }
            _lastChatLineList.postValue(newList)

        }
    }

    fun deleteChatLine(chatLineID : String, chatID : String) {
        viewModelScope.launch(Dispatchers.IO) {
            chatLineRepository.deleteChatLine(chatLineID)
            getChatLine(chatID)
        }
    }
}