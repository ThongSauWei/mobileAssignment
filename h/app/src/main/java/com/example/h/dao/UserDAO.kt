package com.example.h.dao


import com.example.h.data.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UserDAO {
    private val dbRef : DatabaseReference = FirebaseDatabase.getInstance().getReference("User")

    fun addUser(user : User) {
        dbRef.child(user.userID).setValue(user)
            .addOnCompleteListener{

            }
            .addOnFailureListener{

            }
    }

    fun getUserByID(userID : String, callback : (User?) -> Unit) {
        dbRef.orderByChild("userID").equalTo(userID)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (userSnapshot in snapshot.children) {
                            val user = userSnapshot.getValue(User::class.java)
                            callback(user)
                            return
                        }
                    }

                    callback(null)
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(null)
                }

            })
    }

    fun getUserByEmail(email: String, callback: (User?) -> Unit) {
        dbRef.orderByChild("userEmail").equalTo(email)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (userSnapshot in snapshot.children) {
                            val user = userSnapshot.getValue(User::class.java)
                            callback(user)
                            return
                        }
                    }
                    callback(null)
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(null)
                }
            })
    }

    fun getUserByLogin(userEmail : String, hashedPassword : String, callback: (User?) -> Unit) {
        dbRef.orderByChild("userEmail").equalTo(userEmail)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (userSnapshot in snapshot.children) {
                            val password = userSnapshot.child("userPassword").getValue(String::class.java)
                            if (password == hashedPassword) {
                                val user = userSnapshot.getValue(User::class.java)
                                callback(user)
                                return
                            }
                        }
                    }

                    callback(null)
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(null)
                }

            })
    }
}