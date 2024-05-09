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

    fun getUserByID(userID : String, callback : (User?) -> Unit) {
        userDao.getUserByID(userID) { user ->
            callback(user)
        }
    }

    fun getUserByEmail(email: String): User? {
        var user: User? = null
        userDao.getUserByEmail(email) { retrievedUser ->
            user = retrievedUser
        }
        return user
    }

    fun getUserByLogin(userEmail : String, userPassword : String, callback : (User?) -> Unit) {
        // Hash the password before querying the user
        val hashedPassword = hashPassword(userPassword)

        userDao.getUserByLogin(userEmail, hashedPassword) { user ->
            callback(user)
        }
    }

    private fun hashPassword(password: String): String {
        val bytes = password.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("", { str, it -> str + "%02x".format(it) })
    }
}