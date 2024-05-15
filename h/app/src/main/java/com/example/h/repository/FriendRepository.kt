package com.example.h.repository

import com.example.h.dao.FriendDAO
import com.example.h.data.Friend

class FriendRepository(private val friendDao : FriendDAO) {

    suspend fun addFriend(friend : Friend) {
        friendDao.addFriend(friend)
    }

    suspend fun getFriendList(userID : String) : List<Friend> {
        return friendDao.getFriendList(userID)
    }

    suspend fun getFriend(userID_1 : String, userID_2 : String) : Friend? {
        return friendDao.getFriend(userID_1, userID_2)
    }

    fun updateFriend(friend : Friend) {
        friendDao.updateFriend(friend)
    }

    fun deleteFriend(friendID : String) {
        friendDao.deleteFriend(friendID)
    }
}