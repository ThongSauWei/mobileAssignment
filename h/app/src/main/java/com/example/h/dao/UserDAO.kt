package com.example.h.dao


import com.example.h.data.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class UserDAO {
    private val dbRef : DatabaseReference = FirebaseDatabase.getInstance().getReference("User")

    fun addUser(user : User) {
        user.userID = getNextID()

        dbRef.child(user.userID).setValue(user)
            .addOnCompleteListener{

            }
            .addOnFailureListener{

            }
    }

    suspend fun getUserByID(userID : String) : User? = suspendCoroutine { continuation ->

        dbRef.orderByChild("userID").equalTo(userID)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (userSnapshot in snapshot.children) {
                            val user = userSnapshot.getValue(User::class.java)
                            continuation.resume(user)
                            return
                        }
                    }

                    continuation.resume(null)
                }

                override fun onCancelled(error: DatabaseError) {
                    continuation.resumeWithException(error.toException())
                }

            })
    }

    suspend fun getUserByLogin(userEmail : String, hashedPassword : String) : User? = suspendCoroutine { continuation ->
        dbRef.orderByChild("userEmail").equalTo(userEmail)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (userSnapshot in snapshot.children) {
                            val password = userSnapshot.child("userPassword").getValue(String::class.java)
                            if (password == hashedPassword) {
                                val user = userSnapshot.getValue(User::class.java)
                                continuation.resume(user)
                                return
                            }
                        }
                    }

                    continuation.resume(null)
                }

                override fun onCancelled(error: DatabaseError) {
                    continuation.resumeWithException(error.toException())
                }

            })
    }

    fun deleteUser(userID : String) {
        dbRef.child(userID).removeValue()
            .addOnCompleteListener {

            }
            .addOnFailureListener {

            }
    }

    suspend fun searchUser(searchText : String) : List<User> = withContext(Dispatchers.IO) {
        return@withContext suspendCoroutine { continuation ->

            val userList = ArrayList<User>()

            dbRef.orderByChild("username").startAt(searchText)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            for (userSnapshot in snapshot.children) {
                                val username = userSnapshot.child("username").getValue(String::class.java)!!
                                if (username.startsWith(searchText, true)) {
                                    val user = userSnapshot.getValue(User::class.java)
                                    userList.add(user!!)
                                }
                            }
                        }

                        continuation.resume(userList)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        continuation.resumeWithException(error.toException())
                    }

                })
        }
    }

    private fun getNextID() : String {
        var userID = 100
        dbRef.orderByKey().limitToLast(1)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (userSnapshot in snapshot.children) {
                            val lastUserID = userSnapshot.key!!
                            userID = lastUserID.substring(1).toInt() + 1
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    throw IllegalArgumentException("Database Error")
                }

            })

        return "U$userID"
    }
}