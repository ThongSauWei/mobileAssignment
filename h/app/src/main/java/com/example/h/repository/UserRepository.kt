package com.example.h.repository

import com.example.h.dao.UserDAO
import com.example.h.data.User
import java.security.MessageDigest

class UserRepository(private val userDao : UserDAO) {

    fun addUser(user : User) {
        // Hash the password before adding the user
        val hashedPassword = hashPassword(user.userPassword)
        val userWithHashedPassword = User(
            user.userID,
            user.username,
            user.userEmail,
            user.userMobileNo,
            user.userDOB,
            hashedPassword,
            user.userSecurityQuestion
        )

        userDao.addUser(userWithHashedPassword)
    }

    suspend fun getUserByID(userID : String) : User? {
        return userDao.getUserByID(userID)
    }

    suspend fun getUserByEmail(email: String): User? {
        var user: User? = null
        userDao.getUserByEmail(email) { retrievedUser ->
            user = retrievedUser
        }
        return user
    }

   suspend fun getUserByLogin(userEmail : String, userPassword : String, callback : (User?) -> Unit) {
        // Hash the password before querying the user
        val hashedPassword = hashPassword(userPassword)

        return userDao.getUserByLogin(userEmail, userPassword)
    }

    private fun hashPassword(password: String): String {
        val bytes = password.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("", { str, it -> str + "%02x".format(it) })
    }
}