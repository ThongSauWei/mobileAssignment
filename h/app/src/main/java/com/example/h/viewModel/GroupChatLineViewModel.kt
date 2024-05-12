package com.example.h.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.h.dao.GroupChatLineDAO
import com.example.h.data.GroupChatLine
import com.example.h.repository.GroupChatLineRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GroupChatLineViewModel(application : Application) : AndroidViewModel(application) {
    val groupChatLineRepository : GroupChatLineRepository

    init {
        val groupChatLineDao = GroupChatLineDAO()
        groupChatLineRepository = GroupChatLineRepository(groupChatLineDao)
    }

    fun addGroupChatLine(groupChatLine : GroupChatLine) {
        viewModelScope.launch(Dispatchers.IO) {
            groupChatLineRepository.addGroupChatLine(groupChatLine)
        }
    }

    suspend fun getGroupChatLine(groupID : String) : List<GroupChatLine> {
        return groupChatLineRepository.getGroupChatLine(groupID)
    }

    fun deleteGroupChatLine(groupChatLineID : String) {
        viewModelScope.launch(Dispatchers.IO) {
            groupChatLineRepository.deleteGroupChatLine(groupChatLineID)
        }
    }
}