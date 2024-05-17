package com.example.h.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.h.dao.GroupChatLineDAO
import com.example.h.data.GroupChatLine
import com.example.h.repository.GroupChatLineRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GroupChatLineViewModel(application : Application) : AndroidViewModel(application) {
    val groupChatLineRepository : GroupChatLineRepository

    private val _groupChatLineList = MutableLiveData<List<GroupChatLine>>()
    val groupChatLineList : LiveData<List<GroupChatLine>> get() = _groupChatLineList

    init {
        val groupChatLineDao = GroupChatLineDAO()
        groupChatLineRepository = GroupChatLineRepository(groupChatLineDao)
    }

    fun addGroupChatLine(groupChatLine : GroupChatLine) {
        viewModelScope.launch(Dispatchers.IO) {
            groupChatLineRepository.addGroupChatLine(groupChatLine)
            fetchGroupChatLine(groupChatLine.groupID)
        }
    }

    suspend fun fetchGroupChatLine(groupID : String) {
        viewModelScope.launch {
            val newList = groupChatLineRepository.getGroupChatLine(groupID)
            _groupChatLineList.postValue(newList)
        }
    }

    suspend fun getGroupChatLine(groupID : String) : List<GroupChatLine> {
        return groupChatLineRepository.getGroupChatLine(groupID)
    }

    suspend fun getLastGroupChat(groupID : String) : GroupChatLine? {
        return groupChatLineRepository.getLastGroupChat(groupID)
    }

    fun deleteGroupChatLine(groupChatLineID : String, groupID : String) {
        viewModelScope.launch(Dispatchers.IO) {
            groupChatLineRepository.deleteGroupChatLine(groupChatLineID)
            fetchGroupChatLine(groupID)
        }
    }
}