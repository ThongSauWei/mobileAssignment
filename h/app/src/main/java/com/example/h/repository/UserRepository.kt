package com.example.h.repository

import com.example.h.dao.UserDAO
import com.example.h.data.User
import java.security.MessageDigest

class UserRepository(private val userDao : UserDAO) {

    suspend fun addUser(user: User) {
        // hash password here
        val hashedPassword = hashPassword(user.userPassword) // Hash the password before saving
        user.userPassword = hashedPassword
        userDao.addUser(user)
    }

    suspend fun updateUser(user: User){
        userDao.updateUser(user)
    }

    suspend fun getUserByID(userID: String): User? {
        return userDao.getUserByID(userID)
    }

    suspend fun getUserByLogin(userEmail: String, userPassword: String): User? {
        // hash password here
        val hashedPassword = hashPassword(userPassword)
        return userDao.getUserByLogin(userEmail, hashedPassword)
    }



    fun deleteUser(userID: String) {
        userDao.deleteUser(userID)
    }

    suspend fun isEmailRegistered(email: String): Boolean {
        return userDao.isEmailRegistered(email)
    }

    private fun hashPassword(password: String): String {
        val bytes = password.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("", { str, it -> str + "%02x".format(it) })
    }

    suspend fun getUserByEmail(userEmail: String): User? {
        return userDao.getUserByEmail(userEmail)
    }

    suspend fun searchUser(searchText : String) : List<User> {
        return userDao.searchUser(searchText)
    }

}