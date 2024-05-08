package com.example.h.viewModel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.h.dao.UserDAO
import com.example.h.data.User
import com.example.h.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserViewModel(application : Application) : AndroidViewModel(application) {
    private val userRepository : UserRepository

    init {
        val userDao = UserDAO()
        userRepository = UserRepository(userDao)
    }

    fun addUser(user : User) {
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.addUser(user)
        }
    }

    fun getUserByID(userID : String) : User? {

        var user : User? = null

        userRepository.getUserByID(userID) { retrievedUser ->
            if (user == null) {
                Toast.makeText(getApplication(), "Error getting user", Toast.LENGTH_LONG).show()
            } else {
                user = retrievedUser
            }
        }

        return user
    }

    fun getUserByLogin(userEmail : String, userPassword : String) : User? {

        var user : User? = null

        userRepository.getUserByLogin(userEmail, userPassword) { retrievedUser ->
            if (user == null) {
                Toast.makeText(getApplication(), "Error getting user", Toast.LENGTH_LONG).show()
            } else {
                user = retrievedUser
            }
        }

        return user
    }
}