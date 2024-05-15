package com.example.h.repository

import com.example.h.dao.UserDAO
import com.example.h.data.User

class UserRepository(private val userDao : UserDAO) {

    suspend fun addUser(user: User) {
        // hash password here

        userDao.addUser(user)
    }

    suspend fun getUserByID(userID: String): User? {
        return userDao.getUserByID(userID)
    }

    suspend fun getUserByLogin(userEmail: String, userPassword: String): User? {
        // hash password here

        return userDao.getUserByLogin(userEmail, userPassword)
    }

    fun deleteUser(userID: String) {
        userDao.deleteUser(userID)
    }

    suspend fun isEmailRegistered(email: String): Boolean {
        return userDao.isEmailRegistered(email)
    }

    suspend fun searchUser(searchText : String) : List<User> {
        return userDao.searchUser(searchText)
    }
}