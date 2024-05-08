package com.example.h.repository

import com.example.h.dao.FriendDAO
import com.example.h.data.Friend

class FriendRepository(private val friendDao : FriendDAO) {

    fun addFriend(friend : Friend) {
        friendDao.addFriend(friend)
    }

    fun getFriendList(userID : String) : List<Friend> {
        return friendDao.getFriendList(userID)
    }
}