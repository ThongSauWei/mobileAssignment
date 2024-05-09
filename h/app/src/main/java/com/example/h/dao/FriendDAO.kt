package com.example.h.dao

import android.util.Log
import com.example.h.data.Friend
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class FriendDAO {
    private val dbRef : DatabaseReference = FirebaseDatabase.getInstance().getReference("Friend")
    private var friendList = ArrayList<Friend>()

    fun addFriend(friend : Friend) {
        dbRef.child(friend.friendID).setValue(friend)
            .addOnCompleteListener{

            }
            .addOnFailureListener{

            }
    }

    suspend fun getFriendList(userID : String) : List<Friend> = withContext(Dispatchers.IO) {
        return@withContext suspendCoroutine { continuation ->

            dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    friendList.clear()
                    if (snapshot.exists()) {
                        for (friendSnapshot in snapshot.children) {
                            val status = friendSnapshot.child("status").getValue(String::class.java)
                            if (status == "Friend") {
                                val friend = friendSnapshot.getValue(Friend::class.java)
                                friendList.add(friend!!)
                            }
                        }

                        val iterator = friendList.iterator()
                        while (iterator.hasNext()) {
                            val friend = iterator.next()
                            if (friend.requestUserID != userID && friend.receiveUserID != userID) {
                                iterator.remove()
                            }
                        }
                    }
                    continuation.resume(friendList)
                }

                override fun onCancelled(error: DatabaseError) {
                    continuation.resumeWithException(error.toException())
                }

            })
        }
    }
}