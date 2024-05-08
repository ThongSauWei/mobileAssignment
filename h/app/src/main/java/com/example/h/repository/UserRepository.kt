package com.example.h.repository

import com.example.h.dao.UserDAO
import com.example.h.data.User

class UserRepository(private val userDao : UserDAO) {

    fun addUser(user : User) {
        // hash password here

        userDao.addUser(user)
    }

    fun getUserByID(userID : String, callback : (User?) -> Unit) {
        userDao.getUserByID(userID) { user ->
            callback(user)
        }
    }

    fun getUserByLogin(userEmail : String, userPassword : String, callback : (User?) -> Unit) {
        // hash password here

        userDao.getUserByLogin(userEmail, userPassword) { user ->
            callback(user)
        }
    }
}