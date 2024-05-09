package com.example.h.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.h.dao.FriendDAO
import com.example.h.data.Friend
import com.example.h.repository.FriendRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class FriendViewModel(application : Application) : AndroidViewModel(application) {
    private val friendRepository : FriendRepository

    init {
        val friendDao = FriendDAO()
        friendRepository = FriendRepository(friendDao)
    }

    fun addFriend(friend : Friend) {
        viewModelScope.launch(Dispatchers.IO) {
            friendRepository.addFriend(friend)
        }
    }

    suspend fun getFriendList(userID : String) : List<Friend> {
        return friendRepository.getFriendList(userID)
    }
}