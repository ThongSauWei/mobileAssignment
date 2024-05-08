package com.example.h.dao

import android.app.Application
import com.example.h.data.Friend
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue

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

    fun getFriendList(userID : String) : List<Friend> {
        fetchFriendList(userID)

        return friendList
    }

    private fun fetchFriendList(userID : String) {
        dbRef.orderByChild("RequestUserID").equalTo(userID)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (friendSnapshot in snapshot.children) {
                            val status = friendSnapshot.child("status").getValue(String::class.java)
                            if (status == "Friend") {
                                val friend = friendSnapshot.getValue(Friend::class.java)
                                friendList.add(friend!!)
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

        dbRef.orderByChild("ReceiveUserID").equalTo(userID)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (friendSnapshot in snapshot.children) {
                            val status = friendSnapshot.child("status").getValue(String::class.java)
                            if (status == "Friend") {
                                val friend = friendSnapshot.getValue(Friend::class.java)
                                friendList.add(friend!!)
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }
}