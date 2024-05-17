package com.example.h.repository

import com.example.h.dao.UserGroupDAO
import com.example.h.data.UserGroup

class UserGroupRepository(private val userGroupDao : UserGroupDAO) {

    fun addUserGroup(userGroup : UserGroup) {
        userGroupDao.addUserGroup(userGroup)
    }

    suspend fun getUserGroup(userID : String, groupID : String) : UserGroup? {
        return userGroupDao.getUserGroup(userID, groupID)
    }

    suspend fun getUserGroupByGroup(groupID : String) : List<UserGroup> {
        return userGroupDao.getUserGroupByGroup(groupID)
    }

    suspend fun getUserGroupByUser(userID : String) : List<UserGroup> {
        return userGroupDao.getUserGroupByUser(userID)
    }

    fun updateLastSeen(userGroup : UserGroup) {
        userGroupDao.updateLastSeen(userGroup)
    }

    fun deleteUserGroup(userGroupID : String) {
        userGroupDao.deleteUserGroup(userGroupID)
    }
}