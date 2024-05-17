package com.example.h.repository

import com.example.h.dao.GroupDAO
import com.example.h.data.Group

class GroupRepository(private val groupDao : GroupDAO) {

    fun addGroup(group : Group) {
        groupDao.addGroup(group)
    }

    suspend fun getGroup(groupID : String) : Group? {
        return groupDao.getGroup(groupID)
    }

    suspend fun searchGroup(searchText : String) : List<Group> {
        return groupDao.searchGroup(searchText)
    }

    fun deleteGroup(groupID : String) {
        groupDao.deleteGroup(groupID)
    }
}