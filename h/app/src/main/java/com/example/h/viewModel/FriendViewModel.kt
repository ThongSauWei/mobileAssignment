package com.example.h.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.h.dao.FriendDAO
import com.example.h.data.Friend
import com.example.h.repository.FriendRepository
import com.example.h.saveSharedPreference.SaveSharedPreference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class FriendViewModel(application : Application) : AndroidViewModel(application) {
    private val friendRepository : FriendRepository

    private val _friendList = MutableLiveData<List<Friend>>()
    val friendList : LiveData<List<Friend>> get() = _friendList

    private val userID = SaveSharedPreference.getUserID(application.applicationContext)

    init {
        val friendDao = FriendDAO()
        friendRepository = FriendRepository(friendDao)
        fetchFriendList()
    }

    fun addFriend(friend : Friend) {
        viewModelScope.launch(Dispatchers.IO) {
            friendRepository.addFriend(friend)
        }
    }

    suspend fun getFriendList(userID : String) : List<Friend> {
        return friendRepository.getFriendList(userID)
    }

    suspend fun getFriend(userID_1 : String, userID_2 : String) : Friend? {
        return friendRepository.getFriend(userID_1, userID_2)
    }

    fun updateFriend(friend : Friend) {
        viewModelScope.launch(Dispatchers.IO) {
            friendRepository.updateFriend(friend)
        }
    }

    fun deleteFriend(friendID : String) {
        viewModelScope.launch(Dispatchers.IO) {
            friendRepository.deleteFriend(friendID)
            fetchFriendList()
        }
    }

    private fun fetchFriendList() {
        viewModelScope.launch {
            val newList = friendRepository.getFriendList(userID)
            _friendList.postValue(newList)
        }
    }
}