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

    suspend fun getUserByID(userID : String) : User? {

        return userRepository.getUserByID(userID)
    }

    suspend fun getUserByLogin(userEmail : String, userPassword : String) : User? {

        return userRepository.getUserByLogin(userEmail, userPassword)
    }

    fun deleteUser(userID : String) {
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.deleteUser(userID)
        }
    }

    suspend fun isEmailRegistered(email: String): Boolean {
        return userRepository.isEmailRegistered(email)
    }
}