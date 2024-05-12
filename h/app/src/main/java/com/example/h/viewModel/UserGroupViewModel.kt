package com.example.h.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.h.dao.UserGroupDAO
import com.example.h.data.UserGroup
import com.example.h.repository.UserGroupRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserGroupViewModel(application : Application) : AndroidViewModel(application) {
    val userGroupRepository : UserGroupRepository

    init {
        val userGroupDao = UserGroupDAO()
        userGroupRepository = UserGroupRepository(userGroupDao)
    }

    fun addUserGroup(userGroup : UserGroup) {
        viewModelScope.launch(Dispatchers.IO) {
            userGroupRepository.addUserGroup(userGroup)
        }
    }

    suspend fun getUserGroupByGroup(groupID : String) : List<UserGroup> {
        return userGroupRepository.getUserGroupByGroup(groupID)
    }

    suspend fun  getUserGroupByUser(userID : String) : List<UserGroup> {
        return userGroupRepository.getUserGroupByUser(userID)
    }

    fun deleteUserGroup(userGroupID : String) {
        viewModelScope.launch(Dispatchers.IO) {
            userGroupRepository.deleteUserGroup(userGroupID)
        }
    }
}