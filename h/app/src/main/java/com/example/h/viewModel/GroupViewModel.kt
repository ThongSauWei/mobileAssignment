package com.example.h.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.h.dao.GroupDAO
import com.example.h.data.Group
import com.example.h.repository.GroupRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GroupViewModel(application : Application) : AndroidViewModel(application) {
    val groupRepository : GroupRepository

    init {
        val groupDao = GroupDAO()
        groupRepository = GroupRepository(groupDao)
    }

    fun addGroup(group : Group) {
        viewModelScope.launch(Dispatchers.IO) {
            groupRepository.addGroup(group)
        }
    }

    suspend fun getGroup(groupID : String) : Group? {
        return groupRepository.getGroup(groupID)
    }

    suspend fun searchGroup(searchText : String) : List<Group> {
        return groupRepository.searchGroup(searchText)
    }

    fun deleteGroup(groupID : String) {
        viewModelScope.launch(Dispatchers.IO) {
            groupRepository.deleteGroup(groupID)
        }
    }
}