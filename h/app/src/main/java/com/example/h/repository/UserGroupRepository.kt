package com.example.h.repository

import com.example.h.dao.UserGroupDAO
import com.example.h.data.UserGroup

class UserGroupRepository(private val userGroupDao : UserGroupDAO) {

    fun addUserGroup(userGroup : UserGroup) {
        userGroupDao.addUserGroup(userGroup)
    }

    suspend fun getUserGroupByGroup(groupID : String) : List<UserGroup> {
        return userGroupDao.getUserGroupByGroup(groupID)
    }

    suspend fun getUserGroupByUser(userID : String) : List<UserGroup> {
        return userGroupDao.getUserGroupByUser(userID)
    }
}