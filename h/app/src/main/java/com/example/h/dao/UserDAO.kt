package com.example.h.dao


import com.example.h.data.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class UserDAO {
    private val dbRef : DatabaseReference = FirebaseDatabase.getInstance().getReference("User")

    private var nextID = 100
    init {
        GlobalScope.launch {
            nextID = getNextID()
        }
    }

    suspend fun addUser(user : User) {
        user.userID = "U$nextID"
        nextID++

        dbRef.child(user.userID).setValue(user)
            .addOnCompleteListener{

            }
            .addOnFailureListener{

            }.await()
    }

    suspend fun updateUser(user : User) {
        dbRef.child(user.userID).setValue(user)
            .addOnCompleteListener{

            }
            .addOnFailureListener{

            }.await()
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

            dbRef.orderByChild("username")
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

    suspend fun isEmailRegistered(email: String): Boolean = suspendCancellableCoroutine { continuation ->
        dbRef.orderByChild("userEmail").equalTo(email)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // Check if the snapshot contains any data
                    if (snapshot.exists()) {
                        // If the email is found, resume with true
                        continuation.resume(true)
                    } else {
                        // If the email is not found, resume with false
                        continuation.resume(false)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // If there's an error, resume with false
                    continuation.resume(false)
                }
            })
    }

    private suspend fun getNextID() : Int {
        var userID = 100
        val snapshot = dbRef.orderByKey().limitToLast(1).get().await()

        if (snapshot.exists()) {
            for (userSnapshot in snapshot.children) {
                val lastUserID = userSnapshot.key!!
                userID = lastUserID.substring(1).toInt() + 1
            }
        }

        return userID
    }

}